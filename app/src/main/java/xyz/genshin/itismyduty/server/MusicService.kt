package xyz.genshin.itismyduty.server

import android.annotation.SuppressLint
import android.app.Notification.CATEGORY_MESSAGE
import android.app.Notification.DEFAULT_ALL
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.broadcast.LongPressHomeBroadcastReceiver
import xyz.genshin.itismyduty.model.broadcast.MusicNotificationReceiver


class MusicService : MediaBrowserServiceCompat() {

    companion object{
        const val TEST_URI = "https://genshin.itismyduty.xyz/music.jpg"
        private const val MY_MEDIA_ROOT_ID = "genshin_music"
        private const val PACKAGE_NAME = "xyz.genshin.itismyduty"
        private const val MUSIC_DURATION = "music_duration"
    }

    private var isServiceRunning = false
    //音乐播放列表
    private lateinit var mMusicList : ArrayList<MediaBrowserCompat.MediaItem>
    private var mMusicUri = "https://genshin.itismyduty.xyz/Music/Beckoning.mp3"
    private var mMusicUri1 = "https://genshin.itismyduty.xyz/Music/BeginsTheJourney.mp3"
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    //当前播放的音乐id
    private var mCurrentMusicMediaId = "0"
    //播放器
    private val mMediaPlayer = MediaPlayer()
    private var hasInitMusic = false
    private val longPressHomeBroadcast = LongPressHomeBroadcastReceiver()
    private lateinit var mMusicNotificationReceiver: MusicNotificationReceiver
    private var mediaSession: MediaSessionCompat? =null

