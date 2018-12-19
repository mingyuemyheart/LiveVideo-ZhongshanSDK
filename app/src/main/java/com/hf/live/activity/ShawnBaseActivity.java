package com.hf.live.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.hf.live.view.MyDialog;

public class ShawnBaseActivity extends Activity {

	private Context mContext;
	private MyDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}
	
	/**
	 * 初始化dialog
	 */
	public void showDialog() {
		if (mDialog == null) {
			mDialog = new MyDialog(mContext);
		}
		mDialog.show();
	}
	public void cancelDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}
	
}
