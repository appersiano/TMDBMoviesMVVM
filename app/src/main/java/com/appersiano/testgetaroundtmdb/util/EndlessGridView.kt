package com.appersiano.testgetaroundtmdb.util

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class EndlessGridView(private val gridLayoutManager: GridLayoutManager, var block : () -> Unit) : RecyclerView.OnScrollListener() {

    private var previousItemCount: Int = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) {
            val itemCount = gridLayoutManager.itemCount

            if (gridLayoutManager.findLastVisibleItemPosition() >= itemCount - 1) {
                previousItemCount = itemCount
                block.invoke()
            }
        }
    }
}