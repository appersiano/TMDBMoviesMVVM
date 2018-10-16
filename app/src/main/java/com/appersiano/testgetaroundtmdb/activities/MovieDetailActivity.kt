package com.appersiano.testgetaroundtmdb.activities

import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie_detail.*
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.appersiano.testgetaroundtmdb.R


class MovieDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        Picasso.get()
                .load(getString(R.string.url_image_baseurl) +
                        getBestWidth(getScreenWidth()) +
                        "/" +
                        intent.getStringExtra(BACKDROP_IMAGE))
                .error(android.R.drawable.ic_menu_report_image)
                .into(imgBackdrop)

        tvTitle.text = intent.getStringExtra(ORIGINAL_TITLE)
        tvDescription.text = intent.getStringExtra(OVERVIEW)
    }

    private fun getBestWidth(widthPoster: Int): String? {
        //Link size supported -> https://www.themoviedb.org/talk/53c11d4ec3a3684cf4006400
        when (widthPoster) {
            in 0..300 -> return "w300"
            in 300..780 -> return "w780"
            in 780..1280 -> return "w1280"
            else -> return "original"
        }
    }

    private fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    companion object {
        val BACKDROP_IMAGE = "BACKDROP_IMAGE"
        val ORIGINAL_TITLE = "ORIGINAL_TITLE"
        val OVERVIEW = "OVERVIEW"
    }

}
