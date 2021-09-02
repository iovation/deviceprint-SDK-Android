package com.iovation.mobile.android.sample.sampleapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.iovation.mobile.android.FraudForceManager;

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

        setContentView(R.layout.activity_webview);
        final WebView wv = (WebView) findViewById(R.id.webView);
        String url = "file:///android_asset/JsInjectionIntegration.html";
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                FraudForceManager.getInstance().refresh(wv.getContext());
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String[] ref = url.split("#");
                if (url.startsWith("iov://") && ref.length > 1 && ref[1] != null) {
                    String injectedJavascript="javascript:(function() { " +
                            "document.getElementById('" + ref[1] + "').value = '" + FraudForceManager.getInstance().getBlackbox(getApplicationContext()) +
                            "';})()";
                    wv.loadUrl(injectedJavascript);
                    return true;
                }
                return false;
            }
        });

        wv.loadUrl(url);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setAppCacheEnabled(true);
    }

}
