package xyz.genshin.itismyduty.view.music

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.MusicListAdapter
import xyz.genshin.itismyduty.model.bean.MusicListBean
import xyz.genshin.itismyduty.server.MusicService
import xyz.genshin.itismyduty.view.home.MainActivity

/**
 * @author GuanHua
 */
class MusicListActivity: AppCompatActivity() {

    companion object{

        private const val mMusicUri = "https://genshin.itismyduty.xyz/music.jpg"
        private const val TEST_MUSIC = "https://genshin.itismyduty.xyz/Music/Beckoning.mp3"

    }

    private lateinit var mListView: ListView
    private lateinit var mPlayMusic: ImageView
    private lateinit var mMusicClient: MediaBrowserCompat
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

            mMusicClient.subscribe(mMusicClient.root, object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    val mediaItem = children[0]

                    val mList = ArrayList<MusicListBean>()
                    val bean = MusicListBean()
                    bean.mMusicImageUri = mediaItem.description.iconUri.toString()
                    bean.mMusicAuthor = mediaItem.description.subtitle.toString()
                    bean.mMusicName = mediaItem.description.title.toString()

                    mList.add(bean)

                    val mMusicListAdapter = MusicListAdapter(this@MusicListActivity, mList)

                    mListView.adapter = mMusicListAdapter
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

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {}

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_list)
        initView()
        setMusicClient()
        setBack()
    }

    override fun onStart() {
        super.onStart()
        mMusicClient.connect()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initView(){
        mListView = findViewById(R.id.music_list)
        mPlayMusic = findViewById(R.id.music_play_or_pause)
    }

    private fun setBack(){
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {

            finish()

        }
    }

    private fun setPlayMusic(){

        val mMediaPlayer = MediaPlayer()
        mMediaPlayer.setDataSource(TEST_MUSIC)
        mMediaPlayer.prepareAsync()

    }

    private fun setMusicClient(){

        mMusicClient = MediaBrowserCompat(
            this,
            ComponentName(this, MusicService::class.java),
            connectionCallbacks,
            null
        )

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