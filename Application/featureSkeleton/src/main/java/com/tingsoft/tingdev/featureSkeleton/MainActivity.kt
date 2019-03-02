package com.tingsoft.tingdev.featureSkeleton

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tingsoft.tingdev.base.utils.Debug.LOG
import com.tingsoft.tingdev.base.utils.Debug.TYPE
import com.tingsoft.tingdev.base.utils.Debug.MODULE
import com.tingsoft.tingdev.featureUi.UIActivity

class MainActivity : AppCompatActivity()
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
        setContentView(R.layout.activity_main)

        // temporary : until developing the architecture of skeleton
        startActivity(Intent(applicationContext, UIActivity::class.java))

        // Example of a call to a native method
        LOG(TYPE.D, MODULE.MAIN_SKELETON, stringFromJNI())
    }

    /*****************************************************************************
     * Functions & Classes
     *****************************************************************************/

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object
    {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    /*****************************************************************************
     * Interfaces & Overrides
     *****************************************************************************/
}
