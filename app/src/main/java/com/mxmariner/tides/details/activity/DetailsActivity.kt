package com.mxmariner.tides.details.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.mxmariner.tides.R
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class DetailsActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject lateinit var dispatchingInjector: DispatchingAndroidInjector<Fragment>

    //region HasSupportFragmentInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingInjector
    }

    //endregion

    //region AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
    }

    //endregion

}
