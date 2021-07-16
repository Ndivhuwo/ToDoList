package za.co.topcode.absatest.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import za.co.topcode.absatest.databinding.TodoListItemBinding
import za.co.topcode.absatest.model.ToDoModel
import java.text.SimpleDateFormat
import java.util.*

class ToDoListAdapter(private val onItemClickListener: (item: ToDoModel) -> Unit, private val onItemDeleteListener: (item: ToDoModel) -> Unit, private val onItemStatusToggleListener: (item: ToDoModel, complete: Boolean) -> Unit) : ListAdapter<ToDoModel, ToDoListAdapter.ToDoListViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoListViewHolder {
        val binding = TodoListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        return ToDoListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ToDoListViewHolder(
            private val binding: TodoListItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat(
            "EEE d MMM, yyyy h:mm a", Locale.ENGLISH
        )

        fun bind(item: ToDoModel) {
            binding.apply {

                todoTitle.text = item.Title
                todoDueDate.text = dateFormat.format(item.DueDate)
                statusToggle.isChecked = item.Complete
                deleteButton.setOnClickListener {
                    onItemDeleteListener(getItem(adapterPosition))
                }

                statusToggle.setOnCheckedChangeListener { compoundButton, checked ->
                    onItemStatusToggleListener(getItem(adapterPosition), checked)
                }
                root.setOnClickListener {
                    onItemClickListener(getItem(adapterPosition))
                }

            }
        }
    }

    class DiffCallBack : DiffUtil.ItemCallback<ToDoModel>() {

        override fun areItemsTheSame(oldItem: ToDoModel, newItem: ToDoModel): Boolean {
            return oldItem.Id == newItem.Id
        }

        override fun areContentsTheSame(oldItem: ToDoModel, newItem: ToDoModel): Boolean {
            return oldItem == newItem
        }

    }
}