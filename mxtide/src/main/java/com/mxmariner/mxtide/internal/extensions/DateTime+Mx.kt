package com.mxmariner.mxtide.internal.extensions

import org.joda.time.DateTime

internal val DateTime.unixTimeSeconds: Long
    get() = this.millis / 1000

