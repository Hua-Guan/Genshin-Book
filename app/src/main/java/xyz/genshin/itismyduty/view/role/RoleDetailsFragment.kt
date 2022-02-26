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
import xyz.genshin.itismyduty.model.adapter.RolePagerViewAdapter

/**
 * @author GuanHua
 */
class RoleDetailsFragment : Fragment() {

    companion object{

        const val ROLE_INFORMATION = 0
        const val ROLE_INFORMATION_TEXT = "角色信息"
        const val BASIC_ATTRIBUTES = 1
        const val BASIC_ATTRIBUTES_TEXT = "基础属性"
        const val CONSTELLATION = 2
        const val CONSTELLATION_TEXT = "命之座"
        const val TALENT = 3
        const val TALENT_TEXT = "天赋"

    }

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

            if (position == ROLE_INFORMATION){
                tab.text = ROLE_INFORMATION_TEXT
            }else if (position == BASIC_ATTRIBUTES){
                tab.text = BASIC_ATTRIBUTES_TEXT
            }else if (position == CONSTELLATION){
                tab.text = CONSTELLATION_TEXT
            }else if (position == TALENT){
                tab.text = TALENT_TEXT
            }

        }.attach()
    }
}