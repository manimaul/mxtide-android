package com.mxmariner.tides.tides.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.tides.R
import kotlinx.android.synthetic.main.station_list_view.view.*


class StationListView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    init {
        LayoutInflater.from(context).inflate(R.layout.station_list_view, this, true)
        if (isInEditMode) {
            stationNameTextView.text = "Station Name"
        }
    }

    fun apply(station: IStation) {
        stationNameTextView.text = station.name
    }
}
