package com.tingsoft.tingdev.featureUi

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.LinearLayout
import com.tingsoft.tingdev.base.utils.Debug
import com.tingsoft.tingdev.base.utils.Debug.MODULE
import com.tingsoft.tingdev.base.utils.Debug.TYPE
import com.tingsoft.tingdev.base.views.PullToZoomScrollView

class UIActivity : AppCompatActivity()
{
    /*****************************************************************************
     * Constants & Global variables
     *****************************************************************************/

    /*****************************************************************************
     * Life cycles
     *****************************************************************************/
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Debug.LOG(TYPE.D, MODULE.UI, "onCreate")

        loadView()
    }

    /*****************************************************************************
     * Functions & Classes
     *****************************************************************************/
    private fun loadView()
    {
        setContentView(R.layout.activity_ui)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val listView :PullToZoomScrollView = findViewById(R.id.scrollView)
        val zoomView = LayoutInflater.from(this).inflate(R.layout.part_list_head_zoom, null, false)
        val contentView = LayoutInflater.from(this).inflate(R.layout.content_main_menu, null, false)

        listView.setZoomView(zoomView)
        listView.setScrollContentView(contentView)

        val localDM :DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(localDM)

        listView.setHeaderLayoutParams(LinearLayout.LayoutParams(localDM.widthPixels, (9.0F * (localDM.widthPixels / 16.0F)).toInt()))
    }

    /*****************************************************************************
     * Interfaces & Overrides
     *****************************************************************************/
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when (item!!.itemId)
        {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
