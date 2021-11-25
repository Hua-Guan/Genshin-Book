package xyz.genshin.itismyduty.model

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class RolePagerViewAdapter(fragment: Fragment, private val roleName: String): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {

        return 7
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = RoleInformationFragment(roleName)

        return fragment
    }
}