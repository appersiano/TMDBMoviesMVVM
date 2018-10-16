package com.appersiano.testgetaroundtmdb.activities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.appersiano.testgetaroundtmdb.repository.MovieListener
import com.appersiano.testgetaroundtmdb.repository.TMDBRepository
import com.appersiano.testgetaroundtmdb.repository.model.Movie

class MainActivityViewModel : ViewModel() {

    private var flagSearch = false
    private lateinit var lastSearchMovie: String

    private var currentPageNowPlaying: Long = 1
    private var currentPageUpcoming: Long = 1
    private var currentPageSearch: Long = 1

    private var nowPlayingMovies: MutableLiveData<List<Movie>>? = MutableLiveData()
    private var upcomingMovies: MutableLiveData<List<Movie>>? = MutableLiveData()
    private var searchMovies: MutableLiveData<List<Movie>>? = MutableLiveData()

    var pbState: MutableLiveData<Boolean>? = MutableLiveData()
    var showToast: MutableLiveData<String>? = MutableLiveData()
    val scrollToTop: MutableLiveData<Unit>? = MutableLiveData()
    val clearData: MutableLiveData<Unit>? = MutableLiveData()

    fun getNowPlayingMovies(): LiveData<List<Movie>> {
        loadPlayingMovies(false)
        return nowPlayingMovies as MutableLiveData<List<Movie>>
    }

    fun getUpcomingMovies(): LiveData<List<Movie>> {
        return upcomingMovies as MutableLiveData<List<Movie>>
    }

    fun getSearchMovies(): LiveData<List<Movie>> {
        return searchMovies as MutableLiveData<List<Movie>>
    }

    public fun loadPlayingMovies(clear : Boolean) {
        if (clear) clearData?.postValue(Unit)

        flagSearch = false
        pbState?.postValue(true)
        TMDBRepository.nowPlaying(currentPageNowPlaying, object : MovieListener {
            override fun movie(records: List<Movie?>?) {
                Log.d(MainActivity.TAG, "Movies found" + records?.size)
                pbState?.postValue(false)
                nowPlayingMovies?.postValue(records as List<Movie>?)
            }

            override fun error(error: Throwable) {
                Log.e(MainActivity.TAG, error.toString())
                pbState?.postValue(false)
                showToast?.postValue(error.toString())
            }
        })
    }

    fun searchMoviesByName(movieName: String) {
        flagSearch = true
        lastSearchMovie = movieName

        if (movieName.length >= 3) {
            if (currentPageSearch == 1L) {
                scrollToTop?.postValue(Unit)
                clearData?.postValue(Unit)
            }

            pbState?.postValue(true)
            TMDBRepository.searchMovie(movieName, currentPageSearch, object : MovieListener {
                override fun movie(records: List<Movie?>?) {
                    pbState?.postValue(false)
                    Log.d(MainActivity.TAG, "Movies found" + records?.size)
                    if (records?.size == 0) {
                        showToast?.postValue("No nowPlayingMovies found")
                    }
                    searchMovies?.postValue(records as List<Movie>?)
                }

                override fun error(error: Throwable) {
                    Log.e(MainActivity.TAG, error.toString())
                    pbState?.postValue(false)
                    showToast?.postValue(error.toString())
                }
            })
        }
    }

    fun upComingClick(clear : Boolean){
        if (clear) clearData?.postValue(Unit)

        flagSearch = false
        pbState?.postValue(true)
        TMDBRepository.upComing(currentPageUpcoming, object : MovieListener {
            override fun movie(records: List<Movie?>?) {
                Log.d(MainActivity.TAG, "Movies found" + records?.size)
                pbState?.postValue(false)
                upcomingMovies?.postValue(records as List<Movie>?)
            }

            override fun error(error: Throwable) {
                Log.e(MainActivity.TAG, error.toString())
                pbState?.postValue(false)
                showToast?.postValue(error.toString())
            }
        })
    }

    fun loadMore() {
        when (flagSearch) {
            true -> {
                currentPageSearch++
                searchMoviesByName(lastSearchMovie)
            }
            false -> {
                currentPageNowPlaying++
                loadPlayingMovies(false)
            }
        }
    }

    fun closeSearch() {
        when (flagSearch) {
            true -> {
                clearData?.postValue(Unit)
                currentPageNowPlaying = 1
                loadPlayingMovies(false)
            }
        }
    }
}