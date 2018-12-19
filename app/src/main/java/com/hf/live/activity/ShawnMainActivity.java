package com.hf.live.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.hf.live.R;
import com.hf.live.common.MyApplication;
import com.hf.live.dto.PhotoDto;
import com.hf.live.util.AuthorityUtil;
import com.hf.live.util.GetPathFromUri4kitkat;
import com.hf.live.util.WeatherUtil;
import com.hf.live.util.sofia.Sofia;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.com.weather.api.WeatherAPI;
import cn.com.weather.beans.Weather;
import cn.com.weather.constants.Constants.Language;
import cn.com.weather.listener.AsyncResponseHandler;

/**
 * 主界面
 */
public class ShawnMainActivity extends ShawnBaseActivity implements AMapLocationListener, OnClickListener{
	
	private Context mContext;
	private TextView tvAddress,tvTime,tvPhenomenon,tvFactTemp,tvBodyTemp,tvWind,tvHumitidy,tvRainFall,tvAQI;
	private ImageView ivRefresh,ivPhenomenon;
	private LinearLayout llTable;//数据桌面
	private ProgressBar progressBar;
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
	private long mExitTime;//记录点击完返回按钮后的long型时间
	public static int flag = 1;//1为影视、2为会商,别忘记修改安装logo、高德地图key

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_main);
		MyApplication.addDestoryActivity(this, "ShawnMainActivity");
		mContext = this;
		Sofia.with(this)
				.invasionStatusBar()//设置顶部状态栏缩进
				.statusBarBackground(Color.TRANSPARENT);//设置状态栏颜色
		initWidget();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		tvAddress = findViewById(R.id.tvAddress);
		tvTime = findViewById(R.id.tvTime);
		ivPhenomenon = findViewById(R.id.ivPhenomenon);
		tvPhenomenon = findViewById(R.id.tvPhenomenon);
		tvFactTemp = findViewById(R.id.tvFactTemp);
		tvBodyTemp = findViewById(R.id.tvBodyTemp);
		tvWind = findViewById(R.id.tvWind);
		tvHumitidy = findViewById(R.id.tvHumitidy);
		tvRainFall = findViewById(R.id.tvRainFall);
		tvAQI = findViewById(R.id.tvAQI);
		ivRefresh = findViewById(R.id.ivRefresh);
		ivRefresh.setOnClickListener(this);
		llTable = findViewById(R.id.llTable);
		RelativeLayout reLive = findViewById(R.id.reLive);
		reLive.setOnClickListener(this);
		RelativeLayout reMeet = findViewById(R.id.reMeet);
		reMeet.setOnClickListener(this);
		RelativeLayout reCamera = findViewById(R.id.reCamera);
		reCamera.setOnClickListener(this);
		RelativeLayout reVideo = findViewById(R.id.reVideo);
		reVideo.setOnClickListener(this);
		progressBar = findViewById(R.id.progressBar);

		checkAuthority();
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
		tvAddress.setText(amapLocation.getCity()+amapLocation.getDistrict()+amapLocation.getStreet()+amapLocation.getStreetNum());
    	getWeatherInfo(amapLocation.getLatitude(), amapLocation.getLongitude());
	}

	/**
	 * 获取天气数据
	 * @param lat
	 * @param lng
	 */
	private void getWeatherInfo(final double lat, final double lng) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				WeatherAPI.getGeo(mContext,lng+"", lat+"", new AsyncResponseHandler(){
					@Override
					public void onComplete(JSONObject content) {
						super.onComplete(content);
						if (!content.isNull("geo")) {
							try {
								JSONObject geoObj = content.getJSONObject("geo");
								if (!geoObj.isNull("id")) {
									String cityId = geoObj.getString("id");
									if (cityId != null) {
										WeatherAPI.getWeather2(mContext, cityId, Language.ZH_CN, new AsyncResponseHandler() {
											@Override
											public void onComplete(final Weather content) {
												super.onComplete(content);
												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														if (content != null) {
															try {
																JSONObject object = content.getWeatherFactInfo();//实况信息
																if (!object.isNull("l7")) {
																	String time = object.getString("l7");
																	if (time != null) {
																		tvTime.setText(time + getString(R.string.refresh));
																	}
																}
																if (!object.isNull("l5")) {
																	String weatherCode = WeatherUtil.lastValue(object.getString("l5"));
																	if (weatherCode != null) {
																		Drawable drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
																		drawable.setLevel(Integer.valueOf(weatherCode));
																		ivPhenomenon.setImageDrawable(drawable);
																		tvPhenomenon.setText(getString(WeatherUtil.getWeatherId(Integer.valueOf(weatherCode))));
																	}
																}
																if (!object.isNull("l1")) {
																	String factTemp = WeatherUtil.lastValue(object.getString("l1"));
																	if (factTemp != null) {
																		tvFactTemp.setText(getString(R.string.current) + factTemp + getString(R.string.unit_c));
																	}
																}
																if (!object.isNull("l12")) {
																	String bodyTemp = WeatherUtil.lastValue(object.getString("l12"));
																	if (bodyTemp != null) {
																		tvBodyTemp.setText(getString(R.string.body) + bodyTemp + getString(R.string.unit_c));
																	}
																}
																if (!object.isNull("l4") && !object.isNull("l3")) {
																	String windDir = WeatherUtil.lastValue(object.getString("l4"));
																	String windForce = WeatherUtil.lastValue(object.getString("l3"));
																	if (windDir != null && windForce != null) {
																		tvWind.setText(getString(WeatherUtil.getWindDirection(Integer.valueOf(windDir))) + WeatherUtil.getFactWindForce(Integer.valueOf(windForce)));
																	}
																}
																if (!object.isNull("l2")) {
																	String humidity = WeatherUtil.lastValue(object.getString("l2"));
																	if (humidity != null) {
																		tvHumitidy.setText(getString(R.string.humitidy) + humidity + getString(R.string.unit_percent));
																	}
																}
																if (!object.isNull("l6")) {
																	String rainFall = WeatherUtil.lastValue(object.getString("l6"));
																	if (rainFall != null) {
																		tvRainFall.setText(getString(R.string.rainfall) + rainFall + getString(R.string.mm));
																	}
																}

																JSONObject airObj = content.getAirQualityInfo();//空气质量信息
																if (!airObj.isNull("k3")) {
																	String airQua = WeatherUtil.lastValue(airObj.getString("k3"));
																	if (airQua != null) {
																		tvAQI.setText(getString(R.string.aqi) + airQua);
																	}
																}
															} catch (JSONException e) {
																e.printStackTrace();
															}

															progressBar.setVisibility(View.GONE);
															llTable.setVisibility(View.VISIBLE);
															ivRefresh.clearAnimation();
														}
													}
												});

											}
										});
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onError(Throwable error, String content) {
						super.onError(error, content);
					}
				});
			}
		}).start();
	}
	
	/**
	 * 拍摄对话框
	 */
	private void dialogCamera() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.shawn_dialog_camera, null);
		RelativeLayout reCamera = view.findViewById(R.id.reCamera);
		RelativeLayout reSelect = view.findViewById(R.id.reSelect);
		RelativeLayout rePicture = view.findViewById(R.id.rePicture);
		TextView tvCamera = view.findViewById(R.id.tvCamera);
		TextView tvSelect = view.findViewById(R.id.tvSelect);
		TextView tvPicture = view.findViewById(R.id.tvPicture);
		tvCamera.setText(getString(R.string.camera_video));
		tvSelect.setText(getString(R.string.select_video));
		tvPicture.setText(getString(R.string.select_pic));
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.getWindow().setGravity(Gravity.CENTER|Gravity.BOTTOM);
		dialog.show();
		
		reCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (MyApplication.TOKEN != null) {
					startActivity(new Intent(mContext, ShawnCameraActivity.class));
				}
				dialog.dismiss();
			}
		});
		
		reSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (MyApplication.TOKEN != null) {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				    intent.setType("video/*");
				    startActivityForResult(intent, 1);
				}
				dialog.dismiss();
			}
		});
		
		rePicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (MyApplication.TOKEN != null) {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				    intent.setType("image/*");
				    startActivityForResult(intent, 3);
				}
				dialog.dismiss();
			}
		});
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(mContext, getString(R.string.confirm_exit)+getString(R.string.app_name), Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.ivRefresh) {
			Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.round_animation);
			ivRefresh.startAnimation(animation);
			startLocation();

		} else if (i == R.id.reLive) {
			Toast.makeText(mContext, "正在研发中", Toast.LENGTH_SHORT).show();

		} else if (i == R.id.reCamera) {
			dialogCamera();

		} else if (i == R.id.reVideo) {
			startActivity(new Intent(mContext, ShawnVideoWallActivity.class));//视频墙

		} else if (i == R.id.reMeet) {
			if (flag == 1) {
				startActivity(new Intent(mContext, ShawnPersonCenterActivity.class));
			}

		} else {
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				startActivity(new Intent(mContext, ShawnCameraActivity.class));
				break;
			case 1:
				if (data != null) {
					Uri uri = data.getData();
					String filePath = GetPathFromUri4kitkat.getPath(mContext, uri);
					if (filePath == null) {
						Toast.makeText(mContext, getString(R.string.not_found_video), Toast.LENGTH_SHORT).show();    
						return;
					}
					
					//跳转到预览视频界面
					PhotoDto dto = new PhotoDto();
					dto.setLocation(getString(R.string.no_location));
					dto.setWorkTime(sdf2.format(System.currentTimeMillis()));
					dto.setVideoUrl(filePath);
					Intent intent = new Intent(mContext, ShawnDisplayVideoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable("data", dto);
					intent.putExtras(bundle);
					startActivity(intent);
				}else {
					Toast.makeText(mContext, getString(R.string.not_found_video), Toast.LENGTH_SHORT).show();    
					return;
				}
				break;
			case 2:
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			    i.setType("video/*");
			    startActivityForResult(i, 1);
				break;
			case 3:
				if (data != null) {
					Uri uri = data.getData();
					String filePath = GetPathFromUri4kitkat.getPath(mContext, uri);
					if (filePath == null) {
						Toast.makeText(mContext, getString(R.string.not_found_pic), Toast.LENGTH_SHORT).show();    
						return;
					}
					
					//跳转到预览图片界面
					List<PhotoDto> selectList = new ArrayList<>();
					PhotoDto dto = new PhotoDto();
				    dto.setState(true);
					dto.setWorkstype("imgs");
				    dto.imgUrl = filePath;
				    selectList.add(dto);
					Intent intent = new Intent(mContext, ShawnDisplayPictureActivity.class);
				    intent.putExtra("cityName", getString(R.string.no_location));
				    intent.putExtra("takeTime", sdf2.format(System.currentTimeMillis()));
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList("selectList", (ArrayList<? extends Parcelable>) selectList);
					intent.putExtras(bundle);
					startActivity(intent);
				}else {
					Toast.makeText(mContext, getString(R.string.not_found_pic), Toast.LENGTH_SHORT).show();    
					return;
				}
				break;
			case 4:
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
			    startActivityForResult(intent, 3);
				break;

			default:
				break;
			}
		}
	}

	//需要申请的所有权限
	private String[] allPermissions = new String[] {
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.READ_PHONE_STATE
	};

	//拒绝的权限集合
	public static List<String> deniedList = new ArrayList<>();
	/**
	 * 申请定位权限
	 */
	private void checkAuthority() {
		if (Build.VERSION.SDK_INT < 23) {
			startLocation();
		}else {
			deniedList.clear();
			for (String permission : allPermissions) {
				if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
					deniedList.add(permission);
				}
			}
			if (deniedList.isEmpty()) {//所有权限都授予
				startLocation();
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
						startLocation();
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
