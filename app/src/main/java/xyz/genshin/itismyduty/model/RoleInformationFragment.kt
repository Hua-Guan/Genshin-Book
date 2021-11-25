package xyz.genshin.itismyduty.model

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import xyz.genshin.itismyduty.R
import kotlin.concurrent.thread

class RoleInformationFragment(private val roleName: String): Fragment() {

    var mView: View? = null
    val handler = Handler(Looper.myLooper()!!)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null){

            mView = inflater.inflate(R.layout.fragment_role_information, container, false)

        }
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val roleImage = view.findViewById<ImageView>(R.id.role_image)
        thread {
            val conn = MysqlConnect.getMysqlConnect()
            val stmt = conn?.createStatement()
            val sql = "select RoleUrl from role where RoleName = '$roleName'"
            val rs = stmt?.executeQuery(sql)
            rs?.next()
            handler.post(Runnable {
                if (rs != null) {
                    Glide.with(view)
                        .load("https://genshin.itismyduty.xyz/" + rs.getString("RoleUrl"))
                        .into(roleImage)
                }
            })

        }

    }



}