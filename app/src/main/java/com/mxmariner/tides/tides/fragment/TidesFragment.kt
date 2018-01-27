package com.mxmariner.tides.tides.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mxmariner.tides.R
import com.mxmariner.tides.tides.model.TidesViewStateLoadingComplete
import com.mxmariner.tides.tides.model.TidesViewStateLoadingStarted
import com.mxmariner.tides.tides.viewmodel.TidesViewModel
import com.mxmariner.tides.tides.viewmodel.TidesViewModelFactory
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.layout_tides_fragment.*
import javax.inject.Inject

class TidesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: TidesViewModelFactory
    private val viewModel: TidesViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(TidesViewModel::class.java)
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_tides_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = viewModel.recyclerAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        compositeDisposable.add(viewModel.viewState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    when (it) {
                        is TidesViewStateLoadingStarted -> loadingStarted(it.message)
                        is TidesViewStateLoadingComplete -> loadingComplete(it.errorMessage)
                    }
                })
    }

    private fun loadingStarted(message: String?) {
        recyclerView.visibility = View.GONE
        loadingProgress.visibility = View.VISIBLE
        messageTextView.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    private fun loadingComplete(errorMessage: String?) {
        loadingProgress.visibility = View.GONE

        errorMessage?.let {
            messageTextView.text = it
            recyclerView.visibility = View.GONE
        } ?: {
            messageTextView.text = null
            recyclerView.visibility = View.VISIBLE
        }()
    }
}
