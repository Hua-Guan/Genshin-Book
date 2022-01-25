package xyz.genshin.itismyduty.view.music

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media.MediaBrowserServiceCompat
import com.bumptech.glide.Glide
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.MusicListAdapter
import xyz.genshin.itismyduty.model.bean.MusicListBean
import xyz.genshin.itismyduty.model.broadcast.MusicStateUpdateBroadcastReceiver
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
        private const val MUSIC_CURRENT_POSITION = "MUSIC_CURRENT_POSITION"
        private const val MUSIC_CURRENT_PLAY_STATE = "MUSIC_CURRENT_PLAY_STATE"
    }

    private var mListView: ListView? =null
    private lateinit var mPlayMusic: ImageView
    private lateinit var mMusicImage: ImageView
    private lateinit var mMusicTitle: TextView
    private lateinit var mMusicAuthor: TextView
    private lateinit var mMusicClient: MediaBrowserCompat
    private lateinit var mMusicController: MediaControllerCompat
    private lateinit var mMusicSeekBar: SeekBar
    private lateinit var mMusicDuration: Bundle
    //private lateinit var br: MusicStateUpdateBroadcastReceiver
    //private var mIntent = Intent("xyz.genshin.itismyduty.model.broadcast.MusicLatestPositionBroadcastReceiver")
    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            mMusicClient.sessionToken.also {
                token ->
                mMusicController = MediaControllerCompat(
                    this@MusicListActivity,
                    token
                )
                //
                MediaControllerCompat.setMediaController(this@MusicListActivity, mMusicController)
                mMusicController.transportControls.prepare()
                mMusicSeekBar.max = 102000
                mMusicSeekBar.progress = MusicService.mMusicCurrentPosition
                //
                if (MusicService.mMusicCurrentState == PlaybackStateCompat.STATE_PLAYING){
                    mPlayMusic.setImageResource(R.drawable.ic_pause)
                }else{
                    mPlayMusic.setImageResource(R.drawable.ic_play)
                }
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
                if (state.state == PlaybackStateCompat.STATE_PLAYING){
                    mMusicSeekBar.progress = state.position.toInt()
                }

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
        }
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

    fun buildTransportControls(){
        mPlayMusic.setOnClickListener {

            val pbState = mMusicController.playbackState.state
            if (pbState == PlaybackStateCompat.STATE_PLAYING){
                mMusicController.transportControls.pause()
                mPlayMusic.setImageResource(R.drawable.ic_play)
            }else{
                mMusicController.transportControls.play()
                mPlayMusic.setImageResource(R.drawable.ic_pause)
            }
            println("333:::" + pbState)
        }

        mMusicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

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

