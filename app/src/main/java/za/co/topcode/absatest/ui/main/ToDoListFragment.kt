package za.co.topcode.absatest.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import za.co.topcode.absatest.R
import za.co.topcode.absatest.databinding.ToToListFragmentBinding
import za.co.topcode.absatest.model.ToDoModel

@AndroidEntryPoint
class ToDoListFragment : Fragment(R.layout.to_to_list_fragment) {
    private var _binding: ToToListFragmentBinding? = null
    private var _toDoListAdapter: ToDoListAdapter? = null
    private val binding get() = _binding!!
    private val toDoListAdapter get() = _toDoListAdapter!!
    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ToToListFragmentBinding.bind(view)
        _toDoListAdapter = ToDoListAdapter(::onItemClickListener, ::onItemDeleteListener, ::onItemStatusToggleListener)
        activity?.actionBar?.title = "TODO List"
        binding.apply {
            todoList.apply {
                adapter = toDoListAdapter
                layoutManager = LinearLayoutManager(requireActivity())
                setHasFixedSize(true)
            }

            empty.addNew.setOnClickListener {
                navigateToAddNewToDo()
            }

            addNewToDo.setOnClickListener {
                navigateToAddNewToDo()
            }
        }

        setupObservers()
    }

    private fun setupObservers() {
        getToDoList()
        viewModel.updateSuccess.observe(viewLifecycleOwner, {
            if(it){
                showToast("Item completed")
            } else {
                showToast("Item in progress")
            }
            getToDoList()
        })

        viewModel.deleteSuccess.observe(viewLifecycleOwner, {
            showToast("Deleted successfully")
            getToDoList()
        })

        viewModel.isFragmentPopped.observe(viewLifecycleOwner, {
            if(it) {
                getToDoList()
            }
        })

        viewModel.emptyListEvent.observe(viewLifecycleOwner, {
            binding.addNewToDo.visibility = View.GONE
            binding.empty.EmptyResult.visibility = View.VISIBLE
        })

        viewModel.nonEmptyListEvent.observe(viewLifecycleOwner, {
            binding.empty.EmptyResult.visibility = View.GONE
            binding.addNewToDo.visibility = View.VISIBLE
        })

        viewModel.getTodoListData().observe(viewLifecycleOwner, {
            toDoListAdapter.submitList(it)
            binding.progressBar.visibility = View.GONE
        })

        viewModel.completedRatioEvent.observe(viewLifecycleOwner, {
            binding.completedRatio.text = "Completed $it"
        })
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun getToDoList() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getTodoList(requireContext())
    }

    private fun navigateToAddNewToDo() {
        val action=ToDoListFragmentDirections
            .actionToDoListFragmentToAddToDoFragment()

        NavHostFragment.findNavController(this).navigate(
            action
        )
    }

    private fun onItemDeleteListener(item: ToDoModel) {
        viewModel.deleteTodo(requireContext(), item)
    }

    private fun onItemClickListener(item: ToDoModel) {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }
        builder?.setMessage(item.Description)
            ?.setTitle(item.Title)
            ?.setNegativeButton(android.R.string.ok, null)
            ?.show()

    }

    private fun onItemStatusToggleListener(item: ToDoModel, complete: Boolean) {

        viewModel.toDoStatusToggle(requireContext(), item, complete)
    }
}