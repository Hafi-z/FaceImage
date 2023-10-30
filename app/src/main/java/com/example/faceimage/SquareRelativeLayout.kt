package com.example.faceimage

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class SquareRelativeLayout(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
// Set a square layout by using the width as the height
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}