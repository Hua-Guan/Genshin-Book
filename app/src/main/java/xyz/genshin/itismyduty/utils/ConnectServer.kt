package xyz.genshin.itismyduty.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.JsonArray

import com.google.gson.JsonParser

/**
 * @author GuanHua
 */
@Deprecated("已有更好的方案，不在使用此类")
object ConnectServer {

    fun getOverviewImageUri(): JsonArray {

        //请求行
        val urlStr = "https://www.baidu.com/"
        val url = URL(urlStr)
        val urlConnection = url.openConnection()
        val httpURLConnection = urlConnection as HttpURLConnection

        //请求头
        httpURLConnection.doInput = true
        //httpURLConnection.doOutput = true
        httpURLConnection.useCaches = false
        httpURLConnection.requestMethod = "GET"
        httpURLConnection.setRequestProperty("Charset", "UTF-8")
        httpURLConnection.setRequestProperty(
            "Content-Type",
            "application/x-www-form-urlencoded"
        )

        //请求体
        //val os = httpURLConnection.outputStream
        //val param = "request=getOverviewImageUri"
        //os.write(param.toByteArray())

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

        val jsonStr = String(sbf)
        println(jsonStr)
        return JsonParser.parseString(jsonStr).asJsonArray

    }

    fun getAllRoleImageUri(): JsonArray {

        //请求行
        val urlStr = "http://genshin.itismyduty.xyz:8080/GenshinBook/"
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
        val param = "request=getAllRoleImageUri"
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
        val jsonStr = String(sbf)

        return JsonParser.parseString(jsonStr).asJsonArray
    }

    fun getRoleInformationAndImageUri(roleName: String): String {

        //请求行
        val urlStr = "http://genshin.itismyduty.xyz:8080/GenshinBook/"
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
        val param = "request=getRoleInformationAndImageUri&roleName=$roleName"
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
        val jsonStr = String(sbf)

        return jsonStr
    }


}