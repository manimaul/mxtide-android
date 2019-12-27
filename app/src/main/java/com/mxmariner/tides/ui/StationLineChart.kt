package com.mxmariner.tides.ui

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mxmariner.tides.extensions.hoursToDateTime
import com.mxmariner.tides.extensions.unixTimeHours
import com.mxmariner.tides.model.StationPresentation

class StationLineChart : LineChart {
  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  fun applyPresentation(presentation: StationPresentation) {
    val entries = presentation.prediction.map {
      val hours = it.date.unixTimeHours
      Entry(hours, it.value)
    }
    val lineDataSet = LineDataSet(entries, "")
    lineDataSet.setDrawValues(false)
    lineDataSet.setColors(presentation.color)
    lineDataSet.setDrawCircles(true)

    lineDataSet.circleColors = entries.map {
      if (it.x == presentation.now.unixTimeHours) presentation.nowColor else presentation.color
    }
    lineDataSet.setDrawFilled(true)
    lineDataSet.fillColor = presentation.color
    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    val lineData = LineData(lineDataSet)
    data = lineData
    xAxis.granularity = 2.0f
    legend.isEnabled = false
    val timeZone = presentation.timeZone
    xAxis.valueFormatter = object : ValueFormatter() {
      override fun getFormattedValue(value: Float): String {
        val hr = value.hoursToDateTime(timeZone).hourOfDay
        return when {
          hr == 0 -> "12 am"
          hr > 12 -> "${hr - 12}pm"
          else -> "${hr}am"
        }
      }
    }
    val abriviation = context.getString(presentation.yValAbrv)
    axisRight.valueFormatter = object : ValueFormatter() {
      override fun getFormattedValue(value: Float): String {
        return abriviation
      }
    }
    description.text = ""
    setTouchEnabled(false)
    invalidate()
  }

}