package xyz.genshin.itismyduty.model.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.bean.AllMusicListBean

class AllMusicListAdapter(private val context: Context
    ,private val list: List<AllMusicListBean>): BaseAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].musicId.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var mView = convertView
        if (mView == null){
            mView = LayoutInflater.from(context).inflate(R.layout.item_all_music_list, parent, false)
            val holder = Holder()
            holder.textMusicTitle = mView.findViewById(R.id.music_title)
            holder.textMusicSize = mView.findViewById(R.id.music_size)
            holder.isChosenMusic = mView.findViewById(R.id.choose_music)

            holder.textMusicTitle?.text = list[position].musicTitle
            holder.textMusicSize?.text = list[position].musicSize
            if (list[position].isChosen){
                holder.isChosenMusic?.setImageResource(R.drawable.ic_choose)
            }else{
                holder.isChosenMusic?.setImageResource(R.drawable.ic_unchoose)
            }

            mView.tag = holder

        }else{
            val holder = mView.tag as Holder
            holder.textMusicTitle?.text = list[position].musicTitle
            holder.textMusicSize?.text = list[position].musicSize
            if (list[position].isChosen){
                holder.isChosenMusic?.setImageResource(R.drawable.ic_choose)
            }else{
                holder.isChosenMusic?.setImageResource(R.drawable.ic_unchoose)
            }
        }
        return mView
    }

    class Holder {

        var textMusicTitle: TextView? = null
        var textMusicSize: TextView? = null
        var isChosenMusic: ImageView? = null

    }

}