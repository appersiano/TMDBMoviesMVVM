package com.appersiano.testgetaroundtmdb.repository

import com.appersiano.testgetaroundtmdb.repository.model.Movie
import com.appersiano.testgetaroundtmdb.repository.model.MoviesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

object TMDBRepository {

    private var service: ITMDBService = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITMDBService::class.java)

    /*
    I leave here the API key just for the project, we must obfuscate it in production with an
    appropriate tool like DexGuard or hide it with the NDK
    */
    private val API_KEY = "*"

    /*
    We should track the total pages available in nowPlaying and searchmovie to warn the user when
    he scroll down, leave it as is just for the project
    */
    fun nowPlaying(page: Long, listener: MovieListener) {
        service.nowPlaying(API_KEY, page).enqueue(object : Callback<MoviesResponse> {
            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                listener.error(t)
            }

            override fun onResponse(call: Call<MoviesResponse>, response: Response<MoviesResponse>) {
                listener.movie(records = response.body()?.results)
            }
        })
    }


    fun upComing(page: Long, listener: MovieListener) {
        service.upComing(API_KEY, page).enqueue(object : Callback<MoviesResponse> {
            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                listener.error(t)
            }

            override fun onResponse(call: Call<MoviesResponse>, response: Response<MoviesResponse>) {
                listener.movie(records = response.body()?.results)
            }
        })
    }

    private var call: Call<MoviesResponse>? = null
    fun searchMovie(query: String, page: Long, listener: MovieListener) {
        //Avoid multiple http request
        if (call != null && call!!.isExecuted) {
            call?.cancel()
        }

        call = service.searchMovie(API_KEY, query, page)
        call!!.enqueue(object : Callback<MoviesResponse> {
            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                if (t is IOException) {
                    //multiple call! Don't show anything
                } else {
                    listener.error(t)
                }
            }

            override fun onResponse(call: Call<MoviesResponse>, response: Response<MoviesResponse>) {
                listener.movie(records = response.body()?.results)
            }
        })
    }
}

//region Interfaces
interface ITMDBService {
    @GET("movie/now_playing")
    fun nowPlaying(@Query("api_key") api_key: String, @Query("page") page: Long): Call<MoviesResponse>

    @GET("movie/upcoming")
    fun upComing(@Query("api_key") api_key: String, @Query("page") page: Long): Call<MoviesResponse>

    @GET("search/movie")
    fun searchMovie(@Query("api_key") api_key: String, @Query("query") query: String, @Query("page") page: Long): Call<MoviesResponse>
}

interface MovieListener {
    fun movie(records: List<Movie?>?)
    fun error(error: Throwable)
}
//endregion