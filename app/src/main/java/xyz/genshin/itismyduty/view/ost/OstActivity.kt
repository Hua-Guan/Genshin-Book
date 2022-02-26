package xyz.genshin.itismyduty.view.ost

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.OstAdapter
import xyz.genshin.itismyduty.model.bean.OstBean

class OstActivity: AppCompatActivity() {

    private lateinit var mOstListView: ListView
    //
    private var list = ArrayList<OstBean>()

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
        val bean = OstBean()
        list.add(bean)
        val adapter = OstAdapter(this, list)
        mOstListView.adapter = adapter
    }

}