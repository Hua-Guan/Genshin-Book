package xyz.genshin.itismyduty.view.role

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.RolePagerViewAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RoleDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RoleDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mView: View? = null
    private var roleName: String? = null

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
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_role_details, container, false)
        }
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRoleName(view)

        back(view)

        setPagerView(view)
    }

    private fun setRoleName(view: View){

        val roleName = view.findViewById<TextView>(R.id.role_name)
        roleName.text = arguments?.getString("roleName")

    }

    private fun back(view: View){

        val back = view.findViewById<ImageView>(R.id.back)
        back.setOnClickListener {

            findNavController().popBackStack()

        }

    }

    private fun setPagerView(view: View){
        var tab: TabLayout = view.findViewById(R.id.tab_role)
        val pager = view.findViewById<ViewPager2>(R.id.pager)
        val adapter = arguments?.getString("roleName")?.let { RolePagerViewAdapter(this, it) }

        pager.adapter = adapter

        TabLayoutMediator(tab, pager){tab, position ->

            if (position == 0){
                tab.text = "角色信息"
            }else if (position == 1){
                tab.text = "基础属性"
            }

        }.attach()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RoleDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RoleDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}