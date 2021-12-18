package xyz.genshin.itismyduty.model

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.genshin.itismyduty.view.history.HistoryFragment
import xyz.genshin.itismyduty.view.home.HomeFragment
import xyz.genshin.itismyduty.view.me.MeFragment

class MainTabPagerViewAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 0){

            return HomeFragment()

        }else if (position == 1){
            return HistoryFragment()
        }else if (position == 2){
            return MeFragment()
        }
        return HomeFragment()
    }


}