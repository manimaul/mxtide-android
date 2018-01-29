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
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.IStationPrediction
import com.mxmariner.tides.R
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

    fun apply(station: IStation, prediction: List<IStationPrediction<Float>>) {
        stationName.text = station.name
        position.text = "${station.latitude}, ${station.longitude}"
        station.timeZone.toTimeZone()?.displayName?.let {
            stationTimeZoneTitle.visibility = View.VISIBLE
            stationTimeZone.visibility = View.VISIBLE
            stationTimeZone.text = it
        } ?: {
            stationTimeZoneTitle.visibility = View.GONE
            stationTimeZone.visibility = View.GONE
        }()

        val entries = prediction.map {
            //todo: convert to seconds for spanning midnight
            val hours = it.date.hourOfDay.toFloat() + it.date.minuteOfHour.toFloat() / 60.0f
            Entry(hours, it.value)
        }
        val lineDataSet = LineDataSet(entries, "Feet")
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
        lineChart.xAxis.setValueFormatter { value, _ ->
            val hr = value.toInt()
            if (hr > 12) {
                "${hr - 12}pm"
            } else {
                "${value.toInt()}am"
            }
        }

        lineChart.description = description
        lineChart.setTouchEnabled(false)
        lineChart.invalidate()
    }
}
