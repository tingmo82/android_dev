package com.tingsoft.tingdev.base.data

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

open class ClassLoaderSavedState : Parcelable
{
    /*****************************************************************************
     * Constants & Global variables
     *****************************************************************************/
    private var mSuperState: Parcelable? = EMPTY_STATE
    private var mClassLoader: ClassLoader? = null

    /**
     * Constructor used to make the EMPTY_STATE singleton
     */
    private constructor()
    {
        mSuperState = null
        mClassLoader = null
    }

    /**
     * Constructor called by derived classes when creating their ListSavedState objects
     *
     * @param superState The state of the superclass of this view
     */
    protected constructor(superState: Parcelable?, classLoader: ClassLoader?)
    {
        mClassLoader = classLoader

        if (superState == null)
            throw IllegalArgumentException("superState must not be null")
        else
            mSuperState = if (superState !== EMPTY_STATE) superState else null
    }

    /**
     * Constructor used when reading from a parcel. Reads the state of the superclass.
     *
     * @param source
     */
    protected constructor(source: Parcel)
    {
        // ETSY : we're using the passed super class loader unlike AbsSavedState
        val superState = source.readParcelable<Parcelable>(mClassLoader)
        mSuperState = superState ?: EMPTY_STATE
    }

    /*****************************************************************************
     * Functions & Classes
     *****************************************************************************/
    fun getSuperState(): Parcelable? = mSuperState

    companion object
    {
        val EMPTY_STATE: ClassLoaderSavedState = ClassLoaderSavedState()

        @JvmField final val CREATOR: Parcelable.Creator<ClassLoaderSavedState> = object : Parcelable.Creator<ClassLoaderSavedState>
        {
            @SuppressLint("ParcelClassLoader")
            override fun createFromParcel(inn: Parcel): ClassLoaderSavedState
            {
                val superState = inn.readParcelable<Parcelable>(null)

                if (superState != null)
                    throw IllegalStateException("superState must be null")

                return EMPTY_STATE
            }

            override fun newArray(size: Int): Array<ClassLoaderSavedState?> = arrayOfNulls(size)
        }
    }

    /*****************************************************************************
     * Interfaces & Overrides
     *****************************************************************************/
    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = dest.writeParcelable(mSuperState, flags)
}