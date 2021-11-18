package xyz.genshin.itismyduty.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.GridView
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.MysqlConnect
import xyz.genshin.itismyduty.model.OverviewBean
import xyz.genshin.itismyduty.model.OverviewGridViewAdapter
import kotlin.concurrent.thread

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

        thread {
            var mysqlConnect = MysqlConnect.getMysqlConnect()
            var stmt = mysqlConnect?.createStatement()
            var rs = stmt?.executeQuery("select stylename from overview")
            if (rs != null) {
                while (rs.next()){

                    println(rs.getString("stylename"))

                }
            }
        }


    }
}