package com.mxmariner.tides.tides.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mxmariner.tides.R
import com.mxmariner.tides.main.util.RxLocation
import com.mxmariner.tides.tides.viewmodel.TidesViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.layout_tides_fragment.*
import javax.inject.Inject

class TidesFragment : Fragment() {
    @Inject lateinit var rxLocation: RxLocation

    private val viewModel: TidesViewModel by lazy {
        ViewModelProviders.of(this).get(TidesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel.initialize()
        rxLocation.maybeRecentLocation()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onSuccess = {
                    Log.d("WBK", "Location $it")
                })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_tides_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = viewModel.recyclerAdapter
    }
}
