@file:JvmName("Factory")

package com.mxmariner.mxtide.api

import com.mxmariner.mxtide.internal.TidesAndCurrents

fun createTidesAndCurrents(): ITidesAndCurrents = TidesAndCurrents()
