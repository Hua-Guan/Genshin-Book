package xyz.genshin.itismyduty.model.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.SeekBar

class MusicStateUpdateBroadcastReceiver(private val seekBar: SeekBar): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            seekBar.progress = intent.getIntExtra("test", 0)
        }
    }
}