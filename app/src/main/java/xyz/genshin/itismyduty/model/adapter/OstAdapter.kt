package xyz.genshin.itismyduty.model.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.transition.Transition
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.bean.OstBean

class OstAdapter(private val context: Context,
                private val list: List<OstBean>): BaseAdapter() {
    companion object{
        const val TEST_URI = "https://genshin.itismyduty.xyz/OST/1.mp4"
        const val URI_IMAGE = "https://genshin.itismyduty.xyz/sl2.jpg"
    }
    private var isFistLoadVideo = true
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].mVideoId.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var mView = convertView
        if (mView == null){
            mView = LayoutInflater.from(context).inflate(R.layout.item_ost, parent, false)
            val mHolder = Holder()
            mHolder.mVideoView = mView.findViewById(R.id.video)
            mHolder.mVideoName = mView.findViewById(R.id.video_name)
            mHolder.mVideoPlay = mView.findViewById(R.id.img_video_play)

            //val layoutParams = LinearLayout.LayoutParams(getScreenWidth(), (0.56*getScreenWidth()).toInt())
            //mHolder.mVideoView.layoutParams = layoutParams
            //
            val mMediaControl = MediaController(context)
            mHolder.mVideoView.setVideoURI(Uri.parse(TEST_URI))
            mHolder.mVideoView.setMediaController(mMediaControl)
            Glide.with(context).asBitmap().load(URI_IMAGE)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        val drawable = BitmapDrawable(resource)
                        mHolder.mVideoView.background = drawable
                    }
                })
            mMediaControl.show(3000)

            mHolder.mVideoView.setOnTouchListener(object : View.OnTouchListener{
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (event != null) {
                        if (event.action == MotionEvent.ACTION_DOWN){
                            mHolder.mVideoView.background = null
                            mHolder.mVideoPlay.background = null
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
        lateinit var mVideoPlay: ImageView
    }

}