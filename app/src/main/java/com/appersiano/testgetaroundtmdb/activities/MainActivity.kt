package com.appersiano.testgetaroundtmdb.activities

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.appersiano.testgetaroundtmdb.R
import com.appersiano.testgetaroundtmdb.adapter.MoviesAdapter
import com.appersiano.testgetaroundtmdb.repository.model.Movie
import com.appersiano.testgetaroundtmdb.util.ConnectionLiveData
import com.appersiano.testgetaroundtmdb.util.ConnectionModel
import com.appersiano.testgetaroundtmdb.util.EndlessGridView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var mViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mViewModel = ViewModelProviders.of(this@MainActivity).get(MainActivityViewModel::class.java)

        val gridLayoutManager = GridLayoutManager(this@MainActivity, 2, GridLayoutManager.VERTICAL, false)
        rvMovies.layoutManager = gridLayoutManager
        rvMovies.adapter = MoviesAdapter(this@MainActivity)
        rvMovies.addOnScrollListener(EndlessGridView(gridLayoutManager) { mViewModel.loadMore() })

        pbMore.visibility = View.GONE

        ConnectionLiveData(applicationContext).observe(this, object : Observer<ConnectionModel> {
            override fun onChanged(connection: ConnectionModel) {
                if (connection.isConnected) {
                    if (gridLayoutManager.itemCount == 0) {
                        mViewModel.getNowPlayingMovies().observe(this@MainActivity, Observer<List<Movie>>
                        { movieList ->
                            (rvMovies.adapter as MoviesAdapter).addData(movieList?.toMutableList())
                        })
                    }
                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.offline_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
        })

        mViewModel.getUpcomingMovies().observe(this@MainActivity, Observer<List<Movie>>
        { movieList ->
            (rvMovies.adapter as MoviesAdapter).addData(movieList?.toMutableList())
        })

        mViewModel.getSearchMovies().observe(this, Observer<List<Movie>>
        { movieList ->
            (rvMovies.adapter as MoviesAdapter).addData(movieList?.toMutableList())
        })

        mViewModel.pbState?.observe(this, Observer<Boolean> { isVisible ->
            when (isVisible) {
                true -> pbMore.visibility = View.VISIBLE
                false -> pbMore.visibility = View.GONE
            }
        })

        mViewModel.showToast?.observe(this, Observer<String> { message ->
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        })

        mViewModel.scrollToTop?.observe(this, Observer<Unit> { _ ->
            rvMovies.layoutManager?.scrollToPosition(0)
        })

        mViewModel.clearData?.observe(this, Observer<Unit> { _ ->
            (rvMovies.adapter as MoviesAdapter).clearData()
        })

        btnNowPlaying.setOnClickListener({
            it -> mViewModel.loadPlayingMovies(true)
        })

        btnGetUpcoming.setOnClickListener({
            it -> mViewModel.upComingClick(true)
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchView = (menu.findItem(R.id.search).actionView as SearchView)
        searchView.apply {

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    mViewModel.searchMoviesByName(query!!)
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    mViewModel.searchMoviesByName(query!!)
                    return true
                }
            })
        }

        val searchItem = menu.findItem(R.id.search)
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                //hide keyboard
                val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(rvMovies.windowToken, 0)

                searchView.setQuery("", false)
                mViewModel.closeSearch()
                return true
            }

            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }
        })

        return true
    }

    companion object {
        val TAG = this::class.java.simpleName
    }
}