package xyz.genshin.itismyduty.view.music

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.MusicGridAdapter
import xyz.genshin.itismyduty.model.bean.MusicGridBean

class MusicActivity: AppCompatActivity() {

    companion object{

        const val TEST_URI = "https://genshin.itismyduty.xyz/music.jpg"

    }

    private var mGrid: GridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_music)
        initView()
        setGridView()
        showMusicList()
    }

    private fun initView(){

        mGrid = findViewById(R.id.grid_music)

    }

    private fun setGridView(){

        val mList = ArrayList<MusicGridBean>()
        val mBean = MusicGridBean()
        mBean.mMusicImageUri = TEST_URI
        mBean.mMusicAuthor = "33"
        mBean.mMusicName = "44"
        mList.add(mBean)

        val mAdapter = MusicGridAdapter(this, mList)

        mGrid?.adapter = mAdapter

    }

    private fun showMusicList(){

        mGrid?.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id ->
            val intent = Intent(this, MusicListActivity::class.java)
            startActivity(intent)
        }

    }

}