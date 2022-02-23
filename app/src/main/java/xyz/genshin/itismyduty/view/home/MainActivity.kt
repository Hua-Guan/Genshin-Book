package xyz.genshin.itismyduty.view.home

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import xyz.genshin.itismyduty.R

import xyz.genshin.itismyduty.view.me.MeFragment
import xyz.genshin.itismyduty.view.music.MusicFragment


/**
 * @author GuanHua
 */
class MainActivity : AppCompatActivity() {

    private val home = HomeFragment()
    private val music = MusicFragment()
    val me = MeFragment()

    private lateinit var imageHome: ImageView
    private lateinit var imageMusic: ImageView
    private lateinit var imageMe: ImageView

    private lateinit var textHome: TextView
    private lateinit var textMusic: TextView
    private lateinit var textMe: TextView

    private val textYellow = Color.parseColor("#f4ea2a")
    private val textDefault = Color.parseColor("#8A000000")


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        initHomeFragment()

        setClickListener()

        createNotificationChannel()
    }

    private fun initView(){
        imageHome = findViewById(R.id.img_home)
        imageMusic = findViewById(R.id.img_music)
        imageMe = findViewById(R.id.img_me)

        textHome = findViewById(R.id.text_home)
        textMusic = findViewById(R.id.text_music)
        textMe = findViewById(R.id.text_me)
    }

    private fun initHomeFragment(){
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.container_view, home)

            imageHome.setImageResource(R.drawable.ic_home_yellow)
            textHome.setTextColor(textYellow)

            imageMusic.setImageResource(R.drawable.ic_music)
            textMusic.setTextColor(textDefault)

            imageMe.setImageResource(R.drawable.ic_me)
            textMe.setTextColor(textDefault)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setClickListener(){
        imageHome.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.container_view, home)
                imageHome.setImageResource(R.drawable.ic_home_yellow)
                textHome.setTextColor(textYellow)

                imageMusic.setImageResource(R.drawable.ic_music)
                textMusic.setTextColor(textDefault)

                imageMe.setImageResource(R.drawable.ic_me)
                textMe.setTextColor(textDefault)
            }
        }
        imageMusic.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.container_view, music)
                imageHome.setImageResource(R.drawable.ic_home)
                textHome.setTextColor(textDefault)

                imageMusic.setImageResource(R.drawable.ic_music_yellow)
                textMusic.setTextColor(textYellow)

                imageMe.setImageResource(R.drawable.ic_me)
                textMe.setTextColor(textDefault)
            }
        }
        imageMe.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.container_view, me)
                imageHome.setImageResource(R.drawable.ic_home)
                textHome.setTextColor(textDefault)

                imageMusic.setImageResource(R.drawable.ic_music)
                textMusic.setTextColor(textDefault)

                imageMe.setImageResource(R.drawable.ic_me_yellow)
                textMe.setTextColor(textYellow)
            }
        }
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