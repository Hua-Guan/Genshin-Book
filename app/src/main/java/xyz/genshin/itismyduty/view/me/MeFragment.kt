package xyz.genshin.itismyduty.view.me

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import xyz.genshin.itismyduty.R


/**
 * @author GuanHua
 */
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLoginOrRegister(view)

    }

    private fun setLoginOrRegister(mView: View){

        var loginOrRegister = mView.findViewById<Button>(R.id.login_or_register)
        loginOrRegister.setOnClickListener {

            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}