package xyz.genshin.itismyduty.view.upload

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.UploadMusicListAdapter
import xyz.genshin.itismyduty.model.bean.MusicAlbumGridBean
import xyz.genshin.itismyduty.model.bean.UploadMusicListBean
import xyz.genshin.itismyduty.utils.VolleyInstance
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList


class UploadMusicActivity: AppCompatActivity() {

    private val mHandle = Handler(Looper.myLooper()!!)
    private var mMusicAlbumSpinner: Spinner? = null
    private var mMusicAuthorSpinner: Spinner? = null
    private var mChooseMusic: Button? = null
    //
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null
    //
    private var mListView: ListView? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_music)
        initView()
        setMusicAlbumAndAuthorSpinner()
        setGetMusicList()
        registerActivityLaunch()
    }

    private fun initView(){

        mMusicAlbumSpinner = findViewById(R.id.spinner_music_album)
        mMusicAuthorSpinner = findViewById(R.id.spinner_music_author)
        mChooseMusic = findViewById(R.id.choose_music)
        mListView = findViewById(R.id.list_upload_music)

    }

    /**
     * 获取所有的专辑名和音乐作者
     */
    private fun setMusicAlbumAndAuthorSpinner(){
        //val mList = ArrayList<MusicAlbumGridBean>()
        val mAlbumItems = ArrayList<String>()
        val mAuthorItems = ArrayList<String>()
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, "http://genshin.itismyduty.xyz:8080/GenshinBook/music",
            Response.Listener { response ->
                val string = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                val jsonArray = JsonParser.parseString(string).asJsonArray
                for (item in jsonArray){
                    val bean = Gson().fromJson(item, MusicAlbumGridBean::class.java)
                    //mList.add(bean)
                    mAlbumItems.add(bean.albumName)
                    if (mAuthorItems.size == 0) {
                        mAuthorItems.add(bean.albumAuthor)
                    }
                }
                mHandle.post {
                    //设置专辑
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mAlbumItems)
                    mMusicAlbumSpinner?.adapter = adapter
                    //设置作者
                    val authorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mAuthorItems)
                    mMusicAuthorSpinner?.adapter = authorAdapter
                }
            },
            Response.ErrorListener {
            }) {
            override fun getParams(): Map<String, String> {
                val map: MutableMap<String, String> = HashMap()
                map["request"] = "getMusicAlbum"
                return map
            }
        }
        VolleyInstance.getInstance(applicationContext).addToRequestQueue(stringRequest)
    }

    private fun setGetMusicList(){
        mChooseMusic?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
            val musicListIntent = Intent(this, AllMusicListActivity::class.java)
            activityResultLauncher?.launch(musicListIntent)
        }
    }

    /**
     * 登记打开文件管理器的启动器
     */
    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun registerActivityLaunch(){
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result.data
            val musicUri = data?.getStringArrayListExtra(AllMusicConst.MUSIC_URI)
            val musicTitle = data?.getStringArrayListExtra(AllMusicConst.MUSIC_TITLE)
            val list = ArrayList<UploadMusicListBean>()
            for (item in musicTitle!!){
                val bean = UploadMusicListBean()
                bean.musicTitle = item
                list.add(bean)
            }
            val adapter = UploadMusicListAdapter(this, list)
            mListView?.adapter = adapter
        }
    }

    /**
     * 设置listview
     */
    private fun setListView(){



    }

    /**
     * 将byte[]转为各种进制的字符串
     * @param bytes byte[]
     * @param radix 基数可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    private fun binary(bytes: ByteArray?, radix: Int): String? {
        return BigInteger(1, bytes).toString(radix) // 这里的1代表正数
    }

}