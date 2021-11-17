package xyz.genshin.itismyduty.model

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import xyz.genshin.itismyduty.R

class OverviewGridViewAdapter(context: Context) : BaseAdapter() {
    private var context: Context = context

    override fun getCount(): Int {
        return 5
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return 1
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if (view == null){
            val holder = Holder()
            //必须传入parent, 不然gridview的高度会无效
            view = LayoutInflater.from(context).inflate(R.layout.item_overview, parent, false)
            holder.image = view.findViewById(R.id.image)
            holder.typeName =  view.findViewById(R.id.type_name)
            Glide.with(view)
                .load("https://genshin.itismyduty.xyz/Role/role.jpg")
                .into(holder.image)
            holder.typeName.text = "role"
            view?.tag = holder
        }else{


        }
        return view
    }

    class Holder{

        lateinit var image: ImageView
        lateinit var typeName: TextView

    }

}