package xyz.genshin.itismyduty.view.music

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.MusicGridAdapter
import xyz.genshin.itismyduty.model.bean.MusicGridBean

/**
 * @author GuanHua
 */
class MusicFragment: Fragment() {

    companion object{

        const val TEST_URI = "https://genshin.itismyduty.xyz/music.jpg"

    }

    private var mView: View? = null
    private var mGrid: GridView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_music, container, false)
        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView?.let { initView(it) }
        setGridView()
        showMusicList()
    }

    private fun initView(mView: View){

        mGrid = mView.findViewById(R.id.grid_music)

    }

    private fun setGridView(){

        val mList = ArrayList<MusicGridBean>()
        val mBean = MusicGridBean()
        mBean.mMusicImageUri = TEST_URI
        mBean.mMusicAuthor = "33"
        mBean.mMusicName = "44"
        mList.add(mBean)

        val mAdapter = activity?.let { MusicGridAdapter(it, mList) }

        mGrid?.adapter = mAdapter

    }

    private fun showMusicList(){

        mGrid?.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id ->
            val intent = Intent(activity, MusicListActivity::class.java)
            startActivity(intent)
        }

    }

}