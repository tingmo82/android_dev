package com.tingsoft.tingdev.base.data

import android.os.Parcel
import android.os.Parcelable
import android.widget.AbsListView

data class ListSavedState(val parcle: Parcelable?)  : ClassLoaderSavedState(parcle, AbsListView::class.java.classLoader)
{
    /*****************************************************************************
     * Constants & Global variables
     *****************************************************************************/
    var selectedId: Long = 0
    var firstId: Long = 0
    var viewTop: Int = 0
    var position: Int = 0
    var height: Int = 0

    /**
     * Constructor called from {@link #CREATOR}
     */
    constructor(source: Parcel) : this (source.readParcelable<Parcelable>(AbsListView::class.java.classLoader))
    {
        selectedId = source.readLong()
        firstId = source.readLong()
        viewTop = source.readInt()
        position = source.readInt()
        height = source.readInt()
    }

    /*****************************************************************************
     * Functions & Classes
     *****************************************************************************/
    companion object CREATOR : Parcelable.Creator<ListSavedState>
    {
        override fun createFromParcel(parcel: Parcel): ListSavedState = ListSavedState(parcel)

        override fun newArray(size: Int): Array<ListSavedState?> = arrayOfNulls(size)
    }

    /*****************************************************************************
     * Interfaces & Overrides
     *****************************************************************************/
    override fun writeToParcel(dest: Parcel, flags: Int)
    {
        super.writeToParcel(dest, flags)

        dest.writeLong(selectedId)
        dest.writeLong(firstId)
        dest.writeInt(viewTop)
        dest.writeInt(position)
        dest.writeInt(height)
    }

    override fun toString(): String
    {
        return ("ExtendableListView.ListSavedState{"
                + Integer.toHexString(System.identityHashCode(this))
                + " selectedId=" + selectedId
                + " firstId=" + firstId
                + " viewTop=" + viewTop
                + " position=" + position
                + " height=" + height + "}")
    }
}