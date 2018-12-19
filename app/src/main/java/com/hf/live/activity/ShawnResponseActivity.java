package com.hf.live.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hf.live.R;

/**
 * 免责申明
 */
public class ShawnResponseActivity extends ShawnBaseActivity implements OnClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_response);
		initWidget();
		initWebView();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		TextView tvTitle = findViewById(R.id.tvTitle);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);

		String title = getIntent().getStringExtra("activityName");
		if (!TextUtils.isEmpty(title)) {
			tvTitle.setText(title);
		}
	}
	
	/**
	 * 初始化webview
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		String dataUrl = getIntent().getStringExtra("dataUrl");
		if (TextUtils.isEmpty(dataUrl)) {
			return;
		}
		final WebView webView = findViewById(R.id.webView);
        WebSettings webSetting = webView.getSettings();     
        //支持javascript
        webSetting.setJavaScriptEnabled(true); 
  		// 设置可以支持缩放 
        webSetting.setSupportZoom(true); 
  		// 设置出现缩放工具 
        webSetting.setBuiltInZoomControls(false);
  		//扩大比例的缩放
        webSetting.setUseWideViewPort(true);
  		//自适应屏幕
        webSetting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setLoadWithOverviewMode(true);
        webView.setBackgroundColor(0); // 设置背景色
        webView.loadUrl(dataUrl);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String itemUrl) {
				webView.loadUrl(itemUrl);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.llBack) {
			finish();

		} else {
		}
	}
	
}
