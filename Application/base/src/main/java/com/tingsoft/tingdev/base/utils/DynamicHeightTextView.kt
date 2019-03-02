package com.tingsoft.tingdev.base.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

class DynamicHeightTextView : TextView
{
    /*****************************************************************************
     * Constants & Global variables
     *****************************************************************************/
    var mHeightRatio:Double = 0.0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrSet: AttributeSet?) : super(context, attrSet)

    fun setHeightRatio(ratio: Double)
    {
        if (ratio != mHeightRatio)
        {
            mHeightRatio = ratio
            requestLayout()
        }
    }

    /*****************************************************************************
     * Functions & Classes
     *****************************************************************************/
    fun getHeightRatio(): Double = mHeightRatio

    /*****************************************************************************
     * Interfaces & Overrides
     *****************************************************************************/
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        if (mHeightRatio > 0.0)
        {
            // set the image views size
            val width = MeasureSpec.getSize(widthMeasureSpec)
            setMeasuredDimension(width, (width * mHeightRatio).toInt())
        }
        else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}