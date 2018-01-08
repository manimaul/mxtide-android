package com.mxmariner.tides.tides.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mxmariner.tides.R
import com.mxmariner.tides.tides.viewmodel.TidesViewModel
import kotlinx.android.synthetic.main.layout_tides_fragment.*

class TidesFragment : Fragment() {

    //todo: Inject
    private val viewModel: TidesViewModel by lazy {
        ViewModelProviders.of(this).get(TidesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initialize()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_tides_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = viewModel.recyclerAdapter
    }
}