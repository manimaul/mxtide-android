package com.mxmariner.tides.extensions

import android.os.Bundle
import androidx.fragment.app.Fragment

val Fragment.args : Bundle
get() {
    return arguments ?: {
        arguments = Bundle()
        arguments!!
    }()
}
