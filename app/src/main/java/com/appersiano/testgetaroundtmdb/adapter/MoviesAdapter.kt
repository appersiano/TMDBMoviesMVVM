package com.appersiano.testgetaroundtmdb.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.appersiano.testgetaroundtmdb.R
import com.appersiano.testgetaroundtmdb.activities.MovieDetailActivity
import com.appersiano.testgetaroundtmdb.activities.MovieDetailActivity.Companion.BACKDROP_IMAGE
import com.appersiano.testgetaroundtmdb.activities.MovieDetailActivity.Companion.ORIGINAL_TITLE
import com.appersiano.testgetaroundtmdb.activities.MovieDetailActivity.Companion.OVERVIEW
import com.appersiano.testgetaroundtmdb.repository.model.Movie
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_movie.view.*
import java.lang.Exception

class MoviesAdapter(val mContext: Context, var records: MutableList<Movie?>? = mutableListOf()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var widthPoster: Int = 0

    override fun onBindViewHolder(element: RecyclerView.ViewHolder, position: Int) {
        val movie = records!![position]

        element.itemView.pbLoading.visibility = View.VISIBLE
        Picasso.get()
                .load(mContext.getString(R.string.url_image_baseurl) +
                        getBestWidth(widthPoster) +
                        "/" +
                        movie?.poster_path)
                .error(android.R.drawable.ic_menu_report_image)
                .into(element.itemView.imgPoster, object : Callback {
                    override fun onSuccess() {
                        element.itemView.pbLoading.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        //Already implemented by .error, just hide progessbar
                        element.itemView.pbLoading.visibility = View.GONE
                    }

                })

        element.itemView.setOnClickListener {
            val intentDetail = Intent(mContext, MovieDetailActivity::class.java)
            with(intentDetail) {
                putExtra(BACKDROP_IMAGE, movie?.backdrop_path)
                putExtra(ORIGINAL_TITLE, movie?.original_title)
                putExtra(OVERVIEW, movie?.overview)
            }

            mContext.startActivity(intentDetail)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_movie, parent, false)

        widthPoster = parent.measuredWidth / 2
        val height = parent.measuredHeight / 2
        itemView.layoutParams = LinearLayout.LayoutParams(widthPoster, height)

        return MovieHolder(itemView)
    }

    override fun getItemCount(): Int = records?.size!!

    fun addData(newData: MutableList<Movie?>?) {
        records?.addAll(newData?.toList()!!)
        notifyDataSetChanged()
    }

    fun clearData() = records?.clear()

    private fun getBestWidth(widthPoster: Int): String {
        //Link size supported -> https://www.themoviedb.org/talk/53c11d4ec3a3684cf4006400
        when (widthPoster) {
            in 0..92 -> return "w92"
            in 92..154 -> return "w154"
            in 154..185 -> return "w185"
            in 185..342 -> return "w342"
            in 342..500 -> return "w500"
            in 500..780 -> return "w780"
            else -> return "original"
        }
    }

    inner class MovieHolder(view: View) : RecyclerView.ViewHolder(view)
}
