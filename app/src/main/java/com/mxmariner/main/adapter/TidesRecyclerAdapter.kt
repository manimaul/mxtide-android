package com.mxmariner.main.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mxmariner.main.view.TideStationListViewHolder
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.tides.factory.StationPresentationFactory
import com.mxmariner.tides.routing.RouteStationDetails
import com.mxmariner.tides.routing.Router
import javax.inject.Inject

class TidesRecyclerAdapter @Inject constructor(
    private val presentationFactory: StationPresentationFactory,
    private val router: Router
) : RecyclerView.Adapter<TideStationListViewHolder>() {

    private val stationList = ArrayList<IStation>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TideStationListViewHolder {
        return TideStationListViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return stationList.count()
    }

    override fun onBindViewHolder(holder: TideStationListViewHolder, position: Int) {
        val station = stationList[position]
        holder.apply(presentationFactory.createPresentation(station)) {
            router.routeTo(RouteStationDetails(station))
        }
    }

    fun add(stations: List<IStation>) {
        val start = stationList.count()
        stationList += stations
        notifyItemRangeInserted(start, stations.count())
    }
}