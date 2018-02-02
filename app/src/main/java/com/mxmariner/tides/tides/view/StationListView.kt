package com.mxmariner.tides.tides.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mxmariner.tides.R
import com.mxmariner.tides.main.extensions.hoursToDateTime
import com.mxmariner.tides.main.extensions.unixTimeHours
import com.mxmariner.tides.tides.model.StationListViewPresentation
import kotlinx.android.synthetic.main.station_list_view.view.*


class StationListView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private val description = Description()

    init {
        LayoutInflater.from(context).inflate(R.layout.station_list_view, this, true)
        description.text = ""
    }

    fun apply(presentation: StationListViewPresentation, selection: () -> Unit) {
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

        val entries = presentation.prediction.map {
            val hours = it.date.unixTimeHours
            Entry(hours, it.value)
        }
        val lineDataSet = LineDataSet(entries, "")
        val color = ContextCompat.getColor(context, R.color.tideColor)
        lineDataSet.setDrawValues(false)
        lineDataSet.setColors(color)
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor = color
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.xAxis.granularity = 2.0f
        lineChart.legend.isEnabled = false
        val timeZone = presentation.timeZone
        lineChart.xAxis.setValueFormatter { value, _ ->
            val hr = value.hoursToDateTime(timeZone).hourOfDay
            when {
                hr == 0 -> "12 am"
                hr > 12 -> "${hr - 12}pm"
                else -> "${hr}am"
            }
        }
        lineChart.description = description
        lineChart.setTouchEnabled(false)
        lineChart.invalidate()
        detailsButton.setOnClickListener { selection() }
    }
}
