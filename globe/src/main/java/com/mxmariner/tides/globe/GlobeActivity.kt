package com.mxmariner.tides.globe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GlobeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.globe_layout)
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, GlobeFragment())
                .commitNow()
    }
}