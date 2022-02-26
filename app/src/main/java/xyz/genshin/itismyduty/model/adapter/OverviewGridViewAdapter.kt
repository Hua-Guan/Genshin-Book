package xyz.genshin.itismyduty.model.adapter

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
import xyz.genshin.itismyduty.model.bean.OverviewBean

/**
 * @author GuanHua
 */
class OverviewGridViewAdapter constructor(
    private var context: Context,
    private var list: List<OverviewBean>
    ) : BaseAdapter() {
    //private var context: Context = context
    //private var list: List<OverviewBean> = list

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong()
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
                .load(list[position].imageUri)
                .into(holder.image)
            holder.image.clipToOutline = true
            holder.typeName.text = list.get(position).styleName
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