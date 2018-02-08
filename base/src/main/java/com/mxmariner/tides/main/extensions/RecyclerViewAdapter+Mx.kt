package com.mxmariner.tides.main.extensions

import android.support.v7.widget.RecyclerView

internal fun RecyclerView.Adapter<*>.isEmpty() : Boolean {
    return this.itemCount == 0
}
