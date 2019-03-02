package com.tingsoft.tingdev.base.interfaces

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import com.tingsoft.tingdev.R

abstract class PullToZoomBase<T : View> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
        LinearLayout(context, attrs)
{
    /*****************************************************************************
     * Constants & Global variables
     *****************************************************************************/
    private val _fFRICTION = 2.0f

    protected val mRootView: T = this.createRootView(context, attrs)

    private var mTouchSlop: Int = 0
    private var mIsBeingDragged = false
    private var mLastMotionY: Float = 0.toFloat()
    private var mLastMotionX: Float = 0.toFloat()
    private var mInitialMotionY: Float = 0.toFloat()
    private var mInitialMotionX: Float = 0.toFloat()

    protected var onPullZoomListener: OnPullZoomListener? = null
    protected var mHeaderView: View = View(context)
    protected var mZoomView: View = View(context)
    protected var mScreenHeight: Int = 0
    protected var mScreenWidth: Int = 0

    var mIsZoomEnabled = true
    var mIsParallax = true
    var mIsZooming = false
    var mIsHideHeader = false

    init {
        init(context, attrs)
    }

    /*****************************************************************************
     * Functions & Classes
     *****************************************************************************/

    private fun init(context: Context, attrs: AttributeSet?)
    {
        gravity = Gravity.CENTER

        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

        val localDisplayMetrics = DisplayMetrics()
        (getContext() as Activity).windowManager.defaultDisplay.getMetrics(localDisplayMetrics)
        mScreenHeight = localDisplayMetrics.heightPixels
        mScreenWidth = localDisplayMetrics.widthPixels

        if (attrs != null)
        {
            val a = getContext().obtainStyledAttributes(attrs, R.styleable.PullToZoomView)

            val zoomViewResId = a.getResourceId(R.styleable.PullToZoomView_zoomView, 0)

            if (zoomViewResId > 0)
                mZoomView = LayoutInflater.from(getContext()).inflate(zoomViewResId, null, false)

            val headerViewResId = a.getResourceId(R.styleable.PullToZoomView_headerView, 0)

            if (headerViewResId > 0)
                mHeaderView = LayoutInflater.from(getContext()).inflate(headerViewResId, null, false)

            mIsParallax = a.getBoolean(R.styleable.PullToZoomView_isHeaderParallax, true)

            // Let the derivative classes have a go at handling attributes, then recycle them...
            handleStyledAttributes(a)
            a.recycle()
        }

        addView(mRootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun pullEvent()
    {
        val newScrollValue: Int = Math.round(Math.min(mInitialMotionY - mLastMotionY, 0f) / _fFRICTION)

        pullHeaderToZoom(newScrollValue)
        onPullZoomListener?.onPullZooming(newScrollValue)
    }

    /*****************************************************************************
     * Interfaces & Overrides
     *****************************************************************************/
    interface OnPullZoomListener
    {
        fun onPullZooming(newScrollValue: Int)

        fun onPullZoomEnd()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean
    {
        if (!mIsZoomEnabled || mIsHideHeader)
            return false

        val action = event.action

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP)
        {
            mIsBeingDragged = false
            return false
        }

        if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged)
            return true

        when (action)
        {
            MotionEvent.ACTION_MOVE -> {
                if (isReadyForPullStart())
                {
                    // We need to use the correct values, based on scroll direction
                    val diff: Float = event.y - mLastMotionY
                    val oppositeDiff: Float = event.x - mLastMotionX
                    val absDiff: Float = Math.abs(diff)

                    if (absDiff > mTouchSlop && absDiff > Math.abs(oppositeDiff))
                    {
                        if (diff >= 1f && isReadyForPullStart())
                        {
                            mLastMotionY = event.y
                            mLastMotionX = event.x
                            mIsBeingDragged = true
                        }
                    }
                }
            }

            MotionEvent.ACTION_DOWN -> {
                if (isReadyForPullStart())
                {
                    mInitialMotionY = event.y
                    mLastMotionY = mInitialMotionY
                    mInitialMotionX = event.x
                    mLastMotionX = mInitialMotionX
                    mIsBeingDragged = false
                }
            }
        }

        return mIsBeingDragged
    }

    override fun onTouchEvent(event: MotionEvent): Boolean
    {
        if (!mIsZoomEnabled || mIsHideHeader)
            return false

        if (event.action == MotionEvent.ACTION_DOWN && event.edgeFlags != 0)
            return false

        when (event.action)
        {
            MotionEvent.ACTION_MOVE -> {
                if (mIsBeingDragged)
                {
                    mLastMotionY = event.y
                    mLastMotionX = event.x
                    mIsZooming = true

                    pullEvent()

                    return true
                }
            }

            MotionEvent.ACTION_DOWN -> {
                if (isReadyForPullStart())
                {
                    mInitialMotionY = event.y
                    mLastMotionY = mInitialMotionY
                    mInitialMotionX = event.x
                    mLastMotionX = mInitialMotionX

                    return true
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (mIsBeingDragged)
                {
                    mIsBeingDragged = false

                    // If we're already refreshing, just scroll back to the top
                    if (mIsZooming)
                    {
                        smoothScrollToTop()
                        onPullZoomListener?.onPullZoomEnd()

                        mIsZooming = false

                        return true
                    }
                    return true
                }
            }
        }
        return false
    }

    internal abstract fun setHideHeader(_isHideHeader: Boolean)

    internal abstract fun setHeaderView(headerView: View)

    internal abstract fun setZoomView(zoomView: View)

    protected abstract fun pullHeaderToZoom(newScrollValue: Int)

    protected abstract fun createRootView(context: Context, attrs: AttributeSet?): T

    protected abstract fun smoothScrollToTop()

    protected abstract fun isReadyForPullStart(): Boolean

    protected abstract fun handleStyledAttributes(typedArray: TypedArray)
}