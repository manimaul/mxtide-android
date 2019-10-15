package com.mxmariner.tides.extensions

import android.os.Bundle
import android.support.v4.app.Fragment

val Fragment.args : Bundle
get() {
    return arguments ?: {
        arguments = Bundle()
        arguments!!
    }()
}
