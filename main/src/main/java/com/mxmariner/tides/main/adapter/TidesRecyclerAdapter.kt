package com.mxmariner.tides.main.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.tides.factory.StationPresentationFactory
import com.mxmariner.tides.main.view.TideStationListViewHolder
import com.mxmariner.tides.routing.RouteStationDetails
import com.mxmariner.tides.routing.Router

class TidesRecyclerAdapter (kodein: Kodein) : RecyclerView.Adapter<TideStationListViewHolder>() {

    private val presentationFactory: StationPresentationFactory = kodein.instance()
    private val router: Router = kodein.instance()

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