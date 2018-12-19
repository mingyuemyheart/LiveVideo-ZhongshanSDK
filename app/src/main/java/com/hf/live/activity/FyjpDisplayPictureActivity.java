package com.hf.live.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.hf.live.R;
import com.hf.live.adapter.ShawnDisplayPictureAdapter;
import com.hf.live.adapter.ShawnEventTypeAdapter;
import com.hf.live.adapter.ShawnWeatherTypeAdapter;
import com.hf.live.common.CONST;
import com.hf.live.common.MyApplication;
import com.hf.live.dto.PhotoDto;
import com.hf.live.dto.UploadVideoDto;
import com.hf.live.util.AuthorityUtil;
import com.hf.live.util.CommonUtil;
import com.scene.net.Net;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 图片预览并上传
 */
public class FyjpDisplayPictureActivity extends FyjpBaseActivity implements OnClickListener, AMapLocationListener{
	
	private Context mContext = null;
	public TextView tvTitle = null;
	private GridView mGridView = null;
	private ShawnDisplayPictureAdapter mAdapter = null;
	private TextView tvPositon = null;//地址
	private EditText etTitle = null;//标题
	private EditText etContent = null;
	private TextView tvTextCount = null;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	private List<PhotoDto> selectList = new ArrayList<>();
	private ShawnWeatherTypeAdapter adapter1 = null;
	private List<UploadVideoDto> list1 = new ArrayList<>();
	private String weatherType = "";//天气类型
	private ShawnEventTypeAdapter adapter2 = null;
	private List<UploadVideoDto> list2 = new ArrayList<>();
	private String eventType = "";//事件类型
	private int count = 0;
	private double lat = 0, lng = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fyjp_activity_display_picture);
		mContext = this;
		init();
	}

	private void init() {
		initWidget();
		initGridView();
		initGridView1();
		initGridView2();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		tvTitle = findViewById(R.id.tvTitle);
		tvPositon = findViewById(R.id.tvPosition);
		TextView tvDate = findViewById(R.id.tvDate);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvRemove = findViewById(R.id.tvRemove);
		tvRemove.setOnClickListener(this);
		TextView tvUpload = findViewById(R.id.tvUpload);
		tvUpload.setOnClickListener(this);
		etTitle = findViewById(R.id.etTitle);
		etContent = findViewById(R.id.etContent);
		etContent.addTextChangedListener(contentWatcher);
		tvTextCount = findViewById(R.id.tvTextCount);
		
//		String cityName = getIntent().getStringExtra("cityName");
//		if (cityName != null) {
//			tvPositon.setText(cityName);
//		}
		try {
			String time = sdf2.format(sdf1.parse(getIntent().getStringExtra("takeTime")));
			if (time != null) {
				tvDate.setText(time);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		selectList.clear();
		selectList.addAll(getIntent().getExtras().<PhotoDto>getParcelableArrayList("selectList"));
		
		startLocation();
	}
	
	/**
	 * 开始定位
	 */
	private void startLocation() {
		AMapLocationClientOption mLocationOption = new AMapLocationClientOption();//初始化定位参数
		AMapLocationClient mLocationClient = new AMapLocationClient(mContext);//初始化定位
        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setOnceLocation(true);//设置是否只定位一次,默认为false
        mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
        mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();//启动定位
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
    	lat = amapLocation.getLatitude();
    	lng = amapLocation.getLongitude();
    	tvPositon.setText(amapLocation.getCity()+" "+amapLocation.getDistrict());
	}
	
	/**
	 * 初始化gridview
	 */
	private void initGridView() {
		for (int i = 0; i < selectList.size(); i++) {
			selectList.get(i).isSelected = true;
			if (i < 9) {
				selectList.get(i).isSelected = true;
				count++;
			}
		}
		tvTitle.setText(getString(R.string.already_select)+count+getString(R.string.file_count));
		
		mGridView = findViewById(R.id.gridView);
		mAdapter = new ShawnDisplayPictureAdapter(mContext, selectList);
		mGridView.setAdapter(mAdapter);
		CommonUtil.setGridViewHeightBasedOnChildren(mGridView);
	}

	/**
	 * 输入内容监听器
	 */
	private TextWatcher contentWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void afterTextChanged(Editable arg0) {
			if (etContent.getText().length() == 0) {
				tvTextCount.setText("(200字以内)");
			}else {
				int count = 200-etContent.getText().length();
				tvTextCount.setText("(还可输入"+count+"字)");
			}
		}
	};
	
	/**
	 * 初始化天气类型gridview
	 */
	private void initGridView1() {
		//wt01雪，wt02雨，wt03冰雹，wt04晴，wt05霾，wt06大风，wt07沙尘
		list1.clear();
		UploadVideoDto dto = new UploadVideoDto();
		dto.weatherType = "wt01";
		dto.weatherName = "雪";
		dto.isSelected = false;
		list1.add(dto);
		dto = new UploadVideoDto();
		dto.weatherType = "wt02";
		dto.weatherName = "雨";
		dto.isSelected = false;
		list1.add(dto);
		dto = new UploadVideoDto();
		dto.weatherType = "wt03";
		dto.weatherName = "冰雹";
		dto.isSelected = false;
		list1.add(dto);
		dto = new UploadVideoDto();
		dto.weatherType = "wt04";
		dto.weatherName = "晴";
		dto.isSelected = false;
		list1.add(dto);
		dto = new UploadVideoDto();
		dto.weatherType = "wt05";
		dto.weatherName = "霾";
		dto.isSelected = false;
		list1.add(dto);
		dto = new UploadVideoDto();
		dto.weatherType = "wt06";
		dto.weatherName = "大风";
		dto.isSelected = false;
		list1.add(dto);
		dto = new UploadVideoDto();
		dto.weatherType = "wt07";
		dto.weatherName = "沙尘";
		dto.isSelected = false;
		list1.add(dto);

		GridView gridView1 = findViewById(R.id.gridView1);
		adapter1 = new ShawnWeatherTypeAdapter(mContext, list1);
		gridView1.setAdapter(adapter1);
		gridView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				for (int i = 0; i < list1.size(); i++) {
					if (i == arg2) {
						list1.get(i).isSelected = true;
						weatherType = list1.get(i).weatherType;
					}else {
						list1.get(i).isSelected = false;
					}
				}
				if (adapter1 != null) {
					adapter1.notifyDataSetChanged();
				}
			}
		});
	}
	
	/**
	 * 初始化事件类型gridview
	 */
	private void initGridView2() {
		//et01自然灾害，et02事故灾害，et03公共卫生，et04社会安全
		list2.clear();
		UploadVideoDto dto = new UploadVideoDto();
		dto.eventType = "et01";
		dto.eventName = "自然灾害";
		dto.isSelected = false;
		list2.add(dto);dto = new UploadVideoDto();
		dto.eventType = "et02";
		dto.eventName = "事故灾害";
		dto.isSelected = false;
		list2.add(dto);dto = new UploadVideoDto();
		dto.eventType = "et03";
		dto.eventName = "公共卫生";
		dto.isSelected = false;
		list2.add(dto);dto = new UploadVideoDto();
		dto.eventType = "et04";
		dto.eventName = "社会安全";
		dto.isSelected = false;
		list2.add(dto);

		GridView gridView2 = findViewById(R.id.gridView2);
		adapter2 = new ShawnEventTypeAdapter(mContext, list2);
		gridView2.setAdapter(adapter2);
		gridView2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				for (int i = 0; i < list2.size(); i++) {
					if (i == arg2) {
						list2.get(i).isSelected = true;
						eventType = list2.get(i).eventType;
					}else {
						list2.get(i).isSelected = false;
					}
				}
				if (adapter2 != null) {
					adapter2.notifyDataSetChanged();
				}
			}
		});
	}
	
	/**
	 * 删除图片对话框
	 */
	private void deleteDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.fyjp_dialog_delete, null);
		TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
		LinearLayout llNegative = (LinearLayout) view.findViewById(R.id.llNegative);
		LinearLayout llPositive = (LinearLayout) view.findViewById(R.id.llPositive);
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();
		
		final List<PhotoDto> tempList = new ArrayList<PhotoDto>();
		tempList.clear();
		for (int i = 0; i < selectList.size(); i++) {
			PhotoDto dto = selectList.get(i);
			if (dto.isSelected) {
				tempList.add(dto);
			}
		}
		tvMessage.setText(getString(R.string.delete_pics) + tempList.size() + getString(R.string.pictrue));
		
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
				selectList.removeAll(tempList);
				mAdapter.notifyDataSetChanged();
				CommonUtil.setGridViewHeightBasedOnChildren(mGridView);
				for (int j = 0; j < tempList.size(); j++) {
					File file = new File(tempList.get(j).imgUrl);
					if (file.exists()) {
						file.delete();
					}
				}
				
				int count = 0;
				for (int i = 0; i < selectList.size(); i++) {
					if (selectList.get(i).isSelected) {
						count++;
					}
				}
				tvTitle.setText(getString(R.string.already_select)+count+getString(R.string.file_count));

				//发送刷新未上传广播
