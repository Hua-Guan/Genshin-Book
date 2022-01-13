package xyz.genshin.itismyduty.model.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.genshin.itismyduty.view.music.MusicFragment
import xyz.genshin.itismyduty.view.home.HomeFragment
import xyz.genshin.itismyduty.view.me.MeFragment

/**
 * @author GuanHua
 */
class MainTabPagerViewAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {

    companion object{
        const val ALL_TAB = 3
        const val HOME_FRAGMENT = 0
        const val HISTORY_FRAGMENT = 1
        const val ME_FRAGMENT = 2
    }

    override fun getItemCount(): Int {
        return ALL_TAB
    }

    override fun createFragment(position: Int): Fragment {
        if (position == HOME_FRAGMENT){

            return HomeFragment()

        }else if (position == HISTORY_FRAGMENT){
            return MusicFragment()
        }else if (position == ME_FRAGMENT){
            return MeFragment()
        }
        return HomeFragment()
    }


}