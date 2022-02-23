package xyz.genshin.itismyduty.model.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import xyz.genshin.itismyduty.server.MusicConst
import xyz.genshin.itismyduty.server.MusicService

class MusicNotificationReceiver(): BroadcastReceiver() {

    private lateinit var mMusicNotificationListener: MusicNotificationListener

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action.equals(MusicConst.STATE_PLAY)){
            mMusicNotificationListener.onClickNotificationPlayOrPause()
        }else if (action.equals(MusicConst.STATE_NEXT)){
            mMusicNotificationListener.onClickNotificationNext()
        }else if (action.equals(MusicConst.STATE_PRE)){
            mMusicNotificationListener.onClickNotificationPre()
        }
    }

    fun setMusicNotificationListener(mMusicNotificationListener: MusicNotificationListener){
        this.mMusicNotificationListener = mMusicNotificationListener
    }

    interface MusicNotificationListener{
        fun onClickNotificationPlayOrPause()
        fun onClickNotificationNext()
        fun onClickNotificationPre()
    }

}