package xyz.genshin.itismyduty.view.me

import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.FavoriteAlbumAdapter

class FavoriteActivity: AppCompatActivity() {

    //返回键
    private var mBack: ImageView? = null
    //收藏列表
    private var mFavoriteList: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        initView()
        setBack()
        setFavoriteList()
    }

    private fun initView(){
        mBack = findViewById(R.id.back)
        mFavoriteList = findViewById(R.id.list_favorite_album)
    }

    /**
     * 设置当按下返回键时的监听
     */
    private fun setBack(){
        mBack?.setOnClickListener {
            finish()
        }
    }

    /**
     * 加载收藏文件夹
     */
    private fun setFavoriteList(){
        val adapter = FavoriteAlbumAdapter(this)
        mFavoriteList?.adapter = adapter
    }

}