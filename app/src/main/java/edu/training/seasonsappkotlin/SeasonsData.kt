package edu.training.seasonsappkotlin

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Episode(
        @SerializedName("number") val number: Int,
        @SerializedName("title") val title: String
) : Serializable

data class Ids(
        @SerializedName("trakt") val trakt: Int,
        @SerializedName("tmdb") val tmbd: Int
) : Serializable

data class Season(
        @SerializedName("number") val number: Int,
        @SerializedName("ids") val ids: Ids,
        @SerializedName("rating") val rating: Float,
        @SerializedName("aired_episodes") val airedEpisodes: Int,
        @SerializedName("title") val title: String,
        @SerializedName("votes") val votes: String,
        var thumbnail: String
) : Serializable

data class TmdbSeason(
        @SerializedName("id") val id: Int,
        @SerializedName("poster_path") val posterPath: String
) : Serializable

data class TmdbShow(
        @SerializedName("seasons") val seasons: ArrayList<TmdbSeason>
) : Serializable
