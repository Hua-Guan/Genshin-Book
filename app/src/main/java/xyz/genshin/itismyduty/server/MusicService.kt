package xyz.genshin.itismyduty.server

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.media.MediaBrowserServiceCompat
import android.content.IntentFilter
import android.support.v4.media.MediaMetadataCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import xyz.genshin.itismyduty.model.broadcast.LongPressHomeBroadcastReceiver
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList


class MusicService : MediaBrowserServiceCompat() {

    companion object{
        private const val MY_MEDIA_ROOT_ID = "genshin_music"
        private const val PACKAGE_NAME = "xyz.genshin.itismyduty"
        private const val MUSIC_DURATION = "music_duration"
        private const val MUSIC_DATA = "music_data"
        private const val SEEKBAR_CURRENT_PROGRESS = "SEEKBAR_CURRENT_PROGRESS"
        private const val ACTION_SEEKBAR = "ACTION_SEEKBAR"
        private const val SEEKBAR_PROGRESS = "seekbar_progress"
        private const val PLAY_STATE = "PLAY_STATE"
    }

    private var mediaSession: MediaSessionCompat? =null
    private lateinit var mMusicList : ArrayList<MediaBrowserCompat.MediaItem>
    private var mMusicUri = "https://genshin.itismyduty.xyz/Music/Beckoning.mp3"
    private var mMusicUri1 = "https://genshin.itismyduty.xyz/Music/BeginsTheJourney.mp3"
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private var mCurrentMusicMediaId = "0"
    private val mMediaPlayer = MediaPlayer()

    private val longPressHomeBroadcast = LongPressHomeBroadcastReceiver()

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

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        registerReceiver(longPressHomeBroadcast, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(longPressHomeBroadcast)
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
        if (parentId == MusicConst.CITY_OF_WINDS_AND_IDYLLS){
            mMusicList = ArrayList()
            val bundle = Bundle()
            bundle.putInt(MusicConst.MUSIC_CURRENT_PROGRESS, mMediaPlayer.currentPosition)
            bundle.putInt(MusicConst.MUSIC_MAX_PROGRESS, mMediaPlayer.duration)
            bundle.putBoolean(MusicConst.MUSIC_IS_PLAYING, mMediaPlayer.isPlaying)
            //音乐持续时间
            val desc0 = MediaDescriptionCompat.Builder()
                .setMediaId(0.toString())
                .setIconUri(Uri.parse("https://genshin.itismyduty.xyz/music.jpg"))
                .setMediaUri(Uri.parse(mMusicUri))
                .setTitle("Beckoning")
                .setSubtitle("陈")
                .setExtras(bundle)
                .build()
            val item0 = MediaBrowserCompat.MediaItem(desc0, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
            mMusicList.add(item0)

            val desc1 = MediaDescriptionCompat.Builder()
                .setMediaId(1.toString())
                .setIconUri(Uri.parse("https://genshin.itismyduty.xyz/music.jpg"))
                .setMediaUri(Uri.parse(mMusicUri1))
                .setTitle("BeginsTheJourney")
                .setSubtitle("陈")
                .setExtras(bundle)
                .build()
            val item1 = MediaBrowserCompat.MediaItem(desc1, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
            mMusicList.add(item1)

            result.sendResult(mMusicList)
        }
    }

    private val mSessionCallback = object : MediaSessionCompat.Callback(){

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPrepareFromMediaId(mediaId, extras)
            if (mediaId != null) {
                mMediaPlayer.reset()
                mMediaPlayer.setDataSource((mMusicList[Integer.valueOf(mediaId)].description.mediaUri).toString())
                mCurrentMusicMediaId = mediaId
                mMediaPlayer.prepareAsync()
                mMediaPlayer.setOnPreparedListener {
                    mediaSession?.isActive = true
                    mediaSession?.setMetadata(
                        MediaMetadataCompat.Builder()
                            .putLong(MUSIC_DURATION, mMediaPlayer.duration.toLong())
                            .build()
                    )
                    onPlay()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPlay() {
            mediaSession?.isActive = true
            mMediaPlayer.start()
            mMediaPlayer.setOnCompletionListener {
                onSkipToNext()
            }
            setPlayState()
            //启动服务
            startService(Intent(this@MusicService, MusicService::class.java))
            setNotification()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("CommitPrefEdits")
        override fun onPause() {
            mediaSession?.isActive = false
            if (mMediaPlayer.isPlaying) {
                mMediaPlayer.pause()
                setPauseState()
            }else {
                onPlay()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            mMediaPlayer.seekTo(pos.toInt())
            mMediaPlayer.setOnSeekCompleteListener {
                onPlay()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSkipToNext() {
            super.onSkipToNext()
            setSkipToNextState()
            var valueOf = Integer.valueOf(mCurrentMusicMediaId)
            if (valueOf < mMusicList.size - 1){
                valueOf ++
                onPrepareFromMediaId(valueOf.toString(), null)
            }else {
                onPrepareFromMediaId("0", null)
            }
        }
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

    private fun setSkipToNextState(){
        stateBuilder = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, 0,
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
