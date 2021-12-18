package xyz.genshin.itismyduty.view.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.GridView
import android.widget.TableLayout
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.google.gson.Gson
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.OverviewBean
import xyz.genshin.itismyduty.model.OverviewGridViewAdapter
import xyz.genshin.itismyduty.utils.ConnectServer
import xyz.genshin.itismyduty.view.enemy.EnemyActivity
import xyz.genshin.itismyduty.view.role.RoleActivity
import kotlin.concurrent.thread
import com.android.volley.VolleyError

import com.android.volley.Response

import org.json.JSONObject

import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonParser
import xyz.genshin.itismyduty.model.MainTabPagerViewAdapter
import xyz.genshin.itismyduty.utils.VolleyInstance


/**
 * @author GuanHua
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tab = findViewById<TabLayout>(R.id.tab)
        val pager = findViewById<ViewPager2>(R.id.pager)
        val adapter = MainTabPagerViewAdapter(this)
        pager.adapter = adapter

        TabLayoutMediator(tab, pager){tab, position ->

            if (position == 0){
                tab.text = "主页"
            }else if (position == 1){
                tab.text = "历史"
            }else if (position == 2){
                tab.text = "我"
            }

        }.attach()

    }


}