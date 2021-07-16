package za.co.topcode.absatest.job

import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.topcode.absatest.MainActivity
import za.co.topcode.absatest.notification.NotificationHandler
import za.co.topcode.absatest.repository.ToDoRepository
import java.util.*

class ToDoReminderJob : JobService() {
    private val calendar: Calendar =
        Calendar.getInstance(TimeZone.getTimeZone("Africa/Johannesburg"))
    private val calendar2: Calendar =
        Calendar.getInstance(TimeZone.getTimeZone("Africa/Johannesburg"))


    override fun onStartJob(params: JobParameters): Boolean {
        Log.i(TAG, "-------Job Started")
        calendar.time = Date()
        calendar2.time = Date()
        calendar.add(Calendar.HOUR, 1)
        calendar2.add(Calendar.MINUTE, 30)
        CoroutineScope(Dispatchers.IO).launch {
            val list = ToDoRepository.toDoDatabase!!.toDoDao().getTodoListStraight()
            list.forEach {
                Log.i(TAG, "---------${it.Title}")
                if(it.DueDate.after(calendar2.time) && it.DueDate.before(calendar.time)) {
                    NotificationHandler.sendNotification(applicationContext, null,
                        MainActivity::class.java, it.Title, "You have a TODO Item Due",
                        it.Description, true, PendingIntent.FLAG_UPDATE_CURRENT)
                }
            }
        }
        //jobFinished(params, false)
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true
    }

    companion object {
        val TAG: String = ToDoReminderJob::class.java.simpleName
    }
}