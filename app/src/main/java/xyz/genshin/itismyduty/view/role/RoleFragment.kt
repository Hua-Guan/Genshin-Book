package xyz.genshin.itismyduty.view.role

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.MysqlConnect
import xyz.genshin.itismyduty.model.RoleBean
import xyz.genshin.itismyduty.model.RoleGridViewAdapter
import kotlin.concurrent.thread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RoleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RoleFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var list: List<RoleBean>? = null
    private var handler = Handler(Looper.myLooper()!!)
    private var adapter: RoleGridViewAdapter? = null
    private lateinit var gridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_role, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridView = view.findViewById(R.id.role)


        list = ArrayList()
        thread {

            var conn = MysqlConnect.getMysqlConnect()
            var stmt = conn?.createStatement()
            var sql = "select RoleName, RoleUrl from role"
            var rs = stmt?.executeQuery(sql)
            if (rs != null) {
                while (rs.next()){
                    var role = RoleBean()
                    role.roleName = rs.getString("RoleName")
                    role.roleUri = "https://genshin.itismyduty.xyz/" + rs.getString("RoleUrl")
                    (list as ArrayList<RoleBean>).add(role)
                }
                adapter = context?.let { RoleGridViewAdapter(it, list as ArrayList<RoleBean>) }
                handler.post(Runnable {

                    gridView.adapter = adapter

                })
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RoleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RoleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}