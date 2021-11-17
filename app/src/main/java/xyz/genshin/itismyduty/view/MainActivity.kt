package xyz.genshin.itismyduty.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.GridView
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.OverviewGridViewAdapter

class MainActivity : AppCompatActivity() {
    private val handle = Handler(Looper.myLooper()!!)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val overview = findViewById<GridView>(R.id.gv_overview)
        val adapter = OverviewGridViewAdapter(this)
        overview.adapter = adapter

    }
}