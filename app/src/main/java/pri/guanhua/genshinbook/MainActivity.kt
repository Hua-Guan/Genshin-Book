package pri.guanhua.genshinbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class MainActivity : AppCompatActivity() {
    private val handle = Handler(Looper.myLooper()!!)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val image = findViewById<ImageView>(R.id.image)
        val thread = Thread(Runnable {

            handle.post(Runnable {
                Glide.with(this).load("https://itismyduty.xyz/sl.jpg")
                    .timeout(600000).into(image)
            })

        })

        thread.start()

    }
}