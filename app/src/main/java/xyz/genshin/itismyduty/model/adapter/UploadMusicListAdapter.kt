package xyz.genshin.itismyduty.model.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.bean.UploadMusicListBean

class UploadMusicListAdapter(private val context: Context,
private val list: List<UploadMusicListBean>): BaseAdapter() {
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
            mView = LayoutInflater.from(context).inflate(R.layout.item_upload_music, parent, false)
            val holder = Holder()
            holder.mMusicTitle = mView.findViewById(R.id.text_music_title)
            holder.mMusicProgress = mView.findViewById(R.id.text_music_progress)

            holder.mMusicTitle?.text = list[position].musicTitle

            mView.tag = holder
        }else{
            val holder = mView.tag as Holder
            holder.mMusicTitle?.text = list[position].musicTitle
        }
        return mView
    }

    class Holder{

        var mMusicTitle: TextView? = null
        var mMusicProgress: TextView? = null

    }

}