package edu.training.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import edu.training.seasonsappkotlin.Season
import android.view.View
import com.squareup.picasso.Picasso
import edu.training.seasonsappkotlin.DetailsActivity
import edu.training.seasonsappkotlin.R
import kotlinx.android.synthetic.main.item_season_thumbnail.view.*

class SeasonRecyclerAdapter(val items: ArrayList<Season>, val context: Context) : RecyclerView.Adapter<SeasonRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
                    LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_season_thumbnail, parent, false)
            )

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val season = items[position]
        holder.bind(season)
        holder.itemView.setOnClickListener {
            context.startActivity(DetailsActivity.newIntent(context, season))
        }
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(season: Season) {
            view.season_txt!!.text = season.title
            view.ep_count_txt!!.text = "Episodes: ${season.airedEpisodes}"
            val formatedRating = String.format("%.2f", season.rating)
            view.rating_txt!!.text = "Rating: $formatedRating"
            Picasso.get()
                    .load("http://image.tmdb.org/t/p/w185" + season.thumbnail)
                    .resize(400, 578)
                    .placeholder(R.drawable.serie_thumbnail_placeholder)
                    .error(R.drawable.season_background_placeholder)
                    .into(view.thumbnail_img)
        }
    }
}
