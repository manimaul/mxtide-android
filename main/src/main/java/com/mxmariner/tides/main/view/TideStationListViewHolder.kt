package com.mxmariner.tides.main.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mxmariner.tides.main.R
import com.mxmariner.tides.model.StationPresentation

private fun inflateView(parent: ViewGroup) : View {
    return LayoutInflater.from(parent.context).inflate(R.layout.tide_list_view_holder, parent, false)
}

class TideStationListViewHolder(
        parent: ViewGroup
) : RecyclerView.ViewHolder(inflateView(parent)) {

    private val stationListView by lazy {
        itemView.findViewById<StationListView>(R.id.stationListView)
    }

    fun apply(presentation: StationPresentation, selection: () -> Unit) {
        stationListView.apply(presentation, selection)
    }
}