//				Toast.makeText(mContext, getString(R.string.delete_all_files), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setAction(CONST.REFRESH_NOTUPLOAD);
				sendBroadcast(intent);
				if (selectList.size() <= 0) {
					finish();
				}
			}
		});
	}
	
	/**
	 * 检查上传标题dialog
	 */
	private void dialogTitle() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.fyjp_dialog_upload, null);
		LinearLayout llPositive = view.findViewById(R.id.llPositive);
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();
		
		llPositive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}
	
	/**
	 * 上传图片对话框
	 */
	private void dialogUpload() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.fyjp_dialog_delete, null);
		TextView tvMessage = view.findViewById(R.id.tvMessage);
		LinearLayout llNegative = view.findViewById(R.id.llNegative);
		LinearLayout llPositive = view.findViewById(R.id.llPositive);
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();
		
		final List<PhotoDto> tempList = new ArrayList<>();
		tempList.clear();
		for (int i = 0; i < selectList.size(); i++) {
			PhotoDto dto = selectList.get(i);
			if (dto.isSelected) {
				tempList.add(dto);
			}
		}
		tvMessage.setText(getString(R.string.upload_pics) + tempList.size() + getString(R.string.pictrue));
		
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
				OkHttpUpload();
			}
		});
	}
	
	/**
	 * 上传图片
	 */
	private void OkHttpUpload() {
		showDialog();
		final String url = "http://channellive2.tianqi.cn/weather/work/Upload";
		AjaxParams params = new AjaxParams();
		params.put("appid", CONST.APPID);
		params.put("token", MyApplication.TOKEN);
		params.put("workstype", "imgs");
		params.put("latlon", lat+","+lng);
		params.put("title", etTitle.getText().toString());
		if (!TextUtils.isEmpty(etContent.getText().toString())) {
			params.put("content", etContent.getText().toString());
		}
		params.put("weatherType", weatherType);
		if (!TextUtils.isEmpty(eventType)) {
			params.put("eventType", eventType);
		}

		String location = tvPositon.getText().toString();
		if (TextUtils.isEmpty(location)) {
			location = getString(R.string.no_location);
		}
		params.put("location", location);

		final List<PhotoDto> tempList = new ArrayList<PhotoDto>();
		tempList.clear();
		for (int i = 0; i < selectList.size(); i++) {
			PhotoDto dto = selectList.get(i);
			if (dto.isSelected) {
				tempList.add(dto);
			}
		}

		for (int i = 0; i < tempList.size(); i++) {
			File pictureFile = new File(tempList.get(i).imgUrl);
			String fileName = pictureFile.getName().substring(0, pictureFile.getName().length()-4);
			try {
//				params.put("work_time", sdf2.format(sdf1.parse(fileName)));
				params.put("work_time", sdf2.format(new Date()));
				params.put("imgs" + Integer.valueOf(i + 1), pictureFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		Net.post(url, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				cancelDialog();
				selectList.removeAll(tempList);
				mAdapter.notifyDataSetChanged();
				CommonUtil.setGridViewHeightBasedOnChildren(mGridView);
//				for (int j = 0; j < tempList.size(); j++) {
//					File file = new File(tempList.get(j).getUrl());
//					if (file.exists()) {
//						file.delete();
//					}
//				}

				int count = 0;
				for (int i = 0; i < selectList.size(); i++) {
					if (selectList.get(i).isSelected) {
						count++;
					}
				}
				tvTitle.setText(getString(R.string.already_select)+count+getString(R.string.file_count));
//				Toast.makeText(mContext, getString(R.string.upload_all_files), Toast.LENGTH_SHORT).show();
				//发送刷新未上传广播
				Intent intent = new Intent();
				intent.setAction(CONST.REFRESH_NOTUPLOAD);
				sendBroadcast(intent);
				if (selectList.size() <= 0) {
					finish();
				}
			}

			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				cancelDialog();
				Toast.makeText(mContext, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void exitDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.fyjp_dialog_delete, null);
		TextView tvMessage = view.findViewById(R.id.tvMessage);
		LinearLayout llNegative = view.findViewById(R.id.llNegative);
		LinearLayout llPositive = view.findViewById(R.id.llPositive);
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();
		
		tvMessage.setText(getString(R.string.sure_exit));
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
				finish();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitDialog();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.llBack) {
			exitDialog();

		} else if (i == R.id.tvRemove) {
			deleteDialog();

		} else if (i == R.id.tvUpload) {
			if (TextUtils.isEmpty(weatherType) || TextUtils.isEmpty(etTitle.getText().toString())) {
				dialogTitle();
			} else {
				dialogUpload();
			}

		} else {
		}
	}

	//需要申请的所有权限
	private String[] allPermissions = new String[] {
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	//拒绝的权限集合
	public static List<String> deniedList = new ArrayList<>();
	/**
	 * 申请定位权限
	 */
	private void checkAuthority() {
		if (Build.VERSION.SDK_INT < 23) {
			init();
		}else {
			deniedList.clear();
			for (String permission : allPermissions) {
				if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
					deniedList.add(permission);
				}
			}
			if (deniedList.isEmpty()) {//所有权限都授予
				init();
			}else {
				String[] permissions = deniedList.toArray(new String[deniedList.size()]);//将list转成数组
				ActivityCompat.requestPermissions(this, permissions, AuthorityUtil.AUTHOR_LOCATION);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case AuthorityUtil.AUTHOR_LOCATION:
				if (grantResults.length > 0) {
					boolean isAllGranted = true;//是否全部授权
					for (int gResult : grantResults) {
						if (gResult != PackageManager.PERMISSION_GRANTED) {
							isAllGranted = false;
							break;
						}
					}
					if (isAllGranted) {//所有权限都授予
						init();
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
