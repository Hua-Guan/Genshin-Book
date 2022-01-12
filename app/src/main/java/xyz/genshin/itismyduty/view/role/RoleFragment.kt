package xyz.genshin.itismyduty.view.role

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.MysqlConnect
import xyz.genshin.itismyduty.model.RoleBean
import xyz.genshin.itismyduty.model.RoleGridViewAdapter
import xyz.genshin.itismyduty.utils.ConnectServer
import xyz.genshin.itismyduty.utils.VolleyInstance
import kotlin.concurrent.thread

/**
 * @author GuanHua
 */
class RoleFragment : Fragment() {

    companion object{

        const val GET_ALL_ROLE_IMAGE_URI = "getAllRoleImageUri"
        const val DATABASE_NAME = "GenshinBook"

    }

    private var mView: View? = null
    private var list: List<RoleBean>? = null
    private var handler = Handler(Looper.myLooper()!!)
    private var adapter: RoleGridViewAdapter? = null
    private lateinit var gridView: GridView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_role, container, false)
        }
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        display(view)

        back(view)

    }

    private fun display(view: View){

        if (adapter == null) {
            gridView = view.findViewById(R.id.role)
            list = ArrayList()
            val stringRequest = StringRequest(
                Request.Method.GET, "http://genshin.itismyduty.xyz:8080/$DATABASE_NAME?request=$GET_ALL_ROLE_IMAGE_URI",
                { response ->
                    val jsonArray = JsonParser.parseString(response).asJsonArray
                    for (item in jsonArray){
                        val roleJson = Gson().fromJson(item, RoleBean::class.java)
                        val role = RoleBean()
                        role.roleName = roleJson.roleName
                        role.roleUri = "https://genshin.itismyduty.xyz/" + roleJson.roleUri
                        (list as ArrayList<RoleBean>).add(role)
                    }
                    adapter = context?.let { RoleGridViewAdapter(it, list as ArrayList<RoleBean>) }
                    gridView.adapter = adapter
                }, { })
            activity?.let { VolleyInstance.getInstance(it.applicationContext).addToRequestQueue(stringRequest) }
        }

        gridView.setOnItemClickListener { parent, view, position, id ->

            val bundle = bundleOf("roleName" to (list as ArrayList<RoleBean>)[position].roleName)
                findNavController().navigate(R.id.action_roleFragment_to_roleDetailsFragment, bundle)

        }

    }

    private fun back(view: View){

        val back = view.findViewById<ImageView>(R.id.back)
        back.setOnClickListener {

            activity?.finish()

        }

    }

}