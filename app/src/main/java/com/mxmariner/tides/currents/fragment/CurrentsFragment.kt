package com.mxmariner.tides.currents.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mxmariner.tides.R

class CurrentsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.layout_currents_fragment, container, false)
    }
}