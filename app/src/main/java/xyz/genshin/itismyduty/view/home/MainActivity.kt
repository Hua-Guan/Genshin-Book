package xyz.genshin.itismyduty.view.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.GridView
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

        val overviewRoleBean = OverviewBean()
        overviewRoleBean.imageUri = "https://genshin.itismyduty.xyz/Role/role.jpg"
        overviewRoleBean.typeName = "角色"

        val overviewEnemyBean = OverviewBean()
        overviewEnemyBean.imageUri = "https://genshin.itismyduty.xyz/Enemy/enemy.jpg"
        overviewEnemyBean.typeName = "敌人"

        val list = ArrayList<OverviewBean>()
        list.add(overviewRoleBean)
        list.add(overviewEnemyBean)

        val adapter = OverviewGridViewAdapter(this, list)
        overview.adapter = adapter

        overview.setOnItemClickListener { parent, view, position, id ->

            if (position == 0){

                thread {
                    ConnectServer.getOverviewImageUri()
                }

                intent = Intent(this, RoleActivity::class.java)
                startActivity(intent)

            }else if (position == 1){

                intent = Intent(this, EnemyActivity::class.java)
                startActivity(intent)

            }

        }


    }
}