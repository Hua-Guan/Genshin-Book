package xyz.genshin.itismyduty.model.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.bean.MeListBean

class MeListAdapter(private var context: Context,
                    private var list: List<MeListBean>): BaseAdapter() {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if (view == null){

            view = LayoutInflater.from(context).inflate(R.layout.item_me_list, parent, false)
            val holder = Holder()
            holder.itemImage = view.findViewById(R.id.item_img)
            holder.itemName = view.findViewById(R.id.item_name)
            holder.itemGo = view.findViewById(R.id.item_go)

            holder.itemImage.setImageResource(list[position].itemImageUri)

            holder.itemName.text = list[position].itemName

            holder.itemGo.setImageResource(list[position].itemGo)

            view.tag = holder
        }else {
            val holder = view.tag as Holder

            holder.itemImage.setImageResource(list[position].itemImageUri)

            holder.itemName.text = list[position].itemName

            holder.itemGo.setImageResource(list[position].itemGo)
        }
        return view
    }

    class Holder{

        lateinit var itemImage: ImageView
        lateinit var itemName: TextView
        lateinit var itemGo: ImageView

    }

}