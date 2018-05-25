package edu.training.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.training.seasonsappkotlin.Episode
import edu.training.seasonsappkotlin.R
import kotlinx.android.synthetic.main.item_episode.view.*

class EpisodesAdapter(val items : ArrayList<Episode>, val context: Context) : RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_episode, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode = items.get(position)
        holder.mTitleTxt.text = episode.title
        holder.mEpisodeNumberTxt.text = "E${episode.number}"
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int = items.size

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val mTitleTxt = view.episode_title_txt!!
        val mEpisodeNumberTxt = view.episode_number_txt!!
    }

}

