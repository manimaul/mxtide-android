package com.mxmariner.tides.main.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mxmariner.tides.main.R

class CurrentsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
//        Injector.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.layout_currents_fragment, container, false)
    }
}