    //记录当前播放的音乐的标题和图片
    private var mCurrentMusicTitle = "Beckoning"
    private var mCurrentMusicImage = ""
    //记录当前播放的音乐的作者
    private var mCurrentMusicSubtitle = ""
    //音乐通知的view
    private var notificationLayout: RemoteViews? = null
    //通知构建器
    private var builder: NotificationCompat.Builder? = null
    //通知管理器
    private var notificationManager: NotificationManager? = null

    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()
        initMediaSession()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        registerReceiver(longPressHomeBroadcast, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(longPressHomeBroadcast)
        unregisterReceiver(mMusicNotificationReceiver)
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
            bundle.putBoolean(MusicConst.HAS_INIT_MUSIC, hasInitMusic)
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

        /**
         * 设置当service第一次启动时要播放的音乐，一般是音乐列表的第一首
         */
        override fun onPrepare() {
            super.onPrepare()
            initMusic()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPrepareFromMediaId(mediaId, extras)
            if (mediaId != null) {
                mMediaPlayer.reset()
                val description = mMusicList[Integer.valueOf(mediaId)].description
                //更新当前音乐的标题和作者在全局变量中
                mCurrentMusicTitle = description.title.toString()
                mCurrentMusicSubtitle = description.subtitle.toString()
                //加载音乐资源
                mMediaPlayer.setDataSource((description.mediaUri).toString())
                mCurrentMusicMediaId = mediaId
                mMediaPlayer.prepareAsync()
                mMediaPlayer.setOnPreparedListener {
                    //更新当前音乐的标题和作者在音乐控制器中
                    mediaSession?.isActive = true
                    mediaSession?.setMetadata(
                        MediaMetadataCompat.Builder()
                            .putLong(MUSIC_DURATION, mMediaPlayer.duration.toLong())
                            .putString(MusicConst.MUSIC_TITLE, mCurrentMusicTitle)
                            .putString(MusicConst.MUSIC_AUTHOR, mCurrentMusicSubtitle)
                            .build()
                    )
                    //更新当前音乐的标题和作者在通知中的音乐控制器中
                    notificationLayout?.setTextViewText(R.id.text_music_title, mCurrentMusicTitle)
                    notificationManager?.notify(1, builder?.build())
                    onPlay()
                }
                //设置通知中的音乐控制器的音乐logo
                val load = Glide.with(applicationContext)
                    .asBitmap()
                    .load(TEST_URI)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            notificationLayout?.setImageViewBitmap(R.id.img_music_logo, resource)
                            notificationManager?.notify(1, builder?.build())
                        }

                    })
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
            if (!isServiceRunning){
                //启动服务
                startForegroundService(Intent(this@MusicService, MusicService::class.java))
                createNotification(this@MusicService, "1")
                isServiceRunning = true
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("CommitPrefEdits")
        override fun onPause() {
            mediaSession?.isActive = false
            if (mMediaPlayer.isPlaying) {
                mMediaPlayer.pause()
                setPauseState()
                //更新通知播放图标
                notificationLayout?.setImageViewResource(R.id.img_play, R.drawable.ic_play)
            }else {
                onPlay()
                //更新通知播放图标
                notificationLayout?.setImageViewResource(R.id.img_play, R.drawable.ic_pause)
            }
            //更新通知
            notificationManager?.notify(1, builder?.build())
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
            //设置当前播放音乐的标题
            if (notificationLayout != null) {
                notificationLayout!!.setTextViewText(R.id.text_music_title, mCurrentMusicTitle)
                notificationManager?.notify(1, builder?.build())
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            setSkipToPreState()
            var valueOf = Integer.valueOf(mCurrentMusicMediaId)
            if (valueOf > 0){
                valueOf --
                onPrepareFromMediaId(valueOf.toString(), null)
            }else {
                onPrepareFromMediaId((mMusicList.size - 1).toString(), null)
            }
            //设置当前播放音乐的标题
            if (notificationLayout != null) {
                notificationLayout!!.setTextViewText(R.id.text_music_title, mCurrentMusicTitle)
                notificationManager?.notify(1, builder?.build())
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

    private fun setSkipToPreState(){
        stateBuilder = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, 0,
                1f
            )
        mediaSession?.setPlaybackState(stateBuilder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun initMediaSession(){
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

    private fun initMusic(){
        //设置音乐控制器的元数据
        if (!hasInitMusic) {
            hasInitMusic = true
            val description = mMusicList[Integer.valueOf(mCurrentMusicMediaId)].description
            mMediaPlayer.setDataSource((description.mediaUri).toString())
            mMediaPlayer.prepareAsync()
            mMediaPlayer.setOnPreparedListener {
                mediaSession?.isActive = true
                mediaSession?.setMetadata(
                    MediaMetadataCompat.Builder()
                        .putLong(MUSIC_DURATION, mMediaPlayer.duration.toLong())
                        .putString(MusicConst.MUSIC_TITLE, description.title.toString())
                        .putString(MusicConst.MUSIC_AUTHOR, description.subtitle.toString())
                        .build()
                )
            }
        }else{
            val description = mMusicList[Integer.valueOf(mCurrentMusicMediaId)].description
            mediaSession?.isActive = true
            mediaSession?.setMetadata(
                MediaMetadataCompat.Builder()
                    .putLong(MUSIC_DURATION, mMediaPlayer.duration.toLong())
                    .putString(MusicConst.MUSIC_TITLE, description.title.toString())
                    .putString(MusicConst.MUSIC_AUTHOR, description.subtitle.toString())
                    .build()
            )
        }
    }

    @SuppressLint("RemoteViewLayout", "UnspecifiedImmutableFlag")
    private fun createNotification(context: Context, channelId: String){
        val controller = mediaSession?.controller
        //设置当用户按通知中的控制器时的监听
        val playIntent = Intent(MusicConst.STATE_PLAY)
        val nextIntent = Intent(MusicConst.STATE_NEXT)
        val preIntent = Intent(MusicConst.STATE_PRE)
        val playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, 0)
        val pausePendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0)
        val prePendingIntent = PendingIntent.getBroadcast(this, 0, preIntent, 0)
        notificationLayout = RemoteViews(packageName, R.layout.notification_control)
        notificationLayout!!.setOnClickPendingIntent(R.id.img_play, playPendingIntent)
        notificationLayout!!.setOnClickPendingIntent(R.id.img_next, pausePendingIntent)
        notificationLayout!!.setOnClickPendingIntent(R.id.img_pre, prePendingIntent)
        //构建通知
        builder = NotificationCompat.Builder(context, channelId).apply {
            priority = NotificationCompat.PRIORITY_MAX
            setSmallIcon(R.mipmap.ic_launcher)
            setCategory(CATEGORY_MESSAGE)
            setDefaults(DEFAULT_ALL)
            setSound(null)//关闭通知提示音
            setVibrate(null)//关闭震动
            if (controller != null) {
                setContentIntent(controller.sessionActivity)
            }
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            setOngoing(true)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setContent(notificationLayout)
        }
        //设置当前的播放状态
        notificationLayout!!.setImageViewResource(R.id.img_play, R.drawable.ic_pause)
        //设置当前播放音乐的标题
        notificationLayout!!.setTextViewText(R.id.text_music_title, mCurrentMusicTitle)
        //设置音乐通知的广播接收器
        val musicNotificationIntentFilter = IntentFilter()
        musicNotificationIntentFilter.addAction(MusicConst.STATE_PLAY)
        musicNotificationIntentFilter.addAction(MusicConst.STATE_NEXT)
        musicNotificationIntentFilter.addAction(MusicConst.STATE_PRE)
        mMusicNotificationReceiver = MusicNotificationReceiver()
        mMusicNotificationReceiver.setMusicNotificationListener(object : MusicNotificationReceiver.MusicNotificationListener{
            override fun onClickNotificationPlayOrPause() {
                //更新播放状态
                controller?.transportControls?.pause()
            }

            override fun onClickNotificationNext() {
                controller?.transportControls?.skipToNext()
            }

            override fun onClickNotificationPre() {
                controller?.transportControls?.skipToPrevious()
            }

        })
        registerReceiver(mMusicNotificationReceiver, musicNotificationIntentFilter)
        //启动服务
        startForeground(1, builder!!.build())
        //获取通知管理器
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }
}
