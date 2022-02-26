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
import xyz.genshin.itismyduty.model.bean.RoleBean

/**
 * @author GuanHua
 */
class RoleGridViewAdapter constructor(
    private var context: Context,
    private var list: List<RoleBean>
    ): BaseAdapter() {


    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if (view == null){
            val holder = Holder()
            view = LayoutInflater.from(context).inflate(R.layout.item_role, parent, false)
            holder.roleImage = view.findViewById(R.id.role_image)
            holder.roleName = view.findViewById(R.id.role_name)
            view.tag = holder

            Glide.with(view)
                .load(list[position].roleUri)
                .into(holder.roleImage)

            holder.roleImage.clipToOutline = true

            holder.roleName.text = list[position].roleName
        }else{

            val holder = view.tag as Holder
            Glide.with(view)
                .load(list[position].roleUri)
                .into(holder.roleImage)

            holder.roleName.text = list[position].roleName

        }

        return view
    }

    class Holder{

        lateinit var roleImage: ImageView
        lateinit var roleName: TextView

    }

}