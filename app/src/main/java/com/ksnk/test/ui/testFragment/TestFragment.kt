package com.ksnk.test.ui.testFragment


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.ksnk.test.utils.Contains
import com.ksnk.test.R
import com.ksnk.test.ui.main.MainActivity


class TestFragment : Fragment() {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    private val description = "Test notification"
    private var buttonNotification: Button? = null
    private var notId: Int? = 0
    private var intent: Intent? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.test_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        notId = requireArguments().getInt(Contains().argumentId)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notId = requireArguments().getInt(Contains().argumentId)
        buttonNotification = view.findViewById(R.id.notButton)
        buttonNotification?.transformationMethod=null
        notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        buttonNotification?.setOnClickListener {
            val contentView = RemoteViews(context?.packageName, R.layout.notification)
            contentView.setTextViewText(R.id.i_started_d, "Notification $notId")
            intent = Intent(context, MainActivity::class.java)
            intent!!.action = notId.toString()
            intent!!.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationNewVersionApi(pendingIntent, contentView)
            } else {
                createNotificationOldVersionApi(pendingIntent)
            }
        }
    }

    private fun createNotificationOldVersionApi(pendingIntent: PendingIntent) {
        val builder = Notification.Builder(context)
            .setSmallIcon(R.drawable.avatar_png)
            .setLargeIcon(
                (BitmapFactory.decodeResource(resources, R.drawable.avatar_png))
            )
            .setContentText(getString(R.string.notification_title))
            .setSubText("Notification $notId")
            .setContentIntent(pendingIntent)
        notificationManager.notify(notId!!, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationNewVersionApi(
        pendingIntent: PendingIntent,
        contentView: RemoteViews
    ) {
        notificationChannel =
            NotificationChannel(
                notId.toString(),
                description,
                NotificationManager.IMPORTANCE_HIGH
            )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)

        val builder = NotificationCompat.Builder(requireContext(), notId.toString())
            .setContentText(getString(R.string.notification_mes))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(contentView)
            .setContentIntent(pendingIntent)
        notificationManager.notify(notId!!, builder.build())
    }
}