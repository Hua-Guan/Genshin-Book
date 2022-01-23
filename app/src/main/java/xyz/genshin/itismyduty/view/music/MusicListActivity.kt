package xyz.genshin.itismyduty.view.music

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.bumptech.glide.Glide
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.MusicListAdapter
import xyz.genshin.itismyduty.model.bean.MusicListBean
import xyz.genshin.itismyduty.server.MusicList
import xyz.genshin.itismyduty.server.MusicService
import xyz.genshin.itismyduty.utils.Tools
import xyz.genshin.itismyduty.view.home.MainActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/**
 * @author GuanHua
 */
class MusicListActivity: AppCompatActivity() {

    companion object{

        private const val mMusicUri = "https://genshin.itismyduty.xyz/music.jpg"
        private const val TEST_MUSIC = "https://genshin.itismyduty.xyz/Music/Beckoning.mp3"
        private const val MUSIC_STATE_KEY = "music_state_key"
        private const val MUSIC_PLAY_STATE_KEY = "music_play_state_key"
        private const val MUSIC_DURATION = "music_duration"
    }

    private var mListView: ListView? =null
    private lateinit var mPlayMusic: ImageView
    private lateinit var mMusicImage: ImageView
    private lateinit var mMusicTitle: TextView
    private lateinit var mMusicAuthor: TextView
    private lateinit var mMusicClient: MediaBrowserCompat
    private lateinit var mMusicData: SharedPreferences
    private var mMusicState = PlaybackStateCompat.STATE_PAUSED
    private lateinit var mMusicSeekBar: SeekBar
    private lateinit var mMusicDuration: Bundle
    private var mMusicCurrentTime = 0
    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            mMusicClient.sessionToken.also {
                token ->
                val mMusicController = MediaControllerCompat(
                    this@MusicListActivity,
                    token
                )
                MediaControllerCompat.setMediaController(this@MusicListActivity, mMusicController)
            }
            buildTransportControls()

            mMusicClient.subscribe(MusicList.CITY_OF_WINDS_AND_IDYLLS, object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {

                    val mList = ArrayList<MusicListBean>()
                    for (item in children){
                        val bean = MusicListBean()
                        bean.mMusicImageUri = item.description.iconUri.toString()
                        bean.mMusicAuthor = item.description.subtitle.toString()
                        bean.mMusicName = item.description.title.toString()
                        mMusicDuration = item.description.extras!!

                        mList.add(bean)
                    }

                    val mMusicListAdapter = MusicListAdapter(this@MusicListActivity, mList)

                    mListView?.adapter = mMusicListAdapter
                }
            })
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
        }
    }

    private var controllerCallback = object : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {



        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            //改变播放图标
            if (state != null) {
                if (state.state == PlaybackStateCompat.STATE_PLAYING){
                    mPlayMusic.setImageResource(R.drawable.ic_pause)
                    setMusicSeekBar()

                }else if (state.state == PlaybackStateCompat.STATE_PAUSED){
                    mPlayMusic.setImageResource(R.drawable.ic_play)
                }
                //记录音乐播放的状态
                mMusicState = state.state
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_list)
        if (mListView == null) {
            initView()
            setMusicClient()
            setBack()
            setMusicImage()
            setMusicTitle()
            setMusicAuthor()
            mMusicData = getSharedPreferences(MUSIC_STATE_KEY, MODE_PRIVATE)
        }

        val int = mMusicData.getInt(MUSIC_PLAY_STATE_KEY, PlaybackStateCompat.STATE_PAUSED)
        if (int == PlaybackStateCompat.STATE_PLAYING) {
            mPlayMusic.setImageResource(R.drawable.ic_pause)
        }else if (int == PlaybackStateCompat.STATE_PAUSED){
            mPlayMusic.setImageResource(R.drawable.ic_play)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        mMusicClient.connect()
    }

    override fun onResume() {
        super.onResume()
    }

    @SuppressLint("CommitPrefEdits")
    override fun onStop() {
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(controllerCallback)
        mMusicClient.disconnect()
        //保持播放状态
        val edit = mMusicData.edit()
        edit.putInt(MUSIC_PLAY_STATE_KEY, mMusicState)
        edit.apply()
        super.onStop()
    }

    private fun initView(){
        mListView = findViewById(R.id.music_list)
        mPlayMusic = findViewById(R.id.music_play_or_pause)
        mMusicImage = findViewById(R.id.image_music)
        mMusicTitle = findViewById(R.id.music_title)
        mMusicAuthor = findViewById(R.id.music_author)
        mMusicSeekBar = findViewById(R.id.music_seek_bar)
    }

    private fun setBack(){
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {

            finish()

        }
    }

    private fun setMusicClient(){

        mMusicClient = MediaBrowserCompat(
            this,
            ComponentName(this, MusicService::class.java),
            connectionCallbacks,
            null
        )

    }

    private fun setMusicImage(){

        Glide.with(this)
            .load(mMusicUri)
            .into(mMusicImage)
        mMusicImage.clipToOutline = true

    }

    @SuppressLint("SetTextI18n")
    private fun setMusicTitle(){

        mMusicTitle.text = "Beckoning"


    }

    private fun setMusicAuthor(){

        mMusicAuthor.text = "陈"

    }

    private fun setMusicSeekBar(){


        mMusicSeekBar.max = mMusicDuration.getInt(MUSIC_DURATION)

        val task: TimerTask = object : TimerTask() {
            override fun run() {
                mMusicCurrentTime += 1000
                mMusicSeekBar.progress = mMusicCurrentTime
            }
        }

        val timer = Timer()
        timer.schedule(task, 0, 1000)

    }

    fun buildTransportControls(){

        val mMusicController = MediaControllerCompat.getMediaController(this@MusicListActivity)
        mPlayMusic.setOnClickListener {

            val pbState = mMusicController.playbackState.state
            if (pbState == PlaybackStateCompat.STATE_PLAYING){
                mMusicController.transportControls.pause()
            }else{
                mMusicController.transportControls.play()
            }
        }
        mMusicController.registerCallback(controllerCallback)
    }
}