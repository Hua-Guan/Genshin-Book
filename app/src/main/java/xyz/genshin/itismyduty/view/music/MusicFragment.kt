package xyz.genshin.itismyduty.view.music

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import xyz.genshin.itismyduty.R

/**
 * @author GuanHua
 */
class MusicFragment: Fragment() {

    private var mView: View? =null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_music, container, false)
        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val video = view.findViewById<VideoView>(R.id.video)
        video.setMediaController(MediaController(context))
        video.setVideoURI(Uri.parse("https://genshin.itismyduty.xyz/OST/1.mp4"))
        video.start()
    }
}