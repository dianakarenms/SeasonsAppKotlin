package edu.training.seasonsappkotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import com.android.volley.Request
import com.android.volley.Response
import com.squareup.picasso.Picasso
import edu.training.adapters.EpisodesAdapter
import edu.training.api.ApiConfig
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity: AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {

    private var mIsTheTitleVisible           = false
    private var mIsTheTitleContainerVisible  = true
    private lateinit var mEpisodesAdapter: EpisodesAdapter
    private lateinit var mSeason: Season
    private var mEpisodesList = ArrayList<Episode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        bindActivity()
    }

    private fun bindActivity() {
        mSeason = intent.getSerializableExtra(EXTRA_SEASON) as Season

        Picasso.get()
                .load("http://image.tmdb.org/t/p/w185" + mSeason.thumbnail)
                .resize(400, 578)
                .centerCrop()
                .placeholder(R.drawable.season_background_placeholder)
                .error(R.drawable.season_background_placeholder)
                .into(main_background_img as ImageView)
        Picasso.get()
                .load("http://image.tmdb.org/t/p/w185" + mSeason.thumbnail)
                .resize(400, 578)
                .centerCrop()
                .placeholder(R.drawable.serie_thumbnail_placeholder)
                .error(R.drawable.serie_thumbnail_placeholder)
                .into(thumbnail_img as ImageView)

        toolbar_title_txt.text = mSeason.title
        details_title_txt.text = mSeason.title
        details_episodes_txt.text = "Episodes: $mSeason.airedEpisodes"
        details_votes_txt.text = "Votes: $mSeason.votes"
        val formattedRating = String.format("%.1f", mSeason.rating)
        details_rating_txt.text =  formattedRating

        back_btn.setOnClickListener { finish() }

        val recyclerView = recyclerview_coordinator_behavior as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        mEpisodesAdapter = EpisodesAdapter(mEpisodesList, this)
        recyclerView.adapter = mEpisodesAdapter

        main_appbar.addOnOffsetChangedListener(this)
        startAlphaAnimation(toolbar_title_txt, 0, View.INVISIBLE)
        loadSeasonEpisodes()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val maxScroll = appBarLayout?.totalScrollRange!!
        val percentage: Float = (Math.abs(verticalOffset) / maxScroll).toFloat()

        handleAlphaOnTitle(percentage)
        handleToolbarTitleVisibility(percentage)
        handleToolbarVisibility(percentage)
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if(percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if(!mIsTheTitleVisible) {
                startAlphaAnimation(toolbar_title_txt, ALPHA_ANIMATIONS_DURATION, View.VISIBLE)
                mIsTheTitleVisible = true
            }
        } else {
            if(mIsTheTitleVisible) {
                startAlphaAnimation(toolbar_title_txt, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE)
                mIsTheTitleVisible = false
            }
        }
    }

    private fun handleAlphaOnTitle(percentage: Float) {
        if(percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(main_linearlayout_title, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE)
                mIsTheTitleContainerVisible = false
            }
        } else {
            if(!mIsTheTitleContainerVisible) {
                startAlphaAnimation(main_linearlayout_title, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE)
                mIsTheTitleContainerVisible = true
            }
        }
    }

    private fun handleToolbarVisibility(percentage: Float) {
        if(percentage >= PERCENTAGE_TO_SHOW_TOOLBAR)
            main_linearlayout_title.visibility = View.VISIBLE
        else
            main_linearlayout_title.visibility = View.INVISIBLE
    }

    private fun startAlphaAnimation(v: View, duration: Int, visibility: Int) {
        val alphaAnimation: AlphaAnimation =
                if(visibility == View.VISIBLE)
                    AlphaAnimation(0f, 1f)
                else AlphaAnimation(1f, 0f)

        alphaAnimation.duration = duration.toLong()
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }

    private fun loadSeasonEpisodes() {
        val url = String.format(ApiConfig.seasons, mSeason.number)
        ApiClient.getInstance(this).addToRequestQueue(ApiClient.GsonRequest(
                url,
                Array<Episode>::class.java,
                Request.Method.GET,
                ApiConfig.getTraktHeaders(),
                null,
                Response.Listener { response ->
                    mEpisodesList.addAll(response)
                    mEpisodesAdapter.notifyDataSetChanged()
                },
                Response.ErrorListener { error ->
                    Log.e("Load Seasons", error.message)
                },
                true
        ))
    }

    companion object {
        private const val EXTRA_SEASON = "CoordinatorActivity.Season"

        private const val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR     = 0.9f
        private const val PERCENTAGE_TO_SHOW_TOOLBAR              = 0.4f
        private const val PERCENTAGE_TO_HIDE_TITLE_DETAILS        = 0.3f
        private const val ALPHA_ANIMATIONS_DURATION               = 200

        fun newIntent(packageContext: Context, item: Season) : Intent {
            val intent = Intent(packageContext, DetailsActivity::class.java)
            intent.putExtra(EXTRA_SEASON, item)
            return intent
        }
    }
}