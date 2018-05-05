package com.mxmariner.tides.main.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.mxmariner.tides.main.R
import com.mxmariner.tides.model.StationPresentation
import kotlinx.android.synthetic.main.station_list_view.view.*


class StationListView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(R.layout.station_list_view, this, true)
    }

    fun apply(presentation: StationPresentation, selection: () -> Unit) {
        icon.setImageResource(presentation.icon)
        stationName.text = presentation.name
        position.text = presentation.position
        presentation.timeZone.toTimeZone()?.displayName?.let {
            stationTimeZoneTitle.visibility = View.VISIBLE
            stationTimeZone.visibility = View.VISIBLE
            stationTimeZone.text = it
        } ?: {
            stationTimeZoneTitle.visibility = View.GONE
            stationTimeZone.visibility = View.GONE
        }()

        lineChart.applyPresentation(presentation)
        detailsButton.setOnClickListener { selection() }
    }
}
