package za.co.topcode.absatest

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import za.co.topcode.absatest.job.ToDoReminderJob
import za.co.topcode.absatest.ui.main.AddToDoFragment
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setupAlarm()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        navController= findNavController(R.id.todo_nav_host_fragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setupAlarm() {
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val builder = JobInfo.Builder(
            1,
            ComponentName(
                packageName,
                ToDoReminderJob::class.java.name
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //builder.setMinimumLatency(3000)
            Log.i(TAG, "-----Start jobbo ${Build.VERSION_CODES.N} and up")
            builder.setPeriodic(1000*60*30)
        } else {
            Log.i(TAG, "-----Start jobbo")
            builder.setPeriodic(1000*60*30)
        }
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)

        if (jobScheduler.schedule(builder.build()) <= 0) {
            Log.e(TAG, "onCreate: Some error while scheduling the job")
        }
    }
}