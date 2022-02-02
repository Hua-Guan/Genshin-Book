package xyz.genshin.itismyduty.model.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat

class LongPressHomeBroadcastReceiver: BroadcastReceiver() {
    companion object{
        private const val MUSIC_DATA = "music_data"
        private const val PLAY_STATE = "PLAY_STATE"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        val reason = intent?.getStringExtra("reason")
        if (reason.equals("recentapps")){
            context?.getSharedPreferences(MUSIC_DATA, MediaBrowserServiceCompat.MODE_PRIVATE)?.edit()
                ?.putInt(PLAY_STATE, PlaybackStateCompat.STATE_PAUSED)?.apply()
        }
    }
}