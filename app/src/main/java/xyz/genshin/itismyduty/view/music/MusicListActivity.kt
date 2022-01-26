package xyz.genshin.itismyduty.view.music

import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.MusicListAdapter
import xyz.genshin.itismyduty.model.bean.MusicListBean
import xyz.genshin.itismyduty.server.MusicList
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
        private const val MUSIC_DATA = "music_data"
        private const val SEEKBAR_PROGRESS = "seekbar_progress"
    }

    private var mListView: ListView? =null
    private lateinit var mPlayMusic: ImageView
    private lateinit var mMusicImage: ImageView
    private lateinit var mMusicTitle: TextView
    private lateinit var mMusicAuthor: TextView
    private lateinit var mMusicClient: MediaBrowserCompat
    private lateinit var mMusicController: MediaControllerCompat
    private lateinit var mMusicSeekBar: SeekBar
    private var mMusicCurrentPosition = 0
    private var isStartUpdateTime = false
    private lateinit var mMusicDuration: Bundle
    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
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
            mMusicClient.subscribe(MusicList.CITY_OF_WINDS_AND_IDYLLS, object : MediaBrowserCompat.SubscriptionCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
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
    }

    private var controllerCallback = object : MediaControllerCompat.Callback() {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            if (state != null) {
                if (state.state == PlaybackStateCompat.STATE_PAUSED){
                    mPlayMusic.setImageResource(R.drawable.ic_play)
                }else if (state.state == PlaybackStateCompat.STATE_PLAYING){
                    mMusicSeekBar.max = 102000
                    mMusicSeekBar.progress = state.position.toInt()
                    mPlayMusic.setImageResource(R.drawable.ic_pause)
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
        mMusicSeekBar.max = 102000
        mMusicSeekBar.progress = getSharedPreferences(MUSIC_DATA, MODE_PRIVATE)
            .getInt(SEEKBAR_PROGRESS, 0)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onStop() {
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(controllerCallback)
        mMusicController.transportControls.stop()
        mMusicClient.disconnect()
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

    private fun buildTransportControls(){
        mPlayMusic.setOnClickListener {
            val pbState = mMusicController.playbackState.state
            if (pbState == PlaybackStateCompat.STATE_PLAYING){
                mMusicController.transportControls.pause()
                mPlayMusic.setImageResource(R.drawable.ic_play)
            }else{
                mMusicController.transportControls.play()
                mPlayMusic.setImageResource(R.drawable.ic_pause)
            }
        }

        mMusicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    mMusicController.transportControls.pause()
                }
                if (!isStartUpdateTime) {
                    val timer = Timer()
                    val task: TimerTask = object : TimerTask() {
                        override fun run() {
                            if (seekBar != null) {
                                println(Tools.formatSeconds(ceil(((seekBar.progress).toDouble() / 1000)).toInt()))
                            }
                        }
                    }
                    timer.schedule(task, 0, 1000)
                    isStartUpdateTime = true
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    mMusicController.transportControls.seekTo(seekBar.progress.toLong())
                }
            }
        })
        mMusicController.registerCallback(controllerCallback)
    }
}

