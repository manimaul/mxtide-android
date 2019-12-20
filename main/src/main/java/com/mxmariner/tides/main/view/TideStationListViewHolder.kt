package com.mxmariner.tides.main.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mxmariner.tides.R
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
