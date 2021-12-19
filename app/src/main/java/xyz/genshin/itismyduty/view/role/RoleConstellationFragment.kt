package xyz.genshin.itismyduty.view.role

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.genshin.itismyduty.R

/**
 * @author GuanHua
 */
class RoleConstellationFragment: Fragment() {

    private var mView: View? =null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null){

            mView = inflater.inflate(R.layout.fragment_role_constellation, container, false)

        }
        return mView
    }
}