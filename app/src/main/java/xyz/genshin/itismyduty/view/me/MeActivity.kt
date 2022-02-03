package xyz.genshin.itismyduty.view.me

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.MeListAdapter
import xyz.genshin.itismyduty.model.bean.MeListBean

class MeActivity: AppCompatActivity() {

    companion object{

        const val HISTORY_BEAN_ITEM_NAME = "最近浏览"
        const val FAVORITE_BEAN_ITEM_NAME = "我的收藏"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_me)
        setLoginOrRegister()
        setMeListView()
    }

    private fun setLoginOrRegister(){

        val loginOrRegister = findViewById<Button>(R.id.login_or_register)
        loginOrRegister.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setMeListView(){

        val list = ArrayList<MeListBean>()

        val historyBean = MeListBean()
        historyBean.itemImageUri = R.drawable.ic_history
        historyBean.itemName = HISTORY_BEAN_ITEM_NAME
        historyBean.itemGo = R.drawable.ic_to_right
        list.add(historyBean)

        val favoriteBean = MeListBean()
        favoriteBean.itemImageUri = R.drawable.ic_favorites
        favoriteBean.itemName = FAVORITE_BEAN_ITEM_NAME
        favoriteBean.itemGo = R.drawable.ic_to_right
        list.add(favoriteBean)

        val meListView = findViewById<ListView>(R.id.me_list)
        val adapter = MeListAdapter(this, list)
        meListView.adapter = adapter

    }

}