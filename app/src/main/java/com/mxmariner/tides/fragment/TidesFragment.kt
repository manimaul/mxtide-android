package com.mxmariner.tides.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.mxmariner.di.Injector
import com.mxmariner.main.model.TidesViewStateLoadingComplete
import com.mxmariner.main.model.TidesViewStateLoadingStarted
import com.mxmariner.main.viewmodel.TidesViewModel
import com.mxmariner.main.viewmodel.TidesViewModelFactory
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.R
import com.mxmariner.tides.extensions.args
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.layout_tides_fragment.*
import javax.inject.Inject

class TidesFragment : Fragment() {

    companion object {
        fun create(stationType: StationType): TidesFragment {
            val fragment = TidesFragment()
            fragment.args.putSerializable("type", stationType)
            return fragment
        }
    }

    private val stationType by lazy { args.getSerializable("type") as StationType }

    @Inject lateinit var viewModelFactory: TidesViewModelFactory
    @Inject lateinit var viewModel: TidesViewModel


    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().let {
          Injector.activityInjector(it).inject(this)
          viewModel = ViewModelProvider(this, viewModelFactory).get(TidesViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_tides_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = viewModel.recyclerAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        compositeDisposable.add(viewModel.viewState(stationType)
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
