package edu.training.api

import android.net.Uri
import edu.training.seasonsappkotlin.BuildConfig

class ApiConfig {
    companion object {

        private const val seriesInfo = BuildConfig.TRAKT_API_URL + "shows/breaking-bad/"
        const val seasons = seriesInfo + "seasons/%1\$s?extended=full"
        //val thumbnails =  BuildConfig.TMDB_API_URL + "1396?api_key=fb7bb23f03b6994dafc674c074d01761&language=en-US&include_image_language=en,null"

        val thumbnails = (Uri.parse(BuildConfig.TMDB_API_URL).buildUpon()
                .appendPath("1396")
                .appendQueryParameter("api_key", "fb7bb23f03b6994dafc674c074d01761")
                .appendQueryParameter("language", "en-US")
                .appendQueryParameter("include_image_language", "en")
                .build()!!).toString()

        /**
         * Authenticated header for all api requests, makes use of users' access token
         * @return headers
         */
        fun getTraktHeaders() : HashMap<String, String> {
            return hashMapOf(
                    "Content-Type" to "application/json",
                    "trakt-api-key" to BuildConfig.TRAKT_API_KEY,
                    "trakt-api-version" to "2")
        }
    }
}