package com.tingsoft.tingdev.base.views

import android.content.Context
import android.content.res.TypedArray
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import com.tingsoft.tingdev.R
import com.tingsoft.tingdev.base.interfaces.PullToZoomBase
import com.tingsoft.tingdev.base.utils.Debug
import com.tingsoft.tingdev.base.utils.Debug.TYPE
import com.tingsoft.tingdev.base.utils.Debug.MODULE

class PullToZoomScrollView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
        PullToZoomBase<ScrollView>(context, attr)
{
    /*****************************************************************************
     * Constants & Global variables
     *****************************************************************************/
    private var mHeaderContainer: FrameLayout? = null
    private var mRootContainer: LinearLayout? = null
    private var mContentView: View? = null
    private var mHeaderHeight: Int = 0
    private var mIsCustomHeaderHeight = false
    private var mScalingRunnable = ScalingRunnable()

    init {
        (mRootView as InternalScrollView).onScrollViewChangedListener = object : OnScrollViewChangedListener {
            override fun onInternalScrollChanged(left: Int, top: Int, oldLeft: Int, oldTop: Int)
            {
                if (mIsZoomEnabled && mIsParallax)
                {
                    val f = (mHeaderHeight - mHeaderContainer!!.bottom).toFloat() + mRootView.scrollY
                    Debug.LOG(TYPE.D, MODULE.PULL_TO_ZOOM, "onScroll --> f = $f")

                    if (f > 0.0f && f < mHeaderHeight)
                    {
                        val i = (0.65 * f).toInt()
                        mHeaderContainer?.scrollTo(0, -i)
                    }
                    else if (mHeaderContainer?.scrollY != 0)
                        mHeaderContainer?.scrollTo(0, 0)
                }
            }
        }
    }
    /*****************************************************************************
     * Functions & Classes
     *****************************************************************************/
    private val mInterpolator = Interpolator { paramAnonymousFloat ->
        val f = paramAnonymousFloat - 1.0F
        1.0F + f * (f * (f * (f * f)))
    }

    inner class InternalScrollView constructor(context: Context, attr: AttributeSet? = null) : ScrollView(context, attr)
    {
        var onScrollViewChangedListener: OnScrollViewChangedListener? = null

        override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int)
        {
            super.onScrollChanged(l, t, oldl, oldt)
            onScrollViewChangedListener?.onInternalScrollChanged(l, t, oldl, oldt)
        }
    }

    inner class ScalingRunnable : Runnable
    {
        private var mDuration: Long = 0
        private var mScale: Float = Float.MIN_VALUE
        private var mStartTime: Long = 0

        var mIsFinished = true

        fun abortAnimation()
        {
            mIsFinished = true
        }

        override fun run()
        {
            if (!mIsFinished && mScale > 1.0)
            {
                val f1: Float = (SystemClock.currentThreadTimeMillis().toFloat() - mStartTime.toFloat()) / mDuration.toFloat()
                val f2: Float = mScale - (mScale - 1.0F) * mInterpolator.getInterpolation(f1)

                Debug.LOG(TYPE.D, MODULE.PULL_TO_ZOOM, "ScalingRunnable --> f2 = $f2")

                if (f2 > 1.0F)
                {
                    val localLayoutParams = mHeaderContainer!!.layoutParams
                    localLayoutParams.height = (f2 * mHeaderHeight).toInt()
                    mHeaderContainer!!.layoutParams = localLayoutParams

                    if (mIsCustomHeaderHeight)
                    {
                        val zoomLayoutParams = mZoomView.layoutParams
                        zoomLayoutParams.height = (f2 * mHeaderHeight).toInt()
                        mZoomView.layoutParams = zoomLayoutParams
                    }

                    post(this)

                    return
                }

                mIsFinished = true
            }
        }

        fun startAnimation(paramLong: Long)
        {
            mStartTime = SystemClock.currentThreadTimeMillis()
            mDuration = paramLong
            mScale = mHeaderContainer!!.bottom.toFloat() / mHeaderHeight
            mIsFinished = false

            post(this)
        }
    }

    private fun updateHeaderView()
    {
        mHeaderContainer?.removeAllViews()
        mHeaderContainer?.addView(mZoomView)
        mHeaderContainer?.addView(mHeaderView)
    }

    fun setScrollContentView(contentView: View)
    {
        if (mContentView != null)
            mRootContainer?.removeView(mContentView)

        mContentView = contentView

        mRootContainer?.addView(mContentView)
    }

    fun setHeaderViewSize(width: Int, height: Int)
    {
        var localObject: Any? = mHeaderContainer?.layoutParams

        if (localObject == null)
            localObject = AbsListView.LayoutParams(width, height)

        localObject as ViewGroup.LayoutParams
        localObject.width = width
        localObject.height = height
        mHeaderContainer!!.layoutParams = localObject
        mHeaderHeight = height
        mIsCustomHeaderHeight = true
    }

    fun setHeaderLayoutParams(layoutParams: LinearLayout.LayoutParams)
    {
        mHeaderContainer!!.layoutParams = layoutParams
        mHeaderHeight = layoutParams.height
        mIsCustomHeaderHeight = true
    }

    /*****************************************************************************
     * Interfaces & Overrides
     *****************************************************************************/
    interface OnScrollViewChangedListener
    {
        fun onInternalScrollChanged(left: Int, top: Int, oldLeft: Int, oldTop: Int)
    }

    /* --------------------- *
     *   Abstract functions  *
     * --------------------- */
    public override fun setHideHeader(_isHideHeader: Boolean)
    {
        if (_isHideHeader != mIsHideHeader)
        {
            mIsHideHeader = _isHideHeader

            mHeaderContainer?.visibility = if (mIsHideHeader) View.GONE else View.VISIBLE
        }
    }

    public override fun setHeaderView(headerView: View)
    {
        mHeaderView = headerView
        updateHeaderView()
    }

    public override fun setZoomView(zoomView: View)
    {
        mZoomView = zoomView
        updateHeaderView()
    }

    override fun pullHeaderToZoom(newScrollValue: Int)
    {
        Debug.LOG(TYPE.D, MODULE.PULL_TO_ZOOM, "pullHeaderToZoom --> newScrollValue = $newScrollValue")
        Debug.LOG(TYPE.D, MODULE.PULL_TO_ZOOM, "pullHeaderToZoom --> mHeaderHeight = $mHeaderHeight")

        if (!mScalingRunnable.mIsFinished)
            mScalingRunnable.abortAnimation()

        val localLayoutParams = mHeaderContainer!!.layoutParams
        localLayoutParams.height = Math.abs(newScrollValue) + mHeaderHeight
        mHeaderContainer!!.layoutParams = localLayoutParams

        if (mIsCustomHeaderHeight)
        {
            val zoomLayoutParams = mZoomView.layoutParams
            zoomLayoutParams.height = Math.abs(newScrollValue) + mHeaderHeight
            mZoomView.layoutParams = zoomLayoutParams
        }
    }

    override fun createRootView(context: Context, attrs: AttributeSet?): ScrollView = InternalScrollView(context, attrs)

    override fun smoothScrollToTop() = mScalingRunnable.startAnimation(200L)

    override fun isReadyForPullStart(): Boolean = mRootView.scrollY == 0

    override fun handleStyledAttributes(typedArray: TypedArray)
    {
        mRootContainer = LinearLayout(context)
        mRootContainer!!.orientation = LinearLayout.VERTICAL

        mHeaderContainer = FrameLayout(context)

        mHeaderContainer?.addView(mZoomView)
        mHeaderContainer?.addView(mHeaderView)

        val contentViewResId = typedArray.getResourceId(R.styleable.PullToZoomView_contentView, 0)

        if (contentViewResId > 0) {
            mContentView = LayoutInflater.from(context).inflate(contentViewResId, null, false)
            mRootContainer?.addView(mContentView)
        }

        mRootContainer?.addView(mHeaderContainer)

        mRootContainer?.clipChildren = false
        mHeaderContainer?.clipChildren = false

        mRootView.addView(mRootContainer)
    }

    override fun onLayout(paramBoolean: Boolean,
            paramInt1: Int,
            paramInt2: Int,
            paramInt3: Int,
            paramInt4: Int)
    {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4)

        if (mHeaderHeight == 0)
            mHeaderHeight = mHeaderContainer!!.height
    }
}