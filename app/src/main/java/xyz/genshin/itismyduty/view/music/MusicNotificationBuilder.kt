package xyz.genshin.itismyduty.view.music

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import xyz.genshin.itismyduty.R

class MusicNotificationBuilder(val context: Context, val channel: String): NotificationCompat.Builder(context, channel) {

    init {
        setSmallIcon(R.drawable.ic_play)
    }
}