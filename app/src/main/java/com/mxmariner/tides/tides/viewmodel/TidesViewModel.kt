package com.mxmariner.tides.tides.viewmodel

import android.arch.lifecycle.ViewModel
import com.mxmariner.tides.main.repository.HarmonicsRepo

class TidesViewModel(val recyclerAdapter: TidesRecyclerAdapter = TidesRecyclerAdapter()) : ViewModel() {

    fun initialize() {
        HarmonicsRepo.tidesAndCurrents
    }

}