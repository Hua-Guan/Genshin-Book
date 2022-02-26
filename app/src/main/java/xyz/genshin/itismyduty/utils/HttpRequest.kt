package xyz.genshin.itismyduty.utils

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.nio.charset.StandardCharsets

class HttpRequest {

    companion object{
        fun getMusicString(){
            //你需要换成你自己的网址
            //你需要换成你自己的网址
            val urlStr = "https://www.baidu.com/"
            val url = URL(urlStr)
            val urlConnection: URLConnection = url.openConnection()
            val httpURLConnection: HttpURLConnection = urlConnection as HttpURLConnection

            //在写请求头之前要先设置好下面这三
            httpURLConnection.setDoInput(true);
            //httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            //用setRequestProperty方法来写请求头的属性
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //请求体用OutpuStream来传递，在param换成你自己的参数
            //请求体用OutpuStream来传递，在param换成你自己的参数
            //val os: OutputStream = httpURLConnection.outputStream
            //val param = "login=root&password=123456"
           // os.write(param.toByteArray())

            val `is`: InputStream = httpURLConnection.inputStream
            val bf = BufferedReader(InputStreamReader(`is`, StandardCharsets.UTF_8))
            var str: String?
            while (bf.readLine().also { str = it } != null) {
                println(str)
            }

        }
    }

}