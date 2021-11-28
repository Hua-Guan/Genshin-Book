package xyz.genshin.itismyduty.model

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.genshin.itismyduty.view.role.RoleAttributesFragment
import xyz.genshin.itismyduty.view.role.RoleInformationFragment

/**
 * @author GuanHua
 */
class RolePagerViewAdapter(fragment: Fragment, private val roleName: String): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {

        return 2
    }

    override fun createFragment(position: Int): Fragment {

        if (position == 0){

            return RoleInformationFragment(roleName)

        }else if (position == 1){

            return RoleAttributesFragment(roleName)

        }

        return RoleInformationFragment(roleName)
    }
}