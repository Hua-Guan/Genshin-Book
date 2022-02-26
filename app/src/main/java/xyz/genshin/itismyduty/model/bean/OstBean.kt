package xyz.genshin.itismyduty.model.bean

import android.widget.TextView
import android.widget.VideoView
import kotlin.properties.Delegates

class OstBean {
    var mVideoId = 0
    lateinit var mVideoViewUri: TextView
    lateinit var mVideoViewName: TextView
    lateinit var mVideoViewCoverUri: String
    lateinit var mVideoViewTitle: String
    lateinit var mVideoViewDuration: String
}