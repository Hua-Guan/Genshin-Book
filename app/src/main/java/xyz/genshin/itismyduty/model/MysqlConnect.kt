package xyz.genshin.itismyduty.model

import java.sql.Connection
import java.sql.DriverManager

/**
 * @author GuanHua
 */
object MysqlConnect {

    //单例模式
    private var conn: Connection? = null

    fun getMysqlConnect(): Connection? {
        if (conn == null) {
            val url =
                "jdbc:mysql://45.77.1.79:3306/genshinbook?characterEncoding=utf8&verifyServerCertificate=false&useSSL=false"
            val user = "genshin-user"
            val password = "123456"
            Class.forName("com.mysql.jdbc.Driver").newInstance()
            conn = DriverManager.getConnection(url, user, password)
        }
        return conn
    }

}