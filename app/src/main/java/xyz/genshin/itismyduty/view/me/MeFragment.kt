package xyz.genshin.itismyduty.view.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.genshin.itismyduty.R

class MeFragment: Fragment() {

    private var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_me, container, false)
        }

        return mView
    }
}