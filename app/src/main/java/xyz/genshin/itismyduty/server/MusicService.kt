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
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.google.gson.JsonParser
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.bean.MusicBean
import xyz.genshin.itismyduty.model.broadcast.LongPressHomeBroadcastReceiver
import xyz.genshin.itismyduty.model.broadcast.MusicNotificationReceiver
import xyz.genshin.itismyduty.utils.ConnectServer
import xyz.genshin.itismyduty.utils.HttpRequest
import xyz.genshin.itismyduty.utils.VolleyInstance


class MusicService : MediaBrowserServiceCompat() {

    companion object{
        const val TEST_URI = "https://genshin.itismyduty.xyz/music.jpg"
        private const val MY_MEDIA_ROOT_ID = "genshin_music"
        private const val PACKAGE_NAME = "xyz.genshin.itismyduty"
        private const val MUSIC_DURATION = "music_duration"
    }

    private var isServiceRunning = false
    //??????????????????
    private lateinit var mMusicList : ArrayList<MediaBrowserCompat.MediaItem>
    private var mMusicUri = "https://genshin.itismyduty.xyz/Music/Beckoning.mp3"
    private var mMusicUri1 = "https://genshin.itismyduty.xyz/Music/BeginsTheJourney.mp3"
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    //?????????????????????id
    private var mCurrentMusicMediaId = "0"
    //?????????
    private val mMediaPlayer = MediaPlayer()
    private var hasInitMusic = false
    private val longPressHomeBroadcast = LongPressHomeBroadcastReceiver()
    private lateinit var mMusicNotificationReceiver: MusicNotificationReceiver
    private var mediaSession: MediaSessionCompat? =null

    //?????????????????????????????????????????????
    private var mCurrentMusicTitle = "Beckoning"
    private var mCurrentMusicImage = ""
    //????????????????????????????????????
    private var mCurrentMusicSubtitle = ""
    //???????????????view
    private var notificationLayout: RemoteViews? = null
    //???????????????
    private var builder: NotificationCompat.Builder? = null
    //???????????????
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
        if (isServiceRunning) {
            unregisterReceiver(mMusicNotificationReceiver)
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
        result.detach()
            //?????????????????????????????????MusicActivity?????????????????????????????????????????????
            val bundle = Bundle()
            bundle.putInt(MusicConst.MUSIC_CURRENT_PROGRESS, mMediaPlayer.currentPosition)
            bundle.putInt(MusicConst.MUSIC_MAX_PROGRESS, mMediaPlayer.duration)
            bundle.putBoolean(MusicConst.MUSIC_IS_PLAYING, mMediaPlayer.isPlaying)
            bundle.putBoolean(MusicConst.HAS_INIT_MUSIC, hasInitMusic)
            mMusicList = ArrayList()
            val stringRequest = StringRequest("http://genshin.itismyduty.xyz:8080/GenshinBook/musicBean",
                {respond ->
                    val string = String(respond.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                    val jsonArray = JsonParser.parseString(string).asJsonArray
                    var id = -1
                    for (item in jsonArray){
                        id ++
                        val bean = Gson().fromJson(item, MusicBean::class.java)
                        val desc = MediaDescriptionCompat.Builder()
                            .setMediaId(id.toString())
                            .setIconUri(Uri.parse(bean.musicCover))
                            .setMediaUri(Uri.parse(bean.musicUri))
                            .setTitle(bean.musicTitle)
                            .setSubtitle(bean.musicAuthor)
                            .setExtras(bundle)
                            .build()
                        val mediaItem = MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
                        mMusicList.add(mediaItem)
                    }
                    result.sendResult(mMusicList)
                }
            ){}
            VolleyInstance.getInstance(applicationContext).addToRequestQueue(stringRequest)

    }

    private val mSessionCallback = object : MediaSessionCompat.Callback(){

        /**
         * ?????????service????????????????????????????????????????????????????????????????????????
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
                //??????????????????????????????????????????????????????
                mCurrentMusicTitle = description.title.toString()
                mCurrentMusicSubtitle = description.subtitle.toString()
                //??????????????????
                mMediaPlayer.setDataSource((description.mediaUri).toString())
                mCurrentMusicMediaId = mediaId
                mMediaPlayer.prepareAsync()
                mMediaPlayer.setOnPreparedListener {
                    //?????????????????????????????????????????????????????????
                    mediaSession?.isActive = true
                    mediaSession?.setMetadata(
                        MediaMetadataCompat.Builder()
                            .putLong(MUSIC_DURATION, mMediaPlayer.duration.toLong())
                            .putString(MusicConst.MUSIC_TITLE, mCurrentMusicTitle)
                            .putString(MusicConst.MUSIC_AUTHOR, mCurrentMusicSubtitle)
                            .build()
                    )
                    //?????????????????????????????????????????????????????????????????????
                    notificationLayout?.setTextViewText(R.id.text_music_title, mCurrentMusicTitle)
                    notificationManager?.notify(1, builder?.build())
                    onPlay()
                }
                //??????????????????????????????????????????logo
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
                //????????????
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
                //????????????????????????
                notificationLayout?.setImageViewResource(R.id.img_play, R.drawable.ic_play)
            }else {
                onPlay()
                //????????????????????????
                notificationLayout?.setImageViewResource(R.id.img_play, R.drawable.ic_pause)
            }
            //????????????
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
            //?????????????????????????????????
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
            //?????????????????????????????????
            if (notificationLayout != null) {
                notificationLayout!!.setTextViewText(R.id.text_music_title, mCurrentMusicTitle)
                notificationManager?.notify(1, builder?.build())
            }
        }
    }
    private fun setPlayState(){
        //????????????
        stateBuilder = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING,
                mMediaPlayer.currentPosition.toLong(),
                1f
            )
        mediaSession?.setPlaybackState(stateBuilder.build())
    }

    private fun setPauseState(){
        //????????????
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
        //?????????????????????????????????
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
        //???????????????????????????????????????????????????
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
        //????????????
        builder = NotificationCompat.Builder(context, channelId).apply {
            priority = NotificationCompat.PRIORITY_MAX
            setSmallIcon(R.mipmap.ic_launcher)
            setCategory(CATEGORY_MESSAGE)
            setDefaults(DEFAULT_ALL)
            setSound(null)//?????????????????????
            setVibrate(null)//????????????
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
        //???????????????????????????
        notificationLayout!!.setImageViewResource(R.id.img_play, R.drawable.ic_pause)
        //?????????????????????????????????
        notificationLayout!!.setTextViewText(R.id.text_music_title, mCurrentMusicTitle)
        //????????????????????????????????????
        val musicNotificationIntentFilter = IntentFilter()
        musicNotificationIntentFilter.addAction(MusicConst.STATE_PLAY)
        musicNotificationIntentFilter.addAction(MusicConst.STATE_NEXT)
        musicNotificationIntentFilter.addAction(MusicConst.STATE_PRE)
        mMusicNotificationReceiver = MusicNotificationReceiver()
        mMusicNotificationReceiver.setMusicNotificationListener(object : MusicNotificationReceiver.MusicNotificationListener{
            override fun onClickNotificationPlayOrPause() {
                //??????????????????
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
        //????????????
        startForeground(1, builder!!.build())
        //?????????????????????
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }
}
