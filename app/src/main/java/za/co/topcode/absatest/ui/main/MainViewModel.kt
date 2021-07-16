package za.co.topcode.absatest.ui.main

import android.content.Context
import androidx.lifecycle.*
import za.co.topcode.absatest.model.ToDoModel
import za.co.topcode.absatest.repository.ToDoRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(): ViewModel() {
    lateinit var todoList: LiveData<List<ToDoModel>>
    private val todoListData = MediatorLiveData<List<ToDoModel>>()
    val titleError = MutableLiveData<Any>()
    val descriptionError = MutableLiveData<Any>()
    val dateError = MutableLiveData<Any>()
    val timeError = MutableLiveData<Any>()
    val dateTimeError = MutableLiveData<Any>()
    val addNewSuccess = MutableLiveData<Any>()
    val updateSuccess = MutableLiveData<Boolean>()
    val deleteSuccess = MutableLiveData<Any>()
    val isFragmentPopped = MutableLiveData<Boolean>()
    val emptyListEvent = MutableLiveData<Any>()
    val nonEmptyListEvent = MutableLiveData<Any>()
    val completedRatioEvent = MutableLiveData<String>()
    private val df = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH)

    fun fragmentPopped(){
        isFragmentPopped.value = true
    }

    fun getTodoListData(): LiveData<List<ToDoModel>> {
        return todoListData
    }

    fun getTodoList(context: Context) {
        todoList = ToDoRepository.getTodoList(context)

        todoListData.addSource(todoList) { resultList ->
            todoListData.value = resultList
            if (resultList.isEmpty()) {
                emptyListEvent.value = Any()
            } else {
                nonEmptyListEvent.value = Any()
            }
            val total = resultList.size
            var completed = 0
            resultList.forEach {
                if(it.Complete) {
                    completed += 1
                }
            }
            completedRatioEvent.value = "$completed/$total"

        }
    }

    fun toDoStatusToggle(context: Context, item: ToDoModel, complete: Boolean) {
        item.Complete = complete
        ToDoRepository.updateData(context, item)
        updateSuccess.value = complete

    }

    fun deleteTodo(context: Context, item: ToDoModel) {
        ToDoRepository.deleteData(context, item)
        deleteSuccess.value = Any()
    }

    fun addNewTodo(context: Context, title: String?, description: String?, date: String?, time: String?) {
        when {
            title.isNullOrEmpty() -> {
                titleError.value = Any()
            }
            description.isNullOrEmpty() -> {
                descriptionError.value = Any()
            }
            date.isNullOrEmpty()-> {
                dateError.value = Any()
            }
            time.isNullOrEmpty() -> {
                timeError.value = Any()
            }
            else -> {
                try {
                    val dueDate = df.parse("$date $time")
                    ToDoRepository.insertData(context, title, description, dueDate)
                    addNewSuccess.value = Any()
                } catch (e: ParseException) {
                    e.printStackTrace()
                    dateTimeError.value = Any()
                }

            }
        }


    }


}