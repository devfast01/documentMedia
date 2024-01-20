package com.example.documentmedia

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter


class DescriptionAdapter(private val context: Context) :
    MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): String {
        return "Now playing"
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    override fun getCurrentContentText(player: Player): String? {
        return "Streaming audio"
    }


    override fun getCurrentSubText(player: Player): String? {
        return null
    }


    override fun getCurrentLargeIcon(player: Player, callback: BitmapCallback): Bitmap? {
        return null
    }
}