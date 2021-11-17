package xyz.genshin.itismyduty.model

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import xyz.genshin.itismyduty.R

class OverviewGridViewAdapter(context: Context) : BaseAdapter() {
    private var context: Context = context

    override fun getCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return 1
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if (view == null){
            val holder = Holder()
            view = LayoutInflater.from(context).inflate(R.layout.item_overview, null)
            holder.image = view.findViewById(R.id.image)
            Glide.with(view)
                .load("https://genshin.itismyduty.xyz/Role/role.jpg")
                .into(holder.image)
            convertView?.tag = holder
        }else{


        }
        return view
    }

    class Holder{

        lateinit var image: ImageView


    }

}