package com.hf.live.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.hf.live.R;

public class FyjpUploadDialog extends Dialog {

	private Context mContext;
	private String message = null;
	private TextView tvPercent = null;
	private TextView tvContent = null;// message
	
	public FyjpUploadDialog(Context context) {
		super(context);
		mContext = context;
	}
	
	public FyjpUploadDialog(Context context, String msg) {
		super(context);
		mContext = context;
		message = msg;
	}
	
	public void setStyle(int featureId) {
		requestWindowFeature(featureId);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(R.color.transparent);
		setContentView(R.layout.fyjp_dialog_uploading);
		initWidget();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		tvContent = (TextView) findViewById(R.id.content);
		tvPercent = (TextView) findViewById(R.id.tvPercent);

		if (tvContent != null) {
			if (message == null) {
				tvContent.setText(mContext.getResources().getString(R.string.is_loading));
			} else {
				tvContent.setText(message);
			}
		}
		
	}
	
	public void setPercent(int percent) {
		if (tvPercent != null) {
			tvPercent.setText(percent + "%");
		}
	}
	
}
