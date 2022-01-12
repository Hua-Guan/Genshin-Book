package xyz.genshin.itismyduty.model.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.VideoView
import xyz.genshin.itismyduty.R

class OstListAdapter(private var context: Context,
                     private var list: List<OstListAdapter>): BaseAdapter() {
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

            view = LayoutInflater.from(context).inflate(R.layout.item_ost_list, parent, false)
            val holder = Holder()
            holder.videoView = view.findViewById(R.id.video)
            holder.videoName = view.findViewById(R.id.video_name)

        }
        return view
    }
    class Holder{

        lateinit var videoView: VideoView
        lateinit var videoName: TextView

    }
}