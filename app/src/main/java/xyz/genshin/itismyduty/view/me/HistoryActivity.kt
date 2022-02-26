package xyz.genshin.itismyduty.view.me

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import xyz.genshin.itismyduty.R

class HistoryActivity: AppCompatActivity() {

    //返回键
    private var mBack: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        initView()
        setBack()
    }

    private fun initView(){
        mBack = findViewById(R.id.back)
    }

    /**
     * 设置当按下返回键时的监听
     */
    private fun setBack(){
        mBack?.setOnClickListener {
            finish()
        }
    }

}