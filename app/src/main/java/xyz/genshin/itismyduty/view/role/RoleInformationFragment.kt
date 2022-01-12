package xyz.genshin.itismyduty.view.role

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonParser
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.MysqlConnect
import xyz.genshin.itismyduty.model.RoleInformationBean
import xyz.genshin.itismyduty.utils.ConnectServer
import xyz.genshin.itismyduty.utils.VolleyInstance
import kotlin.concurrent.thread

/**
 * @author GuanHua
 */
class RoleInformationFragment(private val roleName: String): Fragment() {

    private var mView: View? = null
    private val handler = Handler(Looper.myLooper()!!)

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
        setData(view)
    }

    private fun setData(view: View){

        val roleImage = view.findViewById<ImageView>(R.id.role_image)
        val mAffiliation = view.findViewById<TextView>(R.id.m_affiliation)
        val mVision = view.findViewById<TextView>(R.id.m_vision)
        val mWeaponType = view.findViewById<TextView>(R.id.m_weapon_type)
        val mConstellation = view.findViewById<TextView>(R.id.m_constellation)
        val mBirthday = view.findViewById<TextView>(R.id.m_birthday)
        val mTitle = view.findViewById<TextView>(R.id.m_title)
        val mIntroduction = view.findViewById<TextView>(R.id.m_introduction)

        val stringRequest = StringRequest(
            Request.Method.GET, "http://genshin.itismyduty.xyz:8080/GenshinBook?" +
                    "request=getRoleInformationAndImageUri&roleName=$roleName",
            { response ->
                println(response)
                val roleInformation = Gson().fromJson(response, RoleInformationBean::class.java)

                //加载图片
                Glide.with(view)
                    .load("https://genshin.itismyduty.xyz/" + roleInformation.roleUri)
                    .into(roleImage)
                roleImage.clipToOutline = true

                //加载其他数据
                mAffiliation.text = roleInformation.affiliation
                mVision.text = roleInformation.vision
                mWeaponType.text = roleInformation.weaponType
                mConstellation.text = roleInformation.constellation
                mBirthday.text = roleInformation.birthday
                mTitle.text = roleInformation.title
                mIntroduction.text = roleInformation.introduction

            }, { })

        context?.let { VolleyInstance.getInstance(it.applicationContext).addToRequestQueue(stringRequest) }

    }

}