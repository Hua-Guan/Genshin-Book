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

    companion object{

        const val TAB_HOME = "主页"
        const val TAB_HOME_POSITION = 0
        const val TAB_MUSIC = "音乐"
        const val TAB_MUSIC_POSITION = 1
        const val TAB_ME = "我"
        const val TAB_ME_POSITION = 2

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tab = findViewById<TabLayout>(R.id.tab)
        val pager = findViewById<ViewPager2>(R.id.pager)
        val adapter = MainTabPagerViewAdapter(this)
        pager.adapter = adapter

        TabLayoutMediator(tab, pager){tab, position ->

            if (position == TAB_HOME_POSITION){
                tab.text = TAB_HOME
            }else if (position == TAB_MUSIC_POSITION){
                tab.text = TAB_MUSIC
            }else if (position == TAB_ME_POSITION){
                tab.text = TAB_ME
            }

        }.attach()

    }


}