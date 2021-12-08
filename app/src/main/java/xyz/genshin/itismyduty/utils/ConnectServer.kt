package xyz.genshin.itismyduty.utils

import android.net.Uri.parse
import androidx.core.net.MailTo.parse
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import xyz.genshin.itismyduty.model.OverviewBean
import java.io.BufferedInputStream
import java.io.StringReader
import com.google.gson.JsonArray

import com.google.gson.JsonParser
import java.net.HttpCookie.parse
import kotlin.time.Duration.Companion.parse


object ConnectServer {

    fun getOverviewImageUri(){

        //请求行
        val urlStr = "http://192.168.31.96:8080/GenshinBookServer/"
        val url = URL(urlStr)
        val urlConnection = url.openConnection()
        val httpURLConnection = urlConnection as HttpURLConnection

        //请求头
        httpURLConnection.doInput = true
        httpURLConnection.doOutput = true
        httpURLConnection.useCaches = false
        httpURLConnection.requestMethod = "POST"
        httpURLConnection.setRequestProperty("Charset", "UTF-8")
        httpURLConnection.setRequestProperty(
            "Content-Type",
            "application/x-www-form-urlencoded"
        )

        //请求体
        val os = httpURLConnection.outputStream
        val param = "request=getOverviewImageUri"
        os.write(param.toByteArray())

        //返回
        val iis = httpURLConnection.inputStream
        val bf = BufferedReader(InputStreamReader(iis))
        val sbf = StringBuffer()
        var str: String?
        do {
            str = bf.readLine()
            if (str != null) {
                sbf.append(str)
            } else {
                break
            }
        } while (true)
        // 使用new方法
        val gson = Gson()
        val turnsType = object : TypeToken<List<String>>() {}.type
        var jsonStr = String(sbf)

        //Json的解析类对象
        //将JSON的String 转成一个JsonArray对象
        //将JSON的String 转成一个JsonArray对象
        val jsonArray: JsonArray = JsonParser.parseString(jsonStr).asJsonArray
        println(jsonArray[0])
        println(jsonArray[1])

        //val turns = Gson().fromJson<List<String>>(jsonArray[0], turnsType)
        //println(turns[0])
        // fromJson 将json字符串转为bean对象
        //val ov: OverviewBean = gson.fromJson(sbf.toString(), OverviewBean::class.java)
        //println(ov.imageUri)
        //println(ov.typeName)


    }
    //inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)


}