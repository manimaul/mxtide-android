package com.mxmariner.tides.tides.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.tides.main.routing.RouteStationDetails
import com.mxmariner.tides.main.routing.Router
import com.mxmariner.tides.tides.StationListViewPresentationFactory
import com.mxmariner.tides.tides.view.TideStationListViewHolder
import javax.inject.Inject


class TidesRecyclerAdapter @Inject constructor(
        private val presentationFactory: StationListViewPresentationFactory,
        private val router: Router
) : RecyclerView.Adapter<TideStationListViewHolder>() {

    private val stationList = ArrayList<IStation>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TideStationListViewHolder {
        return TideStationListViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return stationList.count()
    }

    override fun onBindViewHolder(holder: TideStationListViewHolder?, position: Int) {
        val station = stationList[position]
        val name = station.name
        holder?.apply(presentationFactory.createPresentation(station)) {
            router.routeTo(RouteStationDetails(name))
        }
    }

    fun add(stations: List<IStation>) {
        val start = stationList.count()
        stationList += stations
        notifyItemRangeInserted(start, stations.count())
    }
}