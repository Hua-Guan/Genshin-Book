package xyz.genshin.itismyduty.view.role

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.MysqlConnect
import kotlin.concurrent.thread

class RoleAttributesFragment(private val roleName: String): Fragment() {

    private var mView: View? = null
    private val handler = Handler(Looper.myLooper()!!)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null){

            mView = inflater.inflate(R.layout.fragment_role_attributes, container, false)

        }
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData(view)
    }

    private fun setData(view: View){

        val mAttackType = view.findViewById<TextView>(R.id.m_attack_type)
        val mHP = view.findViewById<TextView>(R.id.m_HP)
        val mATK = view.findViewById<TextView>(R.id.m_ATK)
        val mDEF = view.findViewById<TextView>(R.id.m_DEF)
        val mElementalMastery = view.findViewById<TextView>(R.id.m_elemental_mastery)
        val mCRITRate = view.findViewById<TextView>(R.id.m_CRIT_rate)
        val mCRITDMG = view.findViewById<TextView>(R.id.m_CRIT_DMG)
        val mHealingBonus = view.findViewById<TextView>(R.id.m_healing_bonus)
        val mIncomingHealingBonus = view.findViewById<TextView>(R.id.m_incoming_healing_bonus)
        val mEnergyRecharge = view.findViewById<TextView>(R.id.m_energy_recharge)

        thread {

            val conn = MysqlConnect.getMysqlConnect()
            val stmt = conn?.createStatement()
            val sql = "select AttackType, HP, ATK, DEF, ElementalMastery, CRITRate, CRITDMG, HealingBonus," +
                    " IncomingHealingBonus, EnergyRecharge from basicattributes where RoleName = '$roleName'"
            val rs = stmt?.executeQuery(sql)
            rs?.next()

            handler.post(Runnable {

                if (rs != null){

                    mAttackType.text = rs.getString("AttackType")
                    mHP.text = rs.getString("HP")
                    mATK.text = rs.getString("ATK")
                    mDEF.text = rs.getString("DEF")
                    mElementalMastery.text = rs.getString("ElementalMastery")
                    mCRITRate.text = rs.getString("CRITRate")
                    mCRITDMG.text = rs.getString("CRITDMG")
                    mHealingBonus.text = rs.getString("HealingBonus")
                    mIncomingHealingBonus.text = rs.getString("IncomingHealingBonus")
                    mEnergyRecharge.text = rs.getString("EnergyRecharge")

                }

            })


        }


    }

}