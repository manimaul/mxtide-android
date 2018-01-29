package com.mxmariner.tides.tides.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.tides.settings.Preferences
import com.mxmariner.tides.tides.view.TideStationListViewHolder
import org.joda.time.DateTime
import org.joda.time.Duration
import javax.inject.Inject


class TidesRecyclerAdapter @Inject constructor(private val preferences: Preferences) : RecyclerView.Adapter<TideStationListViewHolder>() {

    private val stationList = ArrayList<IStation>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TideStationListViewHolder {
        return TideStationListViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return stationList.count()
    }

    override fun onBindViewHolder(holder: TideStationListViewHolder?, position: Int) {
        val prediction = stationList[position].getPredictionRaw(DateTime.now().minusHours(3), Duration.standardHours(6), preferences.predictionLevels)
        holder?.apply(stationList[position], prediction)
    }

    fun add(stations: List<IStation>) {
        val start = stationList.count()
        stationList += stations
        notifyItemRangeInserted(start, stations.count())
    }
}