package xyz.genshin.itismyduty.view.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.GridView
import com.google.gson.Gson
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.OverviewBean
import xyz.genshin.itismyduty.model.OverviewGridViewAdapter
import xyz.genshin.itismyduty.utils.ConnectServer
import xyz.genshin.itismyduty.view.enemy.EnemyActivity
import xyz.genshin.itismyduty.view.role.RoleActivity
import kotlin.concurrent.thread

/**
 * @author GuanHua
 */
class MainActivity : AppCompatActivity() {
    private val handle = Handler(Looper.myLooper()!!)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val overview = findViewById<GridView>(R.id.gv_overview)

        thread {
            ConnectServer.getAllRoleImageUri()

            val jsonArray = ConnectServer.getOverviewImageUri()
            val overviewRoleBean = OverviewBean()
            var overviewBean = Gson().fromJson(jsonArray[1], OverviewBean::class.java)
            println(overviewBean)
            overviewRoleBean.imageUri = "https://genshin.itismyduty.xyz/" + overviewBean.imageUri
            overviewRoleBean.typeName = "角色"

            val overviewEnemyBean = OverviewBean()
            overviewBean = Gson().fromJson(jsonArray[0], OverviewBean::class.java)
            overviewEnemyBean.imageUri = "https://genshin.itismyduty.xyz/" + overviewBean.imageUri
            overviewEnemyBean.typeName = "敌人"

            val list = ArrayList<OverviewBean>()
            list.add(overviewRoleBean)
            list.add(overviewEnemyBean)

            handle.post(Runnable {

                val adapter = OverviewGridViewAdapter(this, list)
                overview.adapter = adapter

            })


        }

        overview.setOnItemClickListener { parent, view, position, id ->

            if (position == 0){

                intent = Intent(this, RoleActivity::class.java)
                startActivity(intent)

            }else if (position == 1){

                intent = Intent(this, EnemyActivity::class.java)
                startActivity(intent)

            }

        }


    }
}