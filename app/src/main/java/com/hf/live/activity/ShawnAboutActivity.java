package com.hf.live.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hf.live.R;
import com.hf.live.util.AuthorityUtil;
import com.hf.live.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 关于
 */
public class ShawnAboutActivity extends ShawnBaseActivity implements OnClickListener{
	
	private Context mContext;
	private TextView tvAddress;//网址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_about);
		mContext = this;
		initWidget();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.my_about));
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvAddress = findViewById(R.id.tvAddress);
		tvAddress.setText(getString(R.string.web_addr));
		tvAddress.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvAddress.getPaint().setAntiAlias(true);
		tvAddress.setOnClickListener(this);
		TextView tvVersion = findViewById(R.id.tvVersion);
		TextView tvHotline = findViewById(R.id.tvHotline);
		tvHotline.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvHotline.getPaint().setAntiAlias(true);
		tvHotline.setOnClickListener(this);
		ImageView ivLogo = findViewById(R.id.ivLogo);
		
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.iv_logo);
		if (bitmap != null) {
			Bitmap newBitmap = CommonUtil.getRoundedCornerBitmap(bitmap, 10);
			if (newBitmap != null) {
				ivLogo.setImageBitmap(newBitmap);
			}
		}
		
		tvVersion.setText(CommonUtil.getVersion(mContext));
	}
	
	/**
	 * 拨打电话对话框
	 */
	private void dialogCall() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.shawn_dialog_delete, null);
		TextView tvMessage = view.findViewById(R.id.tvMessage);
		TextView tvContent = view.findViewById(R.id.tvContent);
		LinearLayout llNegative = view.findViewById(R.id.llNegative);
		LinearLayout llPositive = view.findViewById(R.id.llPositive);
		TextView tvPositive = view.findViewById(R.id.tvPositive);
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();
		
		tvPositive.setText(getString(R.string.dial_phone));
		tvMessage.setText("服务热线");
		tvContent.setText("010-68408068");
		tvContent.setVisibility(View.VISIBLE);
		
		llNegative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		
		llPositive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + "010-68408068")));
			}
		});
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.llBack) {
			finish();

		} else if (i == R.id.tvAddress) {
			Intent intent = new Intent(mContext, ShawnResponseActivity.class);
			intent.putExtra("activityName", getString(R.string.app_name));
			intent.putExtra("dataUrl", tvAddress.getText().toString());
			startActivity(intent);

		} else if (i == R.id.tvHotline) {
			checkAuthority();

		} else {
		}
	}

	//需要申请的所有权限
	private String[] allPermissions = new String[] {
			Manifest.permission.CALL_PHONE,
	};

	//拒绝的权限集合
	public static List<String> deniedList = new ArrayList<>();
	/**
	 * 申请定位权限
	 */
	private void checkAuthority() {
		if (Build.VERSION.SDK_INT < 23) {
			dialogCall();
		}else {
			deniedList.clear();
			for (String permission : allPermissions) {
				if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
					deniedList.add(permission);
				}
			}
			if (deniedList.isEmpty()) {//所有权限都授予
				dialogCall();
			}else {
				String[] permissions = deniedList.toArray(new String[deniedList.size()]);//将list转成数组
				ActivityCompat.requestPermissions(this, permissions, AuthorityUtil.AUTHOR_PHONE);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case AuthorityUtil.AUTHOR_PHONE:
				if (grantResults.length > 0) {
					boolean isAllGranted = true;//是否全部授权
					for (int gResult : grantResults) {
						if (gResult != PackageManager.PERMISSION_GRANTED) {
							isAllGranted = false;
							break;
						}
					}
					if (isAllGranted) {//所有权限都授予
						dialogCall();
					}else {//只要有一个没有授权，就提示进入设置界面设置
						checkAuthority();
					}
				}else {
					for (String permission : permissions) {
						if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
							checkAuthority();
							break;
						}
					}
				}
				break;
		}
	}
	
}
