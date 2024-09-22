package com.simple.container;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

public class NovncActivity extends AppCompatActivity {

    //private WebView wv;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.novnc);

        //wv=(WebView) findViewById(R.id.webview);
//        wv.setWebViewClient(new WebViewClient());
//        wv.getSettings().setDomStorageEnabled(true);
//
//        wv.loadUrl("http://www.bilibili.com");

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse("http://localhost:6080/vnc.html"));


    }
}