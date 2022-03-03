package xyz.genshin.itismyduty.view.music

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.model.adapter.MusicGridAdapter
import xyz.genshin.itismyduty.model.bean.MusicAlbumGridBean
import xyz.genshin.itismyduty.server.MusicConst
import xyz.genshin.itismyduty.utils.VolleyInstance

/**
 * @author GuanHua
 */
class MusicFragment: Fragment() {

    companion object{

        const val TEST_URI = "https://genshin.itismyduty.xyz/music.jpg"

    }

    private var mView: View? = null
    private var mGrid: GridView? = null

    private val mHandle = Handler(Looper.myLooper()!!)

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
        //setMusicControlFragment()
    }

    private fun initView(mView: View){

        mGrid = mView.findViewById(R.id.grid_music)

    }

    /**
     * 从服务器中拿到数据并更新
     */
    private fun setGridView(){
        val mList = ArrayList<MusicAlbumGridBean>()
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, "http://genshin.itismyduty.xyz:8080/GenshinBook/music",
            Response.Listener { response ->
                val string = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                val jsonArray = JsonParser.parseString(string).asJsonArray
                for (item in jsonArray){
                    val bean = Gson().fromJson(item, MusicAlbumGridBean::class.java)
                    mList.add(bean)
                }
                mHandle.post {
                    val mAdapter = activity?.let { MusicGridAdapter(it, mList) }
                    mGrid?.adapter = mAdapter
                }
            },
            Response.ErrorListener {
            }) {
            override fun getParams(): Map<String, String> {
                val map: MutableMap<String, String> = HashMap()
                map["request"] = "getMusicAlbum"
                return map
            }
        }
        context?.let { VolleyInstance.getInstance(it.applicationContext).addToRequestQueue(stringRequest) }
    }

    private fun showMusicList(){
        mGrid?.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id ->
            val item = mGrid?.getItemAtPosition(position) as MusicAlbumGridBean
            val intent = Intent(activity, MusicListActivity::class.java)
            intent.putExtra(MusicConst.MUSIC_ALBUM, item.albumName)
            startActivity(intent)
        }
    }

}