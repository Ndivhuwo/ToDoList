package za.co.topcode.absatest.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import za.co.topcode.absatest.model.ToDoModel
import za.co.topcode.absatest.util.DateConverter

@Database(entities = [ToDoModel::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun toDoDao() : DAOAccess

    companion object {

        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        fun getDataseClient(context: Context) : ToDoDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, ToDoDatabase::class.java, "TODO_DATABASE")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }

    }

}