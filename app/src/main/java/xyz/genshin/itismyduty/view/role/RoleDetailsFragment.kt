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

/**
 * @author GuanHua
 */
class RoleDetailsFragment : Fragment() {
    private var mView: View? = null
    private var roleName: String? = null

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
        val tab: TabLayout = view.findViewById(R.id.tab_role)
        val pager = view.findViewById<ViewPager2>(R.id.pager)
        val adapter = arguments?.getString("roleName")?.let { RolePagerViewAdapter(this, it) }

        pager.adapter = adapter

        TabLayoutMediator(tab, pager){tab, position ->

            if (position == 0){
                tab.text = "角色信息"
            }else if (position == 1){
                tab.text = "基础属性"
            }else if (position == 2){
                tab.text = "命之座"
            }

        }.attach()
    }
}