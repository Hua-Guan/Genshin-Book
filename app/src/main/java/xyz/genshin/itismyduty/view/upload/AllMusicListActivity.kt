package xyz.genshin.itismyduty.view.upload

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.AllMusicListAdapter
import xyz.genshin.itismyduty.model.bean.AllMusicListBean
import java.util.ArrayList

class AllMusicListActivity: AppCompatActivity() {

    //返回键
    private var mBack: ImageView? = null
    private var mListView: ListView? = null
    private var mList: ArrayList<AllMusicListBean>? = null
    private var mAdapter: AllMusicListAdapter? = null
    private var mDone: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_music_list)
        initView()
        setBack()
        setMusicList()
        setListViewLister()
        setDone()
    }

    private fun initView(){

        mBack = findViewById(R.id.back)
        mListView = findViewById(R.id.all_music_list)
        mDone = findViewById(R.id.done)
    }

    private fun setBack(){
        mBack?.setOnClickListener {
            finish()
        }
    }

    /**
     * 把所有音乐加载到ListView中
     */
    private fun setMusicList(){
        //获取所有歌曲的列表
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
        )

        val cursor = managedQuery(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        mList = ArrayList<AllMusicListBean>()
        while (cursor.moveToNext()) {
            val bean = AllMusicListBean()
            bean.musicId = cursor.getInt(0)
            bean.musicTitle = cursor.getString(4)
            val strSize = cursor.getString(6)
            val doubleSize = strSize.toDouble()
            val resultSize = doubleSize/1000000
            val format = String.format("%.2f", resultSize)
            bean.musicSize = format
            //装载Uri
            val contentUri =
                ContentUris.
                withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, bean.musicId.toLong())
            bean.musicUri = contentUri
            mList!!.add(bean)
        }

        mAdapter = AllMusicListAdapter(this, mList!!)
        mListView?.adapter = mAdapter

    }

    /**
     * 为listview设置监听
     */
    private fun setListViewLister(){
        mListView?.setOnItemClickListener { parent, view, position, id ->
            if (mList?.get(position)?.isChosen == true){
                mList!![position].isChosen = false
            }else{
                mList!![position].isChosen = true
            }
            mAdapter?.notifyDataSetChanged()
        }
    }

    /**
     * 当用户按下确认键时
     */
    private fun setDone(){
        mDone?.setOnClickListener {
            val intent = Intent()
            val uriList = ArrayList<String>()
            val musicTitleList = ArrayList<String>()
            for (item in mList!!){
                if (item.isChosen){
                    uriList.add(item.musicUri.toString())
                    musicTitleList.add(item.musicTitle)
                }
            }
            intent.putStringArrayListExtra(AllMusicConst.MUSIC_URI, uriList)
            intent.putStringArrayListExtra(AllMusicConst.MUSIC_TITLE, musicTitleList)
            setResult(0, intent)
            finish()
        }
    }

}