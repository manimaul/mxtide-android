package com.mxmariner.tides.tides.model

sealed class TidesViewState
data class TidesViewStateLoadingStarted(val message: String? = null) : TidesViewState()
data class TidesViewStateLoadingComplete(val errorMessage: String? = null) : TidesViewState()
