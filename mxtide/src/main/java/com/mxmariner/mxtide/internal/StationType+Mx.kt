package com.mxmariner.mxtide.internal

import com.mxmariner.mxtide.api.StationType

internal val StationType.nativeStringValue: String
    get() {
        return if (this == StationType.TIDES) {
            "tide"
        } else {
            "current"
        }
    }
