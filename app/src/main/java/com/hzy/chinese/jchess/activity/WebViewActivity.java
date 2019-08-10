package com.hzy.chinese.jchess.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;
import com.hzy.chinese.jchess.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tangbull on 2018/3/27.
 */

public class WebViewActivity extends AppCompatActivity {

    public static final String WEB_VIEW_URL = "WEB_VIEW_URL";

    public static void startUrl(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WEB_VIEW_URL, url);
        context.startActivity(intent);
    }

    @BindView(R.id.web_view_web)
    WebView mWebViewWeb;
    @BindView(R.id.web_view_progress)
    ProgressBar mWebViewProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String mInitUrl = intent.getStringExtra(WEB_VIEW_URL);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setupWebView();
        if (!StringUtils.isEmpty(mInitUrl)) {
            mWebViewWeb.loadUrl(mInitUrl);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebViewWeb.canGoBack()) {
            mWebViewWeb.goBack();
            return;
        }
        super.onBackPressed();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        mWebViewProgress.setMax(100);
        WebSettings webSettings = mWebViewWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        if (NetworkUtils.isConnected()) {
            webSettings.setCacheMode(WebSettings.LOAD_NORMAL);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        mWebViewWeb.setWebViewClient(getWebViewClient());
        mWebViewWeb.setWebChromeClient(getWebChromeClient());
        mWebViewWeb.setDownloadListener(getDownloadListener());
    }

    private DownloadListener getDownloadListener() {
        return (url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        };
    }

    private WebViewClient getWebViewClient() {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http")) {
                    view.loadUrl(url);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        };
    }

    private WebChromeClient getWebChromeClient() {
        return new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 0 && newProgress < 100) {
                    mWebViewProgress.setProgress(newProgress);
                    mWebViewProgress.setVisibility(View.VISIBLE);
                } else {
                    mWebViewProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_refresh:
                mWebViewWeb.reload();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebViewWeb != null)
            mWebViewWeb.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebViewWeb != null)
            mWebViewWeb.onResume();
    }

    @Override
    public void onDestroy() {
        if (mWebViewWeb != null) {
            mWebViewWeb.loadDataWithBaseURL(null, "",
                    "text/html", "utf-8", null);
            mWebViewWeb.clearHistory();
            mWebViewWeb.destroy();
        }
        super.onDestroy();
    }
}
