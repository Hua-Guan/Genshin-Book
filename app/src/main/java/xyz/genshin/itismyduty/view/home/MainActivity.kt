package xyz.genshin.itismyduty.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import xyz.genshin.itismyduty.R

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import xyz.genshin.itismyduty.model.adapter.MainTabPagerViewAdapter


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