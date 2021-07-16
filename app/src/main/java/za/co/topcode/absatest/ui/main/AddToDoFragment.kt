package za.co.topcode.absatest.ui.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import za.co.topcode.absatest.R
import za.co.topcode.absatest.databinding.AddToDoFragmentBinding
import za.co.topcode.absatest.extension.hideKeyboard
import java.util.*

@AndroidEntryPoint
class AddToDoFragment : Fragment(R.layout.add_to_do_fragment){

    companion object {
        val TAG: String = AddToDoFragment::class.java.simpleName
    }
    private var _binding: AddToDoFragmentBinding? = null
    private var date: String? = null
    private var time: String? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    val now: Calendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AddToDoFragmentBinding.bind(view)
        activity?.actionBar?.title = "Add TODO"
        binding.apply {
            dueDate.setOnClickListener {
                openDateSelector()
            }

            dueDate.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
                if(hasFocus) {
                    openDateSelector()
                    dueDate.hideKeyboard()
                }
            }

            dueTime.setOnClickListener {
                openTimePicker()
            }

            dueTime.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
                if(hasFocus) {
                    openTimePicker()
                    dueTime.hideKeyboard()
                }
            }

            addTodoButton.setOnClickListener {
                viewModel.addNewTodo(requireContext(), txtTitle.text.toString(), txtDescription.text.toString(), date, time)
            }
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.titleError.observe(viewLifecycleOwner, {
            showToast("Please enter the title")
        })

        viewModel.descriptionError.observe(viewLifecycleOwner, {
            showToast("Please enter the description")
        })

        viewModel.dateError.observe(viewLifecycleOwner, {
            showToast("Please select the date")
        })

        viewModel.timeError.observe(viewLifecycleOwner, {
            showToast("Please select the time")
        })

        viewModel.dateTimeError.observe(viewLifecycleOwner, {
            showToast("Invalid date and time")
        })

        viewModel.addNewSuccess.observe(viewLifecycleOwner, {
            showToast("TODO dded successfully")
            viewModel.fragmentPopped()
            NavHostFragment.findNavController(this).popBackStack()
        })
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun openDateSelector() {
        Log.i(TAG, "onClickDate")
        val now = Calendar.getInstance()
        var dates: Array<String>? = null
        if (date != null) {
            dates = date!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        val datePickerDialog = DatePickerDialog(
            requireContext(), onDateSetListener,
            if (dates != null) Integer.parseInt(dates[0]) else now.get(Calendar.YEAR),
            if (dates != null) Integer.parseInt(dates[1]) - 1 else now.get(Calendar.MONTH),
            if (dates != null) Integer.parseInt(dates[2]) else now.get(Calendar.DAY_OF_MONTH)
        )
        Log.i(TAG, " - Select Date")

        datePickerDialog.show()
    }

    private fun openTimePicker() {
        Log.i(TAG, "onClickTime")
        val now = Calendar.getInstance()
        var times: Array<String>? = null
        if (time != null) {
            times = time!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        val timePickerDialog = TimePickerDialog(
            requireContext(), onTimeSetListener,
            if (times != null) Integer.parseInt(times[0]) else now.get(Calendar.HOUR),
            if (times != null) Integer.parseInt(times[1]) else now.get(Calendar.MINUTE),
            true
        )
        Log.i(TAG, " - SelectTime")

        timePickerDialog.show()
    }

    private val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        Log.i(TAG, "Selected Date, y: " + year + " m: " + (monthOfYear + 1) + " d: " + dayOfMonth)
        val month = (monthOfYear + 1)
        date = "$year/$month/$dayOfMonth"
        binding.dueDate.setText(date)
    }

    private val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        Log.i(TAG, "Selected Time, h: $hourOfDay m: $minute")
        var min = "$minute"
        if(min.length == 1) {
            min = "0$min"
        }
        time = "$hourOfDay:$min"
        binding.dueTime.setText(time)
    }

}