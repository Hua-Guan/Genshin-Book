package xyz.genshin.itismyduty.view.music

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.MusicListAdapter
import xyz.genshin.itismyduty.model.bean.MusicListBean
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_list)
        initView()
        setMusicList()
        setPlayMusic()
        setBack()
    }

    private fun initView(){

        mListView = findViewById(R.id.music_list)
        mPlayMusic = findViewById(R.id.music_play_or_pause)
    }

    private fun setMusicList(){


        val mList = ArrayList<MusicListBean>()
        val bean = MusicListBean()
        bean.mMusicImageUri = mMusicUri
        bean.mMusicAuthor = "Test"
        bean.mMusicName = "Test"

        mList.add(bean)

        val mMusicListAdapter = MusicListAdapter(this, mList)

        mListView.adapter = mMusicListAdapter

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

        mPlayMusic.setOnClickListener {

            mMediaPlayer.start()
            println("44444444444444444444444444444444444444")
        }

    }



}