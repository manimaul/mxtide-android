package com.mxmariner.tides.extensions

import android.support.v7.widget.RecyclerView

fun RecyclerView.Adapter<*>.isEmpty() : Boolean {
    return this.itemCount == 0
}
