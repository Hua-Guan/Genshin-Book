package xyz.genshin.itismyduty.view.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.bean.OverviewBean
import xyz.genshin.itismyduty.model.adapter.OverviewGridViewAdapter
import xyz.genshin.itismyduty.utils.VolleyInstance
import xyz.genshin.itismyduty.view.enemy.EnemyActivity
import xyz.genshin.itismyduty.view.ost.OstActivity
import xyz.genshin.itismyduty.view.role.RoleActivity

/**
 * @author GuanHua
 */
class HomeFragment: Fragment() {

    companion object{
        const val ROLE_ACTIVITY = 0
        const val ENEMY_ACTIVITY = 1
        const val OST_ACTIVITY = 2
    }

    private var mView: View? = null
    private var overview: GridView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_home, container, false)
        }
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (overview == null){

            overview = view.findViewById(R.id.gv_overview)

            setImageFromServer()

            overview?.setOnItemClickListener { parent, view, position, id ->

                if (position == ROLE_ACTIVITY){

                    val intent = Intent(activity, RoleActivity::class.java)
                    startActivity(intent)

                }else if (position == ENEMY_ACTIVITY){

                    val intent = Intent(activity, EnemyActivity::class.java)
                    startActivity(intent)

                }else if (position == OST_ACTIVITY){

                    val intent = Intent(activity, OstActivity::class.java)
                    startActivity(intent)

                }

            }

        }

    }

    private fun setImageFromServer() {

        val stringRequest = StringRequest(
            Request.Method.GET,
            "http://genshin.itismyduty.xyz:8080/GenshinBook?request=getOverviewImageUri",
            { response ->
                val jsonArray = JsonParser.parseString(response).asJsonArray
                val overviewRoleBean = OverviewBean()
                var overviewBean = Gson().fromJson(jsonArray[1], OverviewBean::class.java)
                overviewRoleBean.imageUri =
                    "https://genshin.itismyduty.xyz/" + overviewBean.imageUri
                overviewRoleBean.typeName = "角色"

                val overviewEnemyBean = OverviewBean()
                overviewBean = Gson().fromJson(jsonArray[0], OverviewBean::class.java)
                overviewEnemyBean.imageUri =
                    "https://genshin.itismyduty.xyz/" + overviewBean.imageUri
                overviewEnemyBean.typeName = "敌人"

                val overviewOstBean = OverviewBean()
                overviewOstBean.imageUri = "https://genshin.itismyduty.xyz/" + overviewBean.imageUri
                overviewOstBean.typeName = "OST"

                val list = ArrayList<OverviewBean>()
                list.add(overviewRoleBean)
                list.add(overviewEnemyBean)
                list.add(overviewOstBean)

                val adapter = context?.let { OverviewGridViewAdapter(it, list) }
                overview?.adapter = adapter
            }, { })
        context?.let { VolleyInstance.getInstance(it.applicationContext).addToRequestQueue(stringRequest) }
    }

}