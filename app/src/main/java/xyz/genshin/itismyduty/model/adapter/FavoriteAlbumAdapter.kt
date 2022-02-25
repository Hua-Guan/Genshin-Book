package xyz.genshin.itismyduty.model.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import xyz.genshin.itismyduty.R

class FavoriteAlbumAdapter(private val context: Context): BaseAdapter() {
    override fun getCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var mView = convertView
        if (mView == null){
            mView = LayoutInflater.from(context).inflate(R.layout.item_favorite_album, parent, false)
            val mHolder = Holder()
            mHolder.textFavoriteAlbumName = mView.findViewById(R.id.favorite_album_name)
            mHolder.textFavoriteAlbumSum = mView.findViewById(R.id.favorite_album_sum)

            mHolder.textFavoriteAlbumName.text = "默认"
            mHolder.textFavoriteAlbumSum.text = "0个内容"

            mView.tag = mHolder
        }else {
            val mHolder = mView.tag as Holder
            mHolder.textFavoriteAlbumName.text = "默认"
            mHolder.textFavoriteAlbumSum.text = "0个内容"
        }
        return mView
    }

    class Holder{
        lateinit var textFavoriteAlbumName: TextView
        lateinit var textFavoriteAlbumSum: TextView
    }

}