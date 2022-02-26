package xyz.genshin.itismyduty.model.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.*
import android.widget.*
import xyz.genshin.itismyduty.R

class OstAdapter(private val context: Context): BaseAdapter() {
    companion object{
        const val TEST_URI = "http://home.itismyduty.xyz:500/y2meta.com-City%20of%20Winds%20and%20Idylls%20-%20Disc%202_%20The%20Horizon%20of%20Dandelion%EF%BD%9CGenshin%20Impact-(1080p).mp4"
    }
    private var isFistLoadVideo = true
    override fun getCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var mView = convertView
        if (mView == null){
            mView = LayoutInflater.from(context).inflate(R.layout.item_ost, parent, false)
            val mHolder = Holder()
            mHolder.mVideoView = mView.findViewById(R.id.video)
            mHolder.mVideoName = mView.findViewById(R.id.video_name)

            val layoutParams = LinearLayout.LayoutParams(getScreenWidth(), (0.56*getScreenWidth()).toInt())
            Log.i("width", getScreenWidth().toString())
            mHolder.mVideoView.layoutParams = layoutParams
            //
            val mMediaControl = MediaController(context)
            mHolder.mVideoView.setVideoURI(Uri.parse(TEST_URI))
            mHolder.mVideoView.setMediaController(mMediaControl)
            mMediaControl.show(3000)

            mHolder.mVideoView.setOnTouchListener(object : View.OnTouchListener{
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (event != null) {
                        if (event.action == MotionEvent.ACTION_DOWN){
                            mMediaControl.show()
                        }
                    }
                    return true
                }

            })

            mHolder.mVideoName.text = "test"

            mView.tag = mHolder
        }else{
            val mHolder = mView.tag as Holder

        }
        return mView
    }

    private fun getScreenWidth(): Int{
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.width
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private fun dip2px(context: Context, dpValue: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    class Holder{
        lateinit var mVideoView: VideoView
        lateinit var mVideoName: TextView
    }

}