package com.mxmariner.tides.extensions

import androidx.recyclerview.widget.RecyclerView


fun RecyclerView.Adapter<*>.isEmpty() : Boolean {
    return this.itemCount == 0
}
