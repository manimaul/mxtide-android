@file:JvmName("MXTideFactory")

package com.mxmariner.mxtide.api

import com.mxmariner.mxtide.internal.TidesAndCurrents

fun createTidesAndCurrents(): ITidesAndCurrents = TidesAndCurrents()
