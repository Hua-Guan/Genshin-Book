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
import com.bumptech.glide.Glide
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.MysqlConnect
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

        thread {
            val conn = MysqlConnect.getMysqlConnect()
            val stmt = conn?.createStatement()
            val mStmt = conn?.createStatement()
            val sql = "select RoleUrl from role where RoleName = '$roleName'"
            val mSql = "select Affiliation, Vision, WeaponType, Constellation, " +
                    "Birthday, Title, Introduction from roleinformation where " +
                    "RoleName = '$roleName'"
            val rs = stmt?.executeQuery(sql)
            val mRs = mStmt?.executeQuery(mSql)
            rs?.next()
            mRs?.next()
            handler.post(Runnable {
                //加载图片
                if (rs != null) {
                    Glide.with(view)
                        .load("https://genshin.itismyduty.xyz/" + rs.getString("RoleUrl"))
                        .into(roleImage)
                    roleImage.clipToOutline = true
                }
                //加载其他数据
                if (mRs != null) {
                    mAffiliation.text = mRs.getString("Affiliation")
                    mVision.text = mRs.getString("Vision")
                    mWeaponType.text = mRs.getString("WeaponType")
                    mConstellation.text = mRs.getString("Constellation")
                    mBirthday.text = mRs.getString("Birthday")
                    mTitle.text = mRs.getString("Title")
                    mIntroduction.text = mRs.getString("Introduction")
                }

            })
        }

    }

}