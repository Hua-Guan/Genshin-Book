package xyz.genshin.itismyduty.view.ost

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.OstAdapter

class OstActivity: AppCompatActivity() {

    private lateinit var mOstListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ost)
        initView()
        setOstListView()
    }

    private fun initView(){
        mOstListView = findViewById(R.id.ost_list)
    }

    private fun setOstListView(){
        val adapter = OstAdapter(this)
        mOstListView.adapter = adapter
    }

}