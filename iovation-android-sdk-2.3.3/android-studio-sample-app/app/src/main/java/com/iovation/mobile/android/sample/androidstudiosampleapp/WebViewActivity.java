package com.iovation.mobile.android.sample.androidstudiosampleapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import static com.iovation.mobile.android.DevicePrint.getBlackbox;
import static com.iovation.mobile.android.DevicePrint.start;

/**
 * Created by trevorchapman on 4/13/15.
 */
public class WebViewActivity extends Activity {
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
        start(getApplicationContext());
        setContentView(R.layout.activity_webview);
        final WebView wv = (WebView) findViewById(R.id.webView);
        //String url = getArguments().getString(ARG_URL);
        String url = "file:///android_asset/JsInjectionIntegration.html";
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String[] ref = url.split("#");
                if (url.startsWith("iov://") && ref.length > 1 && ref[1] != null) {
                    String injectedJavascript="javascript:(function() { " +
                            "document.getElementById('" + ref[1] + "').value = '" + getBlackbox(wv.getContext()) +
                            "';})()";
                    wv.loadUrl(injectedJavascript);
                    return true;
                }
                return false;
            }
        });
        //ioBegin(getActivity().getApplicationContext(), wv);
        wv.loadUrl(url);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setAppCacheEnabled(true);
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d("MyApplication", message);
                return super.onJsAlert(view, url, message, result);
            }

            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Log.d("MyApplication", message + " -- From line "
                        + lineNumber + " of "
                        + sourceID);
            }
        });
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
            return getBlackbox(getApplicationContext());
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
