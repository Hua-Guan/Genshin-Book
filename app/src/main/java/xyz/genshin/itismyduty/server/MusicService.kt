package xyz.genshin.itismyduty.server

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.utils.Tools
import android.app.ActivityManager
import android.content.Context
import android.content.IntentFilter
import android.media.MediaMetadata
import android.net.ConnectivityManager
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import xyz.genshin.itismyduty.model.broadcast.MusicLatestPositionBroadcastReceiver
import java.util.*
import kotlin.collections.ArrayList


class MusicService : MediaBrowserServiceCompat() {

    companion object{
        private const val MY_MEDIA_ROOT_ID = "genshin_music"
        private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"
        private const val TEST_MUSIC = "https://genshin.itismyduty.xyz/Music/Beckoning.mp3"
        private const val PACKAGE_NAME = "xyz.genshin.itismyduty"
        private const val MUSIC_DURATION = "music_duration"

        var mMusicCurrentPosition = 0
        var mMusicCurrentState = PlaybackStateCompat.STATE_PAUSED
    }

    private var mediaSession: MediaSessionCompat? =null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var mMusicDuration: Bundle
    private var mMediaPlayer = MediaPlayer()
    private var isMusicLoaded = false
    //private val intent = Intent("xyz.genshin.itismyduty.model.broadcast.MusicStateUpdateBroadcastReceiver")
    //private var br  = MusicLatestPositionBroadcastReceiver(mMediaPlayer)
    private lateinit var timer: Timer

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
    ): BrowserRoot? {
        return if (clientPackageName == PACKAGE_NAME){
            BrowserRoot(MY_MEDIA_ROOT_ID, null)
        }else {
            null
        }

    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {

        if (parentId == MusicList.CITY_OF_WINDS_AND_IDYLLS){
            //音乐持续时间
            mMusicDuration = Bundle()
            mMusicDuration.putInt(MUSIC_DURATION, 102000)
            val musicList = ArrayList<MediaBrowserCompat.MediaItem>()
            val desc = MediaDescriptionCompat.Builder()
                .setMediaId(MusicList.CITY_OF_WINDS_AND_IDYLLS)
                .setMediaUri(Uri.parse("https://genshin.itismyduty.xyz/Music/Beckoning.mp3"))
                .setIconUri(Uri.parse("https://genshin.itismyduty.xyz/music.jpg"))
                .setTitle("Beckoning")
                .setSubtitle("陈")
                .setExtras(mMusicDuration)
                .build()
            val item = MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
            musicList.add(item)
            musicList.add(item)

            result.sendResult(musicList)
        }

    }

    private val mSessionCallback = object : MediaSessionCompat.Callback(){

        override fun onPrepare() {
            super.onPrepare()
            mMusicCurrentPosition = mMediaPlayer.currentPosition
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPlay() {
            if (!isMusicLoaded) {
                mMediaPlayer.reset()
                mMediaPlayer.setDataSource(TEST_MUSIC)
                mMediaPlayer.prepareAsync()
                mMediaPlayer.setOnPreparedListener {
                    mMediaPlayer.start()
                    setMediaPlayerCompleteCallback()
                    isMusicLoaded = true
                    //更新数据
                    mediaSession?.isActive = true
                    updateSeekBar()
                }
            }else {
                mMediaPlayer.start()
                setMediaPlayerCompleteCallback()
                updateSeekBar()
                //更新数据
                mediaSession?.isActive = true
            }
            //启动服务
            startService(Intent(this@MusicService, MusicService::class.java))
            setNotification()
            setPlayState()
        }

        override fun onStop() {
            super.onStop()
            mMusicCurrentState = if (mMediaPlayer.isPlaying){
                PlaybackStateCompat.STATE_PLAYING
            }else {
                PlaybackStateCompat.STATE_PAUSED
            }
        }

        override fun onPause() {
            mMediaPlayer.pause()
            timer.cancel()
            setPauseState()
            mediaSession?.isActive = false
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            mMediaPlayer.seekTo(pos.toInt())
        }

    }

    private fun setMediaPlayerCompleteCallback(){

        mMediaPlayer.setOnCompletionListener {

            stateBuilder = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED, 0,
                    SystemClock.elapsedRealtime().toFloat()
                )
            mediaSession?.setPlaybackState(stateBuilder.build())

        }

    }

    private fun updateSeekBar(){
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                val stateBuild = PlaybackStateCompat.Builder()
                stateBuild.setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    mMediaPlayer.currentPosition.toLong(),
                    1f)
                mMusicCurrentPosition = mMediaPlayer.currentPosition
                mediaSession?.setPlaybackState(stateBuild.build())
            }
        }
        timer = Timer()
        timer.schedule(task, 0, 1000)
    }

    private fun setPlayState(){
        //设置状态
        stateBuilder = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING,
                mMediaPlayer.currentPosition.toLong(),
                1f
            )
        mediaSession?.setPlaybackState(stateBuilder.build())
    }

    private fun setPauseState(){

        //更新状态
        stateBuilder = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PAUSED, 0,
                1f
            )
        mediaSession?.setPlaybackState(stateBuilder.build())

    }

    private fun setNotification(){

        //通知
        val builder = NotificationCompat.Builder(this@MusicService, "1")
        builder.setContentTitle("33")
        builder.setContentIntent(mediaSession?.controller?.sessionActivity)
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        startForeground(1, builder.build())

    }

}
