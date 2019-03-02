package com.tingsoft.tingdev.base.utils

import android.util.Log
import com.tingsoft.tingdev.BuildConfig

object Debug
{
    enum class TYPE {
        V,
        D,
        I,
        W,
        E
    }

    enum class MODULE(val enable: Boolean) {
        MAIN_SKELETON(true),
        PULL_TO_ZOOM(true),
        UI(true)
    }

    fun LOG(type: TYPE, module: MODULE, msg: String) = LOG(type, module, msg, null)

    fun LOG(type: TYPE, module: MODULE, msg: String, tr: Throwable?)
    {
        if (!BuildConfig.LOG || !module.enable)
            return

        when (type)
        {
            TYPE.V -> Log.v(module.name, msg, tr)
            TYPE.D -> Log.d(module.name, msg, tr)
            TYPE.E -> Log.e(module.name, msg, tr)
            TYPE.I -> Log.i(module.name, msg, tr)
            TYPE.W -> Log.w(module.name, msg, tr)
        }
    }
}