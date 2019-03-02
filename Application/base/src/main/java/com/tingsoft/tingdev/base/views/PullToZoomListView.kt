package com.tingsoft.tingdev.base.views

import android.content.Context
import android.content.res.TypedArray
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.*
import com.tingsoft.tingdev.base.interfaces.PullToZoomBase
import com.tingsoft.tingdev.base.utils.Debug
import com.tingsoft.tingdev.base.utils.Debug.TYPE
import com.tingsoft.tingdev.base.utils.Debug.MODULE

class PullToZoomListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
        PullToZoomBase<ListView>(context, attrs), AbsListView.OnScrollListener
{
    /*****************************************************************************
     * Constants & Global variables
     *****************************************************************************/
    private var mHeaderContainer: FrameLayout? = null
    private var mHeaderHeight: Int = 0
    private var mScalingRunnable = ScalingRunnable()

    init {
        mRootView.setOnScrollListener(this)
    }
    /*****************************************************************************
     * Functions & Classes
     *****************************************************************************/

    private val mInterpolator = Interpolator { paramAnonymousFloat ->
        val f = paramAnonymousFloat - 1.0F
        1.0F + f * (f * (f * (f * f)))
    }

    private fun isFirstItemVisible(): Boolean
    {
        if (mRootView.adapter.isEmpty)
            return true
        else
        {
            /**
             * This check should really just be:
             * mRootView.getFirstVisiblePosition() == 0, but PtRListView
             * internally use a HeaderView which messes the positions up. For
             * now we'll just add one to account for it and rely on the inner
             * condition which checks getTop().
             */
            if (mRootView.firstVisiblePosition <= 1)
            {
                val firstVisibleChild = mRootView.getChildAt(0)

                if (firstVisibleChild != null)
                    return firstVisibleChild.top >= mRootView.top
            }
        }

        return false
    }

    private fun removeHeaderView() = mRootView.removeHeaderView(mHeaderContainer!!)

    private fun updateHeaderView()
    {
        mRootView.removeHeaderView(mHeaderContainer!!)
        mHeaderContainer!!.removeAllViews()
        mHeaderContainer!!.addView(mZoomView)
        mHeaderContainer!!.addView(mHeaderView)
        mHeaderHeight = mHeaderContainer!!.height
        mRootView.addHeaderView(mHeaderContainer)
    }

    fun setAdapter(adapter: ListAdapter)
    {
        mRootView.adapter = adapter
    }

    fun setOnItemClickListener(listener: AdapterView.OnItemClickListener)
    {
        mRootView.onItemClickListener = listener
    }

    fun setHeaderViewSize(width: Int, height: Int)
    {
        var localObject: Any? = mHeaderContainer?.layoutParams

        if (localObject == null)
            localObject = AbsListView.LayoutParams(width, height)

        localObject as ViewGroup.LayoutParams
        localObject.width = width
        localObject.height = height
        mHeaderContainer?.layoutParams = localObject
        mHeaderHeight = height

    }

    fun setHeaderLayoutParams(layoutParams: AbsListView.LayoutParams)
    {
        mHeaderContainer?.layoutParams = layoutParams
        mHeaderHeight = layoutParams.height
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

    /*****************************************************************************
     * Interfaces & Overrides
     *****************************************************************************/
    /* --------------------- *
     *   Abstract functions  *
     * --------------------- */
    override fun setHideHeader(_isHideHeader: Boolean)
    {
        if (_isHideHeader != mIsHideHeader)
        {
            mIsHideHeader = _isHideHeader

            if (mIsHideHeader) removeHeaderView() else updateHeaderView()
        }
    }

    override fun setHeaderView(headerView: View)
    {
        mHeaderView = headerView
        updateHeaderView()
    }

    override fun setZoomView(zoomView: View)
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
    }

    override fun createRootView(context: Context, attrs: AttributeSet?): ListView = ListView(context, attrs)

    override fun smoothScrollToTop() = mScalingRunnable.startAnimation(200L)

    override fun isReadyForPullStart(): Boolean = isFirstItemVisible()

    override fun handleStyledAttributes(typedArray: TypedArray)
    {
        mHeaderContainer = FrameLayout(context)

        mHeaderContainer?.addView(mZoomView)
        mHeaderContainer?.addView(mHeaderView)

        mRootView.addHeaderView(mHeaderContainer)
    }

    override fun onLayout(paramBoolean: Boolean,
            paramInt1: Int,
            paramInt2: Int,
            paramInt3: Int,
            paramInt4: Int)
    {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4)

        if (mHeaderHeight == 0 && mHeaderContainer != null)
            mHeaderHeight = mHeaderContainer!!.height
    }

    /* -------------------------------- *
     *   AbsListView.OnScrollListener   *
     * -------------------------------- */
    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int)
    {
        if (!mIsHideHeader && mIsZoomEnabled)
        {
            val f = (mHeaderHeight - mHeaderContainer!!.bottom).toFloat()
            Debug.LOG(TYPE.D, MODULE.PULL_TO_ZOOM, "onScroll --> f = $f")

            if (mIsParallax)
            {
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

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) { }
}