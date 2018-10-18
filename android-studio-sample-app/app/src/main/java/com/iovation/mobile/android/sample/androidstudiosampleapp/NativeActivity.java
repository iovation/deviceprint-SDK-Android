package com.iovation.mobile.android.sample.androidstudiosampleapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iovation.mobile.android.FraudForceConfiguration;
import com.iovation.mobile.android.FraudForceManager;

public class NativeActivity extends Activity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FraudForceManager.getInstance().refresh(getApplicationContext());

        setContentView(R.layout.activity_main);
    }

    public void printDevice(View target) {
        TextView bbResult = (TextView) findViewById(R.id.bbResult);
        bbResult.setText("");
        bbResult.setVisibility(View.INVISIBLE);
        TextView bbResultLabel = (TextView) findViewById(R.id.bbResultLabel);
        bbResultLabel.setText(R.string.printingWaitMsg);
        bbResultLabel.setVisibility(View.VISIBLE);
        new PrintThread().execute();
    }

    private class PrintThread extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return FraudForceManager.getInstance().getBlackbox(getApplicationContext());
        }

        @Override
        protected void onPostExecute(String bb) {
            TextView bbResultLabel = (TextView) findViewById(R.id.bbResultLabel);
            bbResultLabel.setText(R.string.bbResultLabel);
            bbResultLabel.setVisibility(View.VISIBLE);

            TextView bbResult = (TextView) findViewById(R.id.bbResult);
            bbResult.setText(bb);
            bbResult.setVisibility(View.VISIBLE);
        }
    }
}
