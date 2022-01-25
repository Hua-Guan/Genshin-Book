package xyz.genshin.itismyduty.model.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import xyz.genshin.itismyduty.server.MusicService

class MusicLatestPositionBroadcastReceiver(private val mMediaPlayer: MediaPlayer): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        MusicService.mMusicCurrentPosition = mMediaPlayer.currentPosition
        println("444444444:" + mMediaPlayer.currentPosition)
    }
}