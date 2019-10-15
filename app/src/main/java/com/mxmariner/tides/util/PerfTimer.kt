package com.mxmariner.tides.util

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Measuring tool for instrumenting app performance.
 * Example:

   D/PerfTimer: (All Events) :: duration=150 ms :: cumulative=150 ms
   |----------------------------------------------------------------------------------------------------|
   D/PerfTimer: (MxTidesApplication.onCreate()) :: duration=0 ms :: cumulative=0 ms
   ||
   D/PerfTimer: (HarmonicsRepo.initialize()) :: duration=40 ms :: cumulative=41 ms
     |---------------------------|
   D/PerfTimer: (MainActivity.onCreate()) :: duration=95 ms :: cumulative=149 ms
                                        |---------------------------------------------------------------|

 */
object PerfTimer {

    private const val TAG = "PerfTimer"
    private val RANGE_METER_LENGTH = 100.0
    private val ranges = LinkedHashMap<String, Range>()
    private val eventsTotalRange = Range("All Events")


    private fun printInternal() {
        synchronized(this) {
            if (!ranges.isEmpty()) {
                android.util.Log.d(TAG, rangeMeter(eventsTotalRange))
                for (range in ranges.values) {
                    android.util.Log.d(TAG, rangeMeter(range))
                }
            }
        }
    }

    private fun rangeMeter(range: Range): String {
        val duration = range.duration()
        val totalDuration = eventsTotalRange.duration()
        val lead = TimeUnit.NANOSECONDS.toMillis(range.startNanos - eventsTotalRange.startNanos)
        if (duration < 0) {
            return "(${range.mEventTag}) :: unknown duration - event was not stopped! \n\n"
        }
        val message = "(${range.mEventTag}) :: duration=$duration ms :: cumulative=${lead + duration} ms"
        if (totalDuration > 0) {
            val factor = RANGE_METER_LENGTH / totalDuration
            val blank = Math.round(lead * factor).toInt()
            val count = Math.round(duration * factor).toInt()
            val sb = StringBuilder(message.length + 3 + blank + count)
                    .append(message)
                    .append("\n")
            for (i in 0 until blank) {
                sb.append(' ')
            }
            sb.append('|')
            for (i in 0 until count) {
                sb.append('-')
            }
            sb.append("|\n \n ")
            return sb.toString()
        } else {
            return "$message\n||\n \n "
        }
    }

    /**
     * Clears all recorded events.
     */
    fun resetAndClear() {
        synchronized(this) {
            eventsTotalRange.startNanos = 0
            ranges.clear()
        }
    }

    /**
     * Records the start time of an event.
     *
     * @param tag a tag to identify the event.
     */
    fun markEventStart(tag: String) {
        val t = System.nanoTime()
        synchronized(this) {
            val range = ranges[tag] ?: {
                val r = Range(tag)
                ranges.put(tag, r)
                r
            }()
            range.startNanos = t
            if (eventsTotalRange.startNanos == 0L) {
                eventsTotalRange.startNanos = t
            }
        }
    }

    /**
     * Records the stop time of an event.
     *
     * @param tag a tag to identify the event.
     */
    fun markEventStop(tag: String) {
        val t = System.nanoTime()
        synchronized(this) {
            ranges[tag]?.let {
                it.stopNanos = t
                eventsTotalRange.stopNanos = t
            }
        }
    }

    /**
     * Prints a log of all captured events on a floating scale relative to all captured events.
     *
     * @param clear true to clear all recorded events.
     */
    fun printLogOfCapturedEvents(clear: Boolean) {
        printInternal()
        if (clear) {
            resetAndClear()
        }
    }
}

private class Range(val mEventTag: String) {
    internal var startNanos: Long = 0
    internal var stopNanos: Long = 0

    internal fun duration(): Long {
        return TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos)
    }
}
