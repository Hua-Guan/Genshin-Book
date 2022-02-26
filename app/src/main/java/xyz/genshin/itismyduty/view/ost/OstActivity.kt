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
        val bean0 = OstBean()
        bean0.mVideoViewCoverUri = "https://genshin.itismyduty.xyz/OstCover/test0.png"
        bean0.mVideoViewTitle = "《原神》2.5版本PV: [薄樱初绽时]"
        bean0.mVideoViewDuration = "4:26"
        list.add(bean0)
        val bean1 = OstBean()
        bean1.mVideoViewCoverUri = "https://genshin.itismyduty.xyz/OstCover/test1.png"
        bean1.mVideoViewTitle = "《原神》2.4版本PV: [飞彩携流年]"
        bean1.mVideoViewDuration = "4:48"
        list.add(bean1)
        val bean2 = OstBean()
        bean2.mVideoViewCoverUri = "https://genshin.itismyduty.xyz/OstCover/test2.png"
        bean2.mVideoViewTitle = "《原神》2.3版本PV: [皑尘与雪影]"
        bean2.mVideoViewDuration = "4:36"
        list.add(bean2)
        val bean3 = OstBean()
        bean3.mVideoViewCoverUri = "https://genshin.itismyduty.xyz/OstCover/test3.png"
        bean3.mVideoViewTitle = "《原神》2.1版本PV: [韶光抚月，天下人间]"
        bean3.mVideoViewDuration = "4:40"
        list.add(bean3)
        val adapter = OstAdapter(this, list)
        mOstListView.adapter = adapter
    }

}