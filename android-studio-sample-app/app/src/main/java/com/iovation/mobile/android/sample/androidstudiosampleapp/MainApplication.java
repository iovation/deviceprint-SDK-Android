package com.iovation.mobile.android.sample.androidstudiosampleapp;

import android.app.Application;

import com.iovation.mobile.android.FraudForceConfiguration;
import com.iovation.mobile.android.FraudForceManager;

/**
 * Created by trevorchapman on 11/14/17.
 */

public class MainApplication extends Application {
  @Override
  public void onCreate() {
    FraudForceConfiguration fraudForceConfiguration = new FraudForceConfiguration.Builder()
        .enableNetworkCalls(true)
        .subscriberKey("REPLACE WITH SUBSCRIBER KEY")
        .build();

    FraudForceManager fraudForceManager = FraudForceManager.getInstance();
    fraudForceManager.initialize(fraudForceConfiguration, getApplicationContext());
    super.onCreate();
  }
}
