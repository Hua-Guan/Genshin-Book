package xyz.genshin.itismyduty.view.music

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.MusicListAdapter
import xyz.genshin.itismyduty.model.bean.MusicListBean
import xyz.genshin.itismyduty.server.MusicConst
import xyz.genshin.itismyduty.server.MusicConst.Companion.MUSIC_DATA
import xyz.genshin.itismyduty.server.MusicConst.Companion.MUSIC_MAX_PROGRESS
import xyz.genshin.itismyduty.server.MusicService
import xyz.genshin.itismyduty.utils.Tools
import kotlin.collections.ArrayList
import kotlin.math.ceil

/**
 * @author GuanHua
 */
class MusicListActivity: AppCompatActivity() {

    companion object{
        private const val mMusicUri = "https://genshin.itismyduty.xyz/music.jpg"
        private const val MUSIC_DURATION = "music_duration"
    }

    private val handler = Handler(Looper.myLooper()!!)

    private var mListView: ListView? =null
    private lateinit var mMusicList : MutableList<MediaBrowserCompat.MediaItem>
    //暂停或播放
    private lateinit var mPlayMusic: ImageView
    //下一首
    private lateinit var mMusicNext: ImageView
    //上一首
    private lateinit var mMusicPre: ImageView
    //音乐logo
    private lateinit var mMusicImage: ImageView
    //音乐标题
    private lateinit var mMusicTitle: TextView
    //音乐作者
    private lateinit var mMusicAuthor: TextView
    //当前播放时间
    private lateinit var mMusicTime: TextView
    //总播放时间
    private lateinit var mMusicAllTime: TextView
    private lateinit var mMusicClient: MediaBrowserCompat
    private lateinit var mMusicController: MediaControllerCompat
    //进度条
    private lateinit var mMusicSeekBar: SeekBar
    private var mProgressAnimator = ValueAnimator.ofInt(0, 1000)
    private var mCurrentMusicId = 0
    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onConnected() {
            super.onConnected()
            initMusicControl()
            buildTransportControls()
            initListView()
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
                setMusicTitle(metadata.getString(MusicConst.MUSIC_TITLE))
                setMusicAuthor(metadata.getString(MusicConst.MUSIC_AUTHOR))
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
                    setSeekBarAnimation(state.position.toInt())
                }else if (state.state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT){
                    mProgressAnimator.cancel()
                }else if (state.state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS){
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
        mMusicPre = findViewById(R.id.music_pre)
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
    private fun setMusicTitle(title : String){
        mMusicTitle.text = title
    }

    private fun setMusicAuthor(author : String){
        mMusicAuthor.text = author
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun buildTransportControls(){
        setPlayControl()
        setNextControl()
        setPreControl()
        setSeekBarControl()
        setListViewControl()
        mMusicController.registerCallback(controllerCallback)
    }

    private fun setPlayControl(){
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
    }

    private fun setNextControl(){
        mMusicNext.setOnClickListener {
            mMusicController.transportControls.skipToNext()
        }
    }

    private fun setPreControl(){
        mMusicPre.setOnClickListener {
            mMusicController.transportControls.skipToPrevious()
        }
    }

    private fun setSeekBarControl(){
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
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setListViewControl(){
        mListView?.setOnItemClickListener { parent, view, position, id ->
            mProgressAnimator.cancel()
            mCurrentMusicId = id.toInt()
            mediaController.transportControls.prepareFromMediaId(mCurrentMusicId.toString(), null)
        }
    }

    private fun initListView(){
        val mMusicAlbum = intent.getStringExtra(MusicConst.MUSIC_ALBUM)
        if (mMusicAlbum != null) {
            mMusicClient.subscribe(mMusicAlbum, object : MediaBrowserCompat.SubscriptionCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    mMusicList = children
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
                            setSeekBarMax(extras.getInt(MUSIC_MAX_PROGRESS, 0))
                            setSeekBarProgress(extras.getInt(MusicConst.MUSIC_CURRENT_PROGRESS, 0))
                            setSeekBarTextMax(mMusicSeekBar.max)
                            setSeekBarAnimation()
                            //设置播放图标
                            mPlayMusic.setImageResource(R.drawable.ic_pause)
                        }else{
                            setSeekBarMax(extras.getInt(MUSIC_MAX_PROGRESS, 0))
                            setSeekBarProgress(extras.getInt(MusicConst.MUSIC_CURRENT_PROGRESS, 0))
                            mMusicAllTime.text = Tools.formatSeconds(ceil(((mMusicSeekBar.max).toDouble() / 1000)).toInt())
                        }
                    }
                    //设置当第一次启动service时的music
                    initMusic()
                }
            })
        }
    }

    private fun initMusicControl(){
        mMusicClient.sessionToken.also {
                token ->
            mMusicController = MediaControllerCompat(
                this@MusicListActivity,
                token
            )
            MediaControllerCompat.setMediaController(this@MusicListActivity, mMusicController)
        }
    }

    /**
     * 设置当service第一次启动时要播放的音乐，一般是音乐列表的第一首
     */
    private fun initMusic(){
        mMusicController.transportControls.prepare()
    }
    private fun setSeekBarProgress(progress: Int){
        mMusicSeekBar.progress = progress
    }
    private fun setSeekBarMax(max: Int){
        mMusicSeekBar.max = max
    }
    private fun setSeekBarTextMax(max: Int){
        mMusicAllTime.text = Tools.formatSeconds(ceil(((max).toDouble() / 1000)).toInt())
    }
    private fun setSeekBarAnimation(){
        mProgressAnimator = ValueAnimator.ofInt(mMusicSeekBar.progress, mMusicSeekBar.max)
        mProgressAnimator.duration = (mMusicSeekBar.max - mMusicSeekBar.progress).toLong()
        mProgressAnimator.interpolator = LinearInterpolator()
        mProgressAnimator.addUpdateListener {
            setSeekBarProgress(it.animatedValue as Int)
        }
        mProgressAnimator.start()
    }
    private fun setSeekBarAnimation(currentPosition: Int){
        val timeToEnd = mMusicSeekBar.max - currentPosition
        mProgressAnimator = ValueAnimator.ofInt(currentPosition, mMusicSeekBar.max)
        mProgressAnimator.duration = timeToEnd.toLong()
        mProgressAnimator.interpolator = LinearInterpolator()
        mProgressAnimator.addUpdateListener {
            mMusicSeekBar.progress = it.animatedValue as Int
        }
        mProgressAnimator.start()
    }
}