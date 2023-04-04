package com.iovation.mobile.android.sample.kotlinApp

import android.app.Application
import com.iovation.mobile.android.FraudForceConfiguration
import com.iovation.mobile.android.FraudForceManager

/**
 * Created by Jacob Schmit on 03/27/23.
 */

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val configuration = FraudForceConfiguration.Builder()
            .subscriberKey("M1WrRSwcjUBQmHamij3DxQJWr00YzfRhXaMkI+zhhiY=")
            .enableNetworkCalls(true)
            .build()

        FraudForceManager.initialize(configuration, applicationContext)
    }
}