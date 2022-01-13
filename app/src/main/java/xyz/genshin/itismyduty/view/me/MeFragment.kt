package xyz.genshin.itismyduty.view.me

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.MeListAdapter
import xyz.genshin.itismyduty.model.bean.MeListBean


/**
 * @author GuanHua
 */
class MeFragment: Fragment() {

    companion object{

        const val HISTORY_BEAN_ITEM_NAME = "最近浏览"
        const val FAVORITE_BEAN_ITEM_NAME = "我的收藏"

    }

    private var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_me, container, false)
        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLoginOrRegister(view)

        setMeListView(view)
    }

    private fun setLoginOrRegister(mView: View){

        var loginOrRegister = mView.findViewById<Button>(R.id.login_or_register)
        loginOrRegister.setOnClickListener {

            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setMeListView(mView: View){

        val list = ArrayList<MeListBean>()

        val historyBean = MeListBean()
        historyBean.itemImageUri = R.drawable.ic_history
        historyBean.itemName = HISTORY_BEAN_ITEM_NAME
        historyBean.itemGo = R.drawable.ic_to_right
        list.add(historyBean)

        val favoriteBean = MeListBean()
        favoriteBean.itemImageUri = R.drawable.ic_favorites
        favoriteBean.itemName = FAVORITE_BEAN_ITEM_NAME
        favoriteBean.itemGo = R.drawable.ic_to_right
        list.add(favoriteBean)

        val meListView = mView.findViewById<ListView>(R.id.me_list)
        val adapter = context?.let { MeListAdapter(it, list) }
        meListView.adapter = adapter

    }

}