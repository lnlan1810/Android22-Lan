package com.example.musicapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.musicapp.MainActivity
import com.example.musicapp.R
import com.example.musicapp.repository.MusicRepository

private const val CHANNEL_ID = "channel_id_2"

class NotificationService(
    private val context: Context
) {

    private var builder: NotificationCompat.Builder

    private val manager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_title),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_desc)

            }.also {
                manager.createNotificationChannel(it)
            }
        }

        val previous = Intent(context, MusicService::class.java).apply {
            action = "PREVIOUS"
        }

        val pause = Intent(context, MusicService::class.java).apply {
            action = "PAUSE"
        }

      //  val play = Intent(context, MusicService::class.java).apply {
     //       action = "PLAY"
      //  }

        val next = Intent(context, MusicService::class.java).apply {
            action = "NEXT"
        }

        //val playIntent = PendingIntent.getService(
        //    context,
        //    0,
        //    play,
        //    0
        //)

        val prevIntent = PendingIntent.getService(
            context,
            0,
            previous,
            0
        )

        val pauseIntent = PendingIntent.getService(
            context,
            1,
            pause,
            0
        )

        val nextIntent = PendingIntent.getService(
            context,
            2,
            next,
            0
        )
        builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .addAction(R.drawable.ic_skip_previous, "PREVIOUS", prevIntent)
            .addAction(R.drawable.ic_pause, "PAUSE", pauseIntent)
            .addAction(R.drawable.ic_skip_next, "NEXT", nextIntent)
    }

    fun setNotification(currentTrackId:Int) {
        val music = MusicRepository.musics[currentTrackId]
        val icon = BitmapFactory.decodeResource(context.resources,music.cover)

        val bundle = Bundle().apply {
            putInt("idSong", currentTrackId)
        }

        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.my_nav)
            .setDestination(R.id.fragment_detail)
            .setArguments(bundle)
            .createPendingIntent()

        val updBuilder = builder
            .setContentTitle(music.title)
            .setContentText(music.author)
            .setLargeIcon(icon)
            .setContentIntent(pendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setShowWhen(false)
            .setAutoCancel(false)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        manager.notify(1, updBuilder.build())
    }
}
