package za.co.topcode.absatest.notification

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import za.co.topcode.absatest.R
import javax.inject.Inject

class NotificationHandler {

    companion object {
        val TAG: String = NotificationHandler::class.java.simpleName
        const val NOTIFICATION_CHANNEL_ID : String = "default"
        const val NOTIFICATION_CHANNEL_NAME : String = "test_default_channel"
        const val NOTIFICATION_ID : Int = 8878
        const val INTENT_ACTION_NOTIFICATION: String = "za.co.topcode.absatest.notification"
        const val REQUEST_CODE_NOTIFICATION_INTENT = 2

        fun sendNotification(context: Context, bundle: Bundle?, toActivity: Class<*>, title: String?,
                             subject: String?, message: String?, notificationSound: Boolean, pendingIntentFlag: Int) {
            Log.i(TAG, "Sending Notification")
            val intent = Intent(context, toActivity)
            intent.action = INTENT_ACTION_NOTIFICATION
            bundle?.let {
                Log.i(TAG, "Sending Bundle")
                intent.putExtras(it)
            }

            val pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE_NOTIFICATION_INTENT,
                intent, pendingIntentFlag, bundle)

            pendingIntent?.let {
                Log.i(TAG, "Pending Intent present")
            }

            val trimmedTitle = Html.fromHtml(title).toString()
            val trimmedSubject = Html.fromHtml(subject).toString()
            val trimmedMessage = Html.fromHtml(message).toString()
            val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
                setContentText(trimmedSubject)
                setContentTitle(trimmedTitle)
                setStyle(NotificationCompat.BigTextStyle().bigText(trimmedMessage))
                setContentIntent(pendingIntent)
                setSmallIcon(R.drawable.checkbox_icon)
                setAutoCancel(true)
                setChannelId(NOTIFICATION_CHANNEL_ID)
            }

            val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                channel.description = message
                if (notificationSound) {
                    channel.setSound(defaultSoundUri, audioAttributes)
                    channel.enableLights(true)
                    channel.lightColor = Color.CYAN
                    channel.enableVibration(true)
                }
                manager.createNotificationChannel(channel)
            } else {
                //notificationBuilder.priority = NotificationCompat.PRIORITY_MAX
                if (notificationSound) {
                    notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND or Notification.FLAG_SHOW_LIGHTS)
                    notificationBuilder.setLights(context.resources.getColor(R.color.grey), 300, 100)
                }
            }

            val notification = notificationBuilder.build()

            manager.notify(NOTIFICATION_ID, notification)
        }
    }


}