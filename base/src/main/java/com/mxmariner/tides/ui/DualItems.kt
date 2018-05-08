package com.mxmariner.tides.ui

import android.content.Context
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.mxmariner.tides.R
import kotlinx.android.synthetic.main.dual_items.view.*


class DualItems : FrameLayout {
  constructor(context: Context) : super(context){
    initAttrs(null)
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    initAttrs(attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    initAttrs(attrs)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    initAttrs(attrs)
  }

  private fun initAttrs(attrs: AttributeSet?) {
    LayoutInflater.from(context).inflate(R.layout.dual_items, this, true)
    attrs?.let {
      val a = context.theme.obtainStyledAttributes(
          it,
          R.styleable.DualItems,
          0, 0)

      try {
        a.getString(R.styleable.DualItems_leftTitle)?.let { leftTitle = it }
        a.getString(R.styleable.DualItems_leftDesc)?.let { leftDesc = it }
        a.getString(R.styleable.DualItems_rightTitle)?.let { rightTitle = it }
        a.getString(R.styleable.DualItems_rightDesc)?.let { rightDesc = it }
      } finally {
        a.recycle()
      }
    }
  }

  // region left

  var leftTitle: CharSequence
    get() = col1title.text
    set(value) {
      col1title.text = value
    }

  var leftDesc: CharSequence
    get() = col1desc.text
    set(value) {
      col1desc.text = value
    }

  fun setLeftDesc(@StringRes resId: Int) {
    col1desc.setText(resId)
  }

  // region right

  var rightTitle: CharSequence
    get() = col2title.text
    set(value) {
      col2title.text = value
    }

  var rightDesc: CharSequence
    get() = col2desc.text
    set(value) {
      col2desc.text = value
    }

  fun setRightDesc(@StringRes resId: Int) {
    col2desc.setText(resId)
  }
}
