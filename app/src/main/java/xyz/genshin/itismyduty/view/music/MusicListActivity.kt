package xyz.genshin.itismyduty.view.music

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.MusicListAdapter
import xyz.genshin.itismyduty.model.bean.MusicListBean
import xyz.genshin.itismyduty.server.MusicConst
import xyz.genshin.itismyduty.server.MusicConst.Companion.MUSIC_DATA
import xyz.genshin.itismyduty.server.MusicConst.Companion.MUSIC_MAX_PROGRESS
import xyz.genshin.itismyduty.server.MusicConst.Companion.SEEKBAR_PROGRESS
import xyz.genshin.itismyduty.server.MusicService
import xyz.genshin.itismyduty.utils.Tools
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

/**
 * @author GuanHua
 */
class MusicListActivity: AppCompatActivity() {

    companion object{
        private const val mMusicUri = "https://genshin.itismyduty.xyz/music.jpg"
        private const val MUSIC_DURATION = "music_duration"
        private const val PLAY_STATE = "PLAY_STATE"
    }

    private val handler = Handler(Looper.myLooper()!!)

    private var mListView: ListView? =null
    private lateinit var mPlayMusic: ImageView
    private lateinit var mMusicNext: ImageView
    private lateinit var mMusicImage: ImageView
    private lateinit var mMusicTitle: TextView
    private lateinit var mMusicAuthor: TextView
    private lateinit var mMusicTime: TextView
    private lateinit var mMusicAllTime: TextView
    private lateinit var mMusicClient: MediaBrowserCompat
    private lateinit var mMusicController: MediaControllerCompat
    private lateinit var mMusicSeekBar: SeekBar
    private var mProgressAnimator = ValueAnimator.ofInt(0, 1000)
    private var mCurrentMusicId = 0
    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onConnected() {
            super.onConnected()
            mMusicClient.sessionToken.also {
                token ->
                mMusicController = MediaControllerCompat(
                    this@MusicListActivity,
                    token
                )
                MediaControllerCompat.setMediaController(this@MusicListActivity, mMusicController)
            }
            buildTransportControls()
            mMusicClient.subscribe(MusicConst.CITY_OF_WINDS_AND_IDYLLS, object : MediaBrowserCompat.SubscriptionCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    val mList = ArrayList<MusicListBean>()
                    for ((id, item) in children.withIndex()){
                        val bean = MusicListBean()
                        bean.mMusicImageUri = item.description.iconUri.toString()
                        bean.mMusicId = id
                        bean.mMusicAuthor = item.description.subtitle.toString()
                        bean.mMusicName = item.description.title.toString()

                        mList.add(bean)
                    }
                    val mMusicListAdapter = MusicListAdapter(this@MusicListActivity, mList)

                    mListView?.adapter = mMusicListAdapter
                    //设置SeekBar的值
                    val extras = children[0].description.extras
                    if (extras != null) {
                        if (extras.getBoolean(MusicConst.MUSIC_IS_PLAYING, false)){
                            mMusicSeekBar.max = extras.getInt(MUSIC_MAX_PROGRESS, 0)
                            mMusicSeekBar.progress = extras.getInt(MusicConst.MUSIC_CURRENT_PROGRESS, 0)
                            mMusicAllTime.text = Tools.formatSeconds(ceil(((mMusicSeekBar.max).toDouble() / 1000)).toInt())
                            mProgressAnimator = ValueAnimator.ofInt(mMusicSeekBar.progress, mMusicSeekBar.max)
                            mProgressAnimator.duration = (mMusicSeekBar.max - mMusicSeekBar.progress).toLong()
                            mProgressAnimator.interpolator = LinearInterpolator()
                            mProgressAnimator.addUpdateListener {
                                mMusicSeekBar.progress = it.animatedValue as Int
                            }
                            mProgressAnimator.start()
                            //设置播放图标
                            mPlayMusic.setImageResource(R.drawable.ic_pause)
                        }
                    }
                }
            })
        }
    }

    private var controllerCallback = object : MediaControllerCompat.Callback() {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            if (metadata != null) {
                mMusicSeekBar.max = metadata.getLong(MUSIC_DURATION).toInt()
                getSharedPreferences(MUSIC_DATA, MODE_PRIVATE)
                    .edit()
                    .putInt(MUSIC_MAX_PROGRESS, mMusicSeekBar.max)
                    .apply()
                mMusicAllTime.text = Tools.formatSeconds(ceil(((mMusicSeekBar.max).toDouble() / 1000)).toInt())
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            if (state != null) {
                if (state.state == PlaybackStateCompat.STATE_PAUSED){
                    mPlayMusic.setImageResource(R.drawable.ic_play)
                    mProgressAnimator.cancel()
                }else if (state.state == PlaybackStateCompat.STATE_PLAYING){
                    mPlayMusic.setImageResource(R.drawable.ic_pause)
                    val timeToEnd = mMusicSeekBar.max - state.position
                    mProgressAnimator = ValueAnimator.ofInt(state.position.toInt(), mMusicSeekBar.max)
                    mProgressAnimator.duration = timeToEnd
                    mProgressAnimator.interpolator = LinearInterpolator()
                    mProgressAnimator.addUpdateListener {
                        mMusicSeekBar.progress = it.animatedValue as Int
                    }
                    mProgressAnimator.start()

                }else if (state.state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT){
                    mProgressAnimator.cancel()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_list)
        initView()
        setMusicClient()
        setBack()
        setMusicImage()
        setMusicTitle()
        setMusicAuthor()
    }

    override fun onStart() {
        super.onStart()
        mMusicClient.connect()
    }

    @SuppressLint("CommitPrefEdits")
    override fun onStop() {
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(controllerCallback)
        mMusicController.transportControls.stop()
        mMusicClient.disconnect()
        mProgressAnimator.cancel()
        super.onStop()
    }

    private fun initView(){
        mListView = findViewById(R.id.music_list)
        mPlayMusic = findViewById(R.id.music_play_or_pause)
        mMusicNext = findViewById(R.id.music_next)
        mMusicImage = findViewById(R.id.image_music)
        mMusicTitle = findViewById(R.id.music_title)
        mMusicAuthor = findViewById(R.id.music_author)
        mMusicSeekBar = findViewById(R.id.music_seek_bar)
        mMusicTime = findViewById(R.id.music_time)
        mMusicAllTime = findViewById(R.id.music_all_time)
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun buildTransportControls(){
        mPlayMusic.setOnClickListener {
            val pbState = mMusicController.playbackState.state
            if (pbState == PlaybackStateCompat.STATE_PLAYING){
                mMusicController.transportControls.pause()
                mPlayMusic.setImageResource(R.drawable.ic_play)
            }else{
                mMusicController.transportControls.pause()
                //mMusicController.transportControls.prepareFromMediaId(mCurrentMusicId.toString(), null)
                mPlayMusic.setImageResource(R.drawable.ic_pause)
            }
        }

        mMusicNext.setOnClickListener {
            mMusicController.transportControls.skipToNext()
        }

        mMusicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                handler.post {
                    if (seekBar != null) {
                        mMusicTime.text = Tools.formatSeconds(ceil(((seekBar.progress).toDouble() / 1000)).toInt())
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (mProgressAnimator.isRunning){
                    mProgressAnimator.cancel()
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    mMusicController.transportControls.seekTo(seekBar.progress.toLong())
                }
            }
        })

        mListView?.setOnItemClickListener { parent, view, position, id ->
            mCurrentMusicId = id.toInt()
            mediaController.transportControls.prepareFromMediaId(mCurrentMusicId.toString(), null)
        }

        mMusicController.registerCallback(controllerCallback)
    }
}

