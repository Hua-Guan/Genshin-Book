package xyz.genshin.itismyduty.model

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import xyz.genshin.itismyduty.R
import java.util.zip.Inflater

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

            Glide.with(view)
                .load(list[position].itemImageUri)
                .into(holder.itemImage)

            holder.itemName.text = list[position].itemName

            Glide.with(view)
                .load(list[position].itemGo)
                .into(holder.itemGo)
        }
        return view
    }

    class Holder{

        lateinit var itemImage: ImageView
        lateinit var itemName: TextView
        lateinit var itemGo: ImageView

    }

}