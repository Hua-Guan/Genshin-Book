package xyz.genshin.itismyduty.model.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.bean.MusicGridBean

class MusicGridAdapter(private val context: Context,
                        private val list: List<MusicGridBean>): BaseAdapter() {
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
        var mView = convertView
        if (mView == null){

            mView = LayoutInflater.from(context).inflate(R.layout.item_music_grid, parent, false)
            val holder = Holder()
            holder.mMusicImage = mView.findViewById(R.id.image_music)
            holder.mMusicName = mView.findViewById(R.id.text_music_name)
            holder.mMusicAuthor = mView.findViewById(R.id.text_music_author)

//            Glide.with(mView)
//                .setDefaultRequestOptions(
//                    RequestOptions()
//                        .frame(1000)
//                        .centerCrop()
//                )
//                .load(list[position].mMusicVideoUri)
//                .into(holder.mMusicImage)

            Glide.with(mView)
                .load(list[position].mMusicImageUri)
                .into(holder.mMusicImage)

            holder.mMusicImage.clipToOutline = true

            holder.mMusicName.text = list[position].mMusicName
            holder.mMusicAuthor.text = list[position].mMusicAuthor

            mView.tag = holder

        }else {
            val holder = mView.tag as Holder

            Glide.with(mView)
                .load(list[position].mMusicImageUri)
                .into(holder.mMusicImage)

            holder.mMusicName.text = list[position].mMusicName
            holder.mMusicAuthor.text = list[position].mMusicAuthor
        }
        return mView
    }

    class Holder{

        lateinit var mMusicImage: ImageView
        lateinit var mMusicName: TextView
        lateinit var mMusicAuthor: TextView

    }

}