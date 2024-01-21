package com.example.documentmedia

import android.R.attr.bitmap
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager


@Suppress("DEPRECATION")
class MyService : Service() {
    private lateinit var exoPlayer: SimpleExoPlayer
    private var myBinder = MyBinder()
    private var mediaSession: MediaSessionCompat? = null

    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var trackSelector: DefaultTrackSelector
    private var notificationId = 123
    private var channelId = "channelId"
    private var UrlLink = ""

    override fun onCreate() {
        super.onCreate()

        trackSelector = DefaultTrackSelector(this)
        exoPlayer = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
        trackSelector.setParameters(
            trackSelector
                .buildUponParameters()
                .setMaxVideoBitrate(0)
        )

    }

    /**
     * This class will be what is returned when an activity binds to this service.
     * The activity will also use this to know what it can get from our service to know
     * about the video playback.
     */
    inner class VideoServiceBinder : Binder() {

        /**
         * This method should be used only for setting the exoplayer instance.
         * If exoplayer's internal are altered or accessed we can not guarantee
         * things will work correctly.
         */
        fun getExoPlayerInstance() = exoPlayer
        fun getService(): MyService = this@MyService
    }

    override fun onBind(intent: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext, "Music")
        return myBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val audioUrl = intent?.getStringExtra("audioUrl")
        if (audioUrl != null) {
            playAudio(audioUrl)
        }
        return START_NOT_STICKY
    }

    private fun playAudio(audioUrl: String) {
        val context = this
        val mediaItem = MediaItem.fromUri(audioUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        exoPlayer.addListener(object : Player.EventListener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (!isPlaying) {
                    stopForeground( /* removeNotification= */false)
                }
            }
        })

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.music_cover_placeholder)

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            this,
            channelId,
            R.string.app_name,
            R.string.app_name,
            notificationId,
            object : PlayerNotificationManager.MediaDescriptionAdapter {

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    // return pending intent
                    val intent = Intent(context, MainActivity::class.java)
                    return PendingIntent.getActivity(
                        context, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                //pass description here
                override fun getCurrentContentText(player: Player): String? {
                    return "ExoPlayer PIP example"
                }

                //pass title (mostly playing audio name)
                override fun getCurrentContentTitle(player: Player): String {
                    return "ExoPlayer PIP example Video"
                }

                // pass image as bitmap
                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback,
                ): Bitmap? {
                    return bitmap
                }
            },
            object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    onGoing: Boolean,
                ) {
                    startForeground(notificationId, notification)
                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean,
                ) {
                    super.onNotificationCancelled(notificationId, dismissedByUser)
                    stopSelf()
                }
            }
        )

        //attach player to playerNotificationManager
        playerNotificationManager.setPlayer(exoPlayer) // this line brings the player to the notification bar
        playerNotificationManager.setUseStopAction(true)
        playerNotificationManager.setFastForwardIncrementMs(0)
        playerNotificationManager.setRewindIncrementMs(0)
        playerNotificationManager.setSmallIcon(R.drawable.music_cover_placeholder)
    }

//    val playerNotificationManagerBuilder =
//        PlayerNotificationManager.Builder(this, 12, "CHANNEL_ID_12")
//    playerNotificationManagerBuilder.setMediaDescriptionAdapter(mediaDescriptorInstance)
//    val playerNotificationManager = playerNotificationManagerBuilder.build()
//
//    playerNotificationManager.setPlayer(exoPlayer)
//
//    private var mediaDescriptorInstance: MediaDescriptionAdapter = object : MediaDescriptionAdapter {
//        override fun getCurrentContentTitle(player: Player): CharSequence {
//            return "<Media Main Title>"
//        }
//
//        override fun createCurrentContentIntent(player: Player): PendingIntent? {
//            return null
//        }
//
//        override fun getCurrentContentText(player: Player): CharSequence? {
//            return "<Media sub-Title>"
//        }
//
//        override fun getCurrentLargeIcon(player: Player, callback: BitmapCallback): Bitmap? {
//            // Icon to display
//            return null
////            return bitmap
//        }
//    }


    inner class MyBinder : Binder() {
        fun currentService(): MyService {
            return this@MyService
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

}