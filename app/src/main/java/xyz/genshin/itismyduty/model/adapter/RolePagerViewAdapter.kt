package xyz.genshin.itismyduty.model.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.genshin.itismyduty.view.role.RoleAttributesFragment
import xyz.genshin.itismyduty.view.role.RoleConstellationFragment
import xyz.genshin.itismyduty.view.role.RoleInformationFragment
import xyz.genshin.itismyduty.view.role.RoleTalentFragment

/**
 * @author GuanHua
 */
class RolePagerViewAdapter(fragment: Fragment, private val roleName: String): FragmentStateAdapter(fragment) {

    companion object{

        const val COUNT = 4
        const val ROLE_INFORMATION = 0
        const val ROLE_ATTRIBUTE = 1
        const val ROLE_CONSTELLATION = 2
        const val ROLE_TALENT = 3

    }

    override fun getItemCount(): Int {

        return COUNT
    }

    override fun createFragment(position: Int): Fragment {

        if (position == ROLE_INFORMATION){

            return RoleInformationFragment(roleName)

        }else if (position == ROLE_ATTRIBUTE){

            return RoleAttributesFragment(roleName)

        }else if (position == ROLE_CONSTELLATION){

            return RoleConstellationFragment()

        }else if (position == ROLE_TALENT){

            return RoleTalentFragment()

        }

        return RoleInformationFragment(roleName)
    }
}