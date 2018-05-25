package edu.training.seasonsappkotlin

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import edu.training.adapters.SeasonRecyclerAdapter
import edu.training.api.ApiConfig
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {
    private var adapter: SeasonRecyclerAdapter? = null
    private var seasonList = ArrayList<Season>()
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.drawable.ic_action)

        adapter = SeasonRecyclerAdapter(seasonList, this)
        val numColumns: Int =
                if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2
                else 3
//        val recycler = findViewById<RecyclerView>(R.id.main_recycler)
        season_recycler.layoutManager = GridLayoutManager(context, numColumns)
        season_recycler.itemAnimator = DefaultItemAnimator()
        season_recycler.adapter = adapter!!

        loadSeasons()
    }

    private fun loadSeasons() {
        val url: String = String.format(ApiConfig.seasons, "")
        ApiClient.getInstance(this).addToRequestQueue(ApiClient.GsonRequest(
                url,
                Array<Season>::class.java,
                Request.Method.GET,
                ApiConfig.getTraktHeaders(),
                null,
                Response.Listener<Array<Season>> { response ->
                    seasonList.addAll(response)
                    loadThumbnails()
                }, Response.ErrorListener { error -> error.printStackTrace() }, true
        ))
    }

    private fun loadThumbnails() {
        ApiClient.getInstance(this).addToRequestQueue(ApiClient.GsonRequest(
                ApiConfig.thumbnails,
                TmdbShow::class.java,
                Request.Method.GET,
                null,
                null,
                Response.Listener<TmdbShow> { response ->
                    val seasonsThumbnails = response.seasons
                    for (i in seasonsThumbnails.indices) {
                        seasonList[i].thumbnail = seasonsThumbnails[i].posterPath
                    }
                    adapter!!.notifyDataSetChanged()
                },
                Response.ErrorListener { error -> error.printStackTrace() },
                true
        ))
    }
}