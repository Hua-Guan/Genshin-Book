package xyz.genshin.itismyduty.view.home

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.viewpager2.widget.ViewPager2
import xyz.genshin.itismyduty.R

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import xyz.genshin.itismyduty.model.adapter.MainTabPagerViewAdapter
import xyz.genshin.itismyduty.server.MusicService
import xyz.genshin.itismyduty.server.MyService
import xyz.genshin.itismyduty.utils.Tools


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

    @RequiresApi(Build.VERSION_CODES.O)
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

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "gg"
            val descriptionText = "ff"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}