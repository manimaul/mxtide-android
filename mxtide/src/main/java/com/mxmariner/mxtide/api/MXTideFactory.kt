package com.mxmariner.mxtide.api

import com.mxmariner.mxtide.internal.TidesAndCurrents

object MXTideFactory {

    fun createTidesAndCurrents(): ITidesAndCurrents = TidesAndCurrents()

}
