package za.co.topcode.absatest.room

import androidx.lifecycle.LiveData
import androidx.room.*
import za.co.topcode.absatest.model.ToDoModel

@Dao
interface DAOAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(toDoTableModel: ToDoModel)

    @Query("SELECT * FROM ToDo")
    fun getTodoList() : LiveData<List<ToDoModel>>

    @Query("SELECT * FROM ToDo")
    fun getTodoListStraight() : List<ToDoModel>

    @Update
    suspend fun updateToDo(toDoTableModel: ToDoModel)

    @Delete
    suspend fun deleteToDo(toDoTableModel: ToDoModel)

}