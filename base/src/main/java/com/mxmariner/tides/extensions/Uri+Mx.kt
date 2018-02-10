package com.mxmariner.tides.extensions

import android.net.Uri

fun Uri.Builder.addParams(params: Map<Any, Any>?) : Uri.Builder {
    params?.forEach {
        this.appendQueryParameter("${it.key}", "${it.value}")
    }
    return this
}