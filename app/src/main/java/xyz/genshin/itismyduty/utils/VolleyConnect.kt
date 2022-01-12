package xyz.genshin.itismyduty.utils

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.JsonParser

/**
 * @author GuanHua
 */
object VolleyConnect {


    fun getAllRoleImageUri(context: Context){

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, "http://genshin.itismyduty.xyz:8080/GenshinBook/",
            Response.Listener { response ->
                val jsonArray = JsonParser.parseString(response).asJsonArray
            },
            Response.ErrorListener {

            }) {
            override fun getParams(): Map<String, String> {
                val map: MutableMap<String, String> = HashMap()
                map["request"] = "getOverviewImageUri"
                return map
            }
        }

        VolleyInstance.getInstance(context.applicationContext).addToRequestQueue(stringRequest)


    }

}