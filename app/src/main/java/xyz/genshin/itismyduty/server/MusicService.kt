package xyz.genshin.itismyduty.server

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.utils.Tools


class MusicService : MediaBrowserServiceCompat() {

    companion object{
        private const val MY_MEDIA_ROOT_ID = "genshin_music"
        private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"
        private const val TEST_MUSIC = "https://genshin.itismyduty.xyz/Music/Beckoning.mp3"
        private const val PACKAGE_NAME = "xyz.genshin.itismyduty"
    }

    private var mediaSession: MediaSessionCompat? =null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private val builder = NotificationCompat.Builder(baseContext, "1").apply {


    }
    private var mMediaPlayer: MediaPlayer? = null

    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_PLAY_PAUSE)

            setPlaybackState(stateBuilder.build())

            setCallback(mSessionCallback)

            setSessionToken(sessionToken)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return if (clientPackageName == PACKAGE_NAME){
            BrowserRoot(MY_MEDIA_ROOT_ID, null)
        }else {
            BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
        }

    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {

        if (parentId == MY_MEDIA_ROOT_ID){
            val musicList = ArrayList<MediaBrowserCompat.MediaItem>()
            val desc = MediaDescriptionCompat.Builder()
                .setMediaId(MY_MEDIA_ROOT_ID)
                .setMediaUri(Uri.parse("https://genshin.itismyduty.xyz/Music/Beckoning.mp3"))
                .setIconUri(Uri.parse("https://genshin.itismyduty.xyz/music.jpg"))
                .setTitle("Beckoning")
                .setSubtitle("陈")
                .build()
            val item = MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
            musicList.add(item)
            result.sendResult(musicList)
        }

    }

    private fun setPlayMusic(){

        val mMediaPlayer = MediaPlayer()
        mMediaPlayer.setDataSource(TEST_MUSIC)
        mMediaPlayer.prepareAsync()
        mMediaPlayer.setOnPreparedListener{
            mMediaPlayer.start()
        }
        //startForeground(1, builder.build())

    }

    private val mSessionCallback = object : MediaSessionCompat.Callback(){

        override fun onPlay() {
            startService(Intent(baseContext, MusicService::class.java))
            if (mMediaPlayer == null){
                mMediaPlayer = MediaPlayer()
                mMediaPlayer!!.setDataSource(TEST_MUSIC)
                mMediaPlayer!!.prepareAsync()
                mMediaPlayer!!.setOnPreparedListener{
                    mMediaPlayer!!.start()
                }
                val builder = NotificationCompat.Builder(this@MusicService, "1")
                builder.setContentTitle("33")
                startForeground(1, builder.build())
            }else{
                mMediaPlayer!!.start()
            }

            stateBuilder = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0,
                    SystemClock.elapsedRealtime().toFloat()
                )
            mediaSession?.setPlaybackState(stateBuilder.build())
        }

        override fun onPause() {
            mMediaPlayer?.pause()

            stateBuilder = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED, 0,
                    SystemClock.elapsedRealtime().toFloat()
                )
            mediaSession?.setPlaybackState(stateBuilder.build())
        }
    }



}
