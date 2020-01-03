package com.mxmariner.tides.ui

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mxmariner.mxtide.api.IStationPrediction
import com.mxmariner.tides.extensions.differenceHoursMinutes
import com.mxmariner.tides.extensions.formatTime
import com.mxmariner.tides.extensions.withinOneMinute
import com.mxmariner.tides.model.StationPresentation

class StationLineChart : LineChart {
  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  fun applyPresentation(presentation: StationPresentation) {
    val start = presentation.prediction.first().date
    val entryMap = presentation.prediction.map {
      val hours = it.date.differenceHoursMinutes(start)
      hours to it
    }.toMap()
    val entries = entryMap.map {
      Entry(it.key, it.value.value, it.value)
    }
    val lineDataSet = LineDataSet(entries, "")
    lineDataSet.setDrawValues(false)
    lineDataSet.setColors(presentation.color)
    lineDataSet.setDrawCircles(true)

    lineDataSet.circleColors = entries.map {
      val prediction = it.data as? IStationPrediction<*>
      if (presentation.now.withinOneMinute(prediction?.date)) presentation.nowColor else presentation.color
    }
    lineDataSet.setDrawFilled(true)
    lineDataSet.fillColor = presentation.color
    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    val lineData = LineData(lineDataSet)
    data = lineData
    xAxis.granularity = 2.0f
    legend.isEnabled = false
    xAxis.valueFormatter = object : ValueFormatter() {
      override fun getFormattedValue(value: Float): String {
        return entryMap[value]?.date?.formatTime() ?: ""
      }
    }
    val abbreviation = context.getString(presentation.yValAbrv)
    axisRight.valueFormatter = object : ValueFormatter() {
      override fun getFormattedValue(value: Float): String {
        return abbreviation
      }
    }
    description.text = ""
    setTouchEnabled(false)
    invalidate()
  }

}