package com.mxmariner.tides.tides.model

sealed class TidesViewState
class TidesViewStateLoadingStarted : TidesViewState()
class TidesViewStateLoadingComplete(val errorMessage: String? = null) : TidesViewState()
