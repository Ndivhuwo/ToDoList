package za.co.topcode.absatest.repository

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import za.co.topcode.absatest.model.ToDoModel
import za.co.topcode.absatest.room.ToDoDatabase
import java.util.*

class ToDoRepository {

    companion object {

        var toDoDatabase: ToDoDatabase? = null

        var todoList: LiveData<List<ToDoModel>>? = null

        fun initializeDB(context: Context) : ToDoDatabase {
            return ToDoDatabase.getDataseClient(context)
        }

        fun insertData(context: Context, title: String, description: String, dueDate: Date) {

            toDoDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                val todoDetails = ToDoModel(false, title, description, dueDate)
                toDoDatabase!!.toDoDao().insertData(todoDetails)
            }

        }

        fun updateData(context: Context, toDoItem: ToDoModel) {

            toDoDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                toDoDatabase!!.toDoDao().updateToDo(toDoItem)
            }

        }

        fun deleteData(context: Context, toDoItem: ToDoModel) {

            toDoDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                toDoDatabase!!.toDoDao().deleteToDo(toDoItem)
            }

        }

        fun getTodoList(context: Context) : LiveData<List<ToDoModel>> {

            toDoDatabase = initializeDB(context)

            //todoList = toDoDatabase!!.toDoDao().getTodoList()

            return toDoDatabase!!.toDoDao().getTodoList()
        }

    }
}