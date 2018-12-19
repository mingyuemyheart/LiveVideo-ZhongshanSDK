package com.hf.live.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.hf.live.R;
import com.hf.live.adapter.ShawnEventTypeAdapter;
import com.hf.live.adapter.ShawnWeatherTypeAdapter;
import com.hf.live.common.CONST;
import com.hf.live.common.MyApplication;
import com.hf.live.dto.PhotoDto;
import com.hf.live.dto.UploadVideoDto;
import com.hf.live.util.AuthorityUtil;
import com.hf.live.util.CommonUtil;
import com.hf.live.util.HttpMultipartPost;
import com.hf.live.util.HttpMultipartPost.AsynLoadCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 预览、上传界面
 * @author shawn_sun
 */
public class FyjpDisplayVideoActivity extends Activity implements SurfaceHolder.Callback, OnPreparedListener,
OnVideoSizeChangedListener, OnCompletionListener, OnClickListener, AMapLocationListener{
	
	private Context mContext = null;
	private SurfaceView surfaceView = null;
	private SurfaceHolder surfaceHolder = null;
	private MediaPlayer mPlayer = null;
	private String videoUrl = null;//视频路径
	private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
	private Timer timer = null;
	private int displayW = 0;//屏幕宽
	private int displayH = 0;//屏幕高
	private File thumbnailFile = null;//缩略图文件
	private Configuration configuration = null;//方向监听器
	private static final int HANDLER_PROCESS = 0;
	private static final int HANDLER_VISIBILITY = 1;
	private boolean executeOnce = true;//只执行一次
	private double lat = 0, lng = 0;
	
	//竖屏布局
	private TextView tvPositon,tvDate,tvTextCount;//地址
	private EditText etTitle,etContent;//编辑视频标题
	private ScrollView scrollView;//操作区域
	
	//横屏布局
	private ImageView ivPlayLand = null;//播放按钮
	private TextView tvStartTimeLand,tvEndTimeLand;//开始时间
	private SeekBar seekBarLand = null;//进度条
	private ImageView ivInFull = null;//全屏按钮
	private RelativeLayout reTop,reBottom;//屏幕上方区域
	
	private ShawnWeatherTypeAdapter adapter1 = null;
	private List<UploadVideoDto> list1 = new ArrayList<>();
	private String weatherType = "";//天气类型
	private ShawnEventTypeAdapter adapter2 = null;
	private List<UploadVideoDto> list2 = new ArrayList<>();
	private String eventType = "";//事件类型
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fyjp_activity_display_video);
		mContext = this;
		checkAuthority();
	}

	private void init() {
		initWidget();
		initGridView1();
		initGridView2();
		initSurfaceView();
	}
	
	private void fullScreen(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		configuration = newConfig;
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			showPort();
			fullScreen(false);
		}else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			showLand();
			fullScreen(true);
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		//竖屏布局
		tvPositon = findViewById(R.id.tvPosition);
		tvDate = findViewById(R.id.tvDate);
		etTitle = findViewById(R.id.etTitle);
		etContent = findViewById(R.id.etContent);
		etContent.addTextChangedListener(contentWatcher);
		tvTextCount = findViewById(R.id.tvTextCount);
		TextView tvRemove = findViewById(R.id.tvRemove);
		tvRemove.setOnClickListener(this);
		TextView tvUpload = findViewById(R.id.tvUpload);
		tvUpload.setOnClickListener(this);
		scrollView = findViewById(R.id.scrollView);
		
		//横屏布局
		ImageView ivBackLand = findViewById(R.id.ivBackLand);
		ivBackLand.setOnClickListener(this);
		ivPlayLand = findViewById(R.id.ivPlayLand);
		ivPlayLand.setOnClickListener(this);
		seekBarLand = findViewById(R.id.seekBarLand);
		seekBarLand.setOnTouchListener(seekbarListener);
		tvStartTimeLand = findViewById(R.id.tvStartTimeLand);
		tvStartTimeLand.setText("00:00");
		tvEndTimeLand = findViewById(R.id.tvEndTimeLand);
		ivInFull = findViewById(R.id.ivInFull);
		ivInFull.setOnClickListener(this);
		reTop = findViewById(R.id.reTop);
		reBottom = findViewById(R.id.reBottom);
		LinearLayout llSurfaceView = findViewById(R.id.llSurfaceView);
		llSurfaceView.setOnClickListener(this);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getRealMetrics(dm);
		displayW = dm.widthPixels;
		displayH = dm.heightPixels;
		
		startLocation();
		
		if (getIntent().hasExtra("data")) {
			PhotoDto data = getIntent().getExtras().getParcelable("data");
			if (data != null) {
//				tvPositon.setText(data.getLocation());
				try {
					String time = sdf2.format(sdf3.parse(data.workTime));
					if (time != null) {
						tvDate.setText(time);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				videoUrl = data.videoUrl;
				getThumbnail(videoUrl, new File(videoUrl).getName());
			}
		}
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
	 * 获取视频缩略图
	 * @param workTime
	 */
	private void getThumbnail(String videoUrl, String workTime) {
		Bitmap thumbBitmap = CommonUtil.getVideoThumbnail(videoUrl, displayW/4, displayW/4, Thumbnails.MINI_KIND);
		File files = new File(CONST.THUMBNAIL_ADDR);
		if (!files.exists()) {
			files.mkdirs();
		}

		workTime = workTime.substring(0, workTime.length()-4);
	    thumbnailFile = new File(CONST.THUMBNAIL_ADDR, workTime + ".jpg");
	    FileOutputStream fos;
	    try {
			fos = new FileOutputStream(thumbnailFile);
			thumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
		    fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 显示竖屏，隐藏横屏
	 */
	private void showPort() {
		scrollView.setVisibility(View.VISIBLE);
		ivInFull.setImageResource(R.drawable.iv_out_full);
		changeVideo(mPlayer.getVideoWidth(), mPlayer.getVideoHeight());
	}
	
	/**
	 * 显示横屏，隐藏竖屏
	 */
	private void showLand() {
		scrollView.setVisibility(View.GONE);
		ivInFull.setImageResource(R.drawable.iv_in_full);
		changeVideo(mPlayer.getVideoWidth(), mPlayer.getVideoHeight());
	}
	
	/**
	 * 禁止seekbar监听事件
	 */
	private OnTouchListener seekbarListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			return true;
		}
	};
	
	/**
	 * 初始化surfaceView
	 */
	@SuppressWarnings("deprecation")
	private void initSurfaceView() {
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		surfaceView.setLayoutParams(new LinearLayout.LayoutParams(CONST.standarH, CONST.standarH));
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceHolder = holder;
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer.setDisplay(holder);
		mPlayer.setOnPreparedListener(this);
		mPlayer.setOnVideoSizeChangedListener(this);
		mPlayer.setOnCompletionListener(this);
        //设置显示视频显示在SurfaceView上
        try {
        	if (videoUrl != null) {
            	mPlayer.setDataSource(videoUrl);
            	Log.d("videoUrl", videoUrl);
            	mPlayer.prepareAsync();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
		surfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceHolder = holder;
		releaseTimer();
        releaseMediaPlayer();
	}
	
	@Override
	public void onPrepared(MediaPlayer player) {
		tvStartTimeLand.setText(sdf.format(player.getCurrentPosition()));
		tvEndTimeLand.setText(sdf.format(player.getDuration()));

		seekBarLand.setProgress(0);
		seekBarLand.setMax(player.getDuration()/1000);

    	startPlayVideo();
	}
	
	/**
	 * 开始播放视频
	 */
	private void startPlayVideo() {
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				ivPlayLand.setImageResource(R.drawable.iv_play);
				mPlayer.pause();
				releaseTimer();
			}else {
				ivPlayLand.setImageResource(R.drawable.iv_pause);
				mPlayer.start();
				
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						if(mPlayer==null){
							return;
						} 
				        if (mPlayer.isPlaying() && seekBarLand.isPressed() == false) {  
				            handler.sendEmptyMessage(0);  
				        }  
					}
				}, 0, 1000);
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {  
	    public void handleMessage(Message msg) {  
	    	switch (msg.what) {
			case HANDLER_PROCESS:
				if (mPlayer != null) {
					int position = mPlayer.getCurrentPosition();  
					int duration = mPlayer.getDuration(); 
					
					if (position > 0) {
						if (executeOnce) {
							dismissColunm();
						}
					}
					
					if (duration > 0) {  
						long posLand = seekBarLand.getMax() * position / duration;  
						seekBarLand.setProgress((int) posLand);  
						tvStartTimeLand.setText(sdf.format(position));
					}  
				}
				break;
			case HANDLER_VISIBILITY:
				reTop.setVisibility(View.GONE);
				reBottom.setVisibility(View.GONE);
				ivPlayLand.setVisibility(View.GONE);
				break;

			default:
				break;
			}
	        
	    };  
	};  
	
	/**
	 * 启动线程,隐藏操作栏
	 */
	private void dismissColunm() {
		handler.removeMessages(HANDLER_VISIBILITY);
		Message msg = new Message();
		msg.what = HANDLER_VISIBILITY;
		handler.sendMessageDelayed(msg, 3000);
		executeOnce = false;
	}
	
	/**
	 * 改变横竖屏切换是视频的比例
	 * @param videoW
	 * @param videoH
	 */
	private void changeVideo(int videoW, int videoH) {
		if (surfaceView != null) {
			if (mPlayer != null) {
				int standarH = CONST.standarH;//自定义高度
				if (configuration != null) {
					if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
						standarH = CONST.standarH;
					}else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
						standarH = displayW;
					}
				}
				if (videoW == 0 || videoH == 0) {
					surfaceView.setLayoutParams(new LinearLayout.LayoutParams(standarH, standarH));
					return;
				}else {
					surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoW*standarH/videoH, standarH));
				}
			}
			
//			if (configuration != null) {
//				if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//					surfaceView.setLayoutParams(new LinearLayout.LayoutParams(displayW, displayW*videoH/videoW));
//				}else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//					surfaceView.setLayoutParams(new LinearLayout.LayoutParams(displayH, displayH*videoH/videoW));
//				}
//			}else {
//				surfaceView.setLayoutParams(new LinearLayout.LayoutParams(displayW, displayW*videoH/videoW));
//			}
		}
	}
	
	@Override
	public void onVideoSizeChanged(MediaPlayer player, int videoW, int videoH) {
		changeVideo(videoW, videoH);
	}
	
	@Override
	public void onCompletion(MediaPlayer player) {
		releaseTimer();
		ivPlayLand.setImageResource(R.drawable.iv_play);
		seekBarLand.setProgress(0);
		tvStartTimeLand.setText("00:00");
		handler.removeMessages(HANDLER_VISIBILITY);
		reTop.setVisibility(View.VISIBLE);
		reBottom.setVisibility(View.VISIBLE);
		ivPlayLand.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 释放MediaPlayer资源
	 */
	private void releaseMediaPlayer() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}
	
	/**
	 * 释放timer
	 */
	private void releaseTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
		releaseTimer();
        releaseMediaPlayer();
    }
	
	/**
	 * 删除视频对话框
	 */
	private void deleteVideoDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.fyjp_dialog_delete, null);
		TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
		LinearLayout llNegative = (LinearLayout) view.findViewById(R.id.llNegative);
		LinearLayout llPositive = (LinearLayout) view.findViewById(R.id.llPositive);
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();
		
		tvMessage.setText(getString(R.string.delete_video));
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
				File file = new File(videoUrl);
				if (file.exists()) {
					file.delete();
				}
				if (thumbnailFile.exists()) {
					thumbnailFile.delete();
				}
				
				//发送广播，刷新已上传、未上传
				Intent intent = new Intent();
				intent.setAction(CONST.REFRESH_UPLOAD);
				sendBroadcast(intent);
				
				Intent intent2 = new Intent();
				intent2.setAction(CONST.REFRESH_NOTUPLOAD);
				sendBroadcast(intent2);
				
				setResult(RESULT_OK);
				finish();
			}
		});
	}
	
	/**
	 * 上传视频对话框
	 */
	private void uploadVideoDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.fyjp_dialog_upload, null);
		LinearLayout llPositive = (LinearLayout) view.findViewById(R.id.llPositive);
		
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
	 * 上传视频
	 */
	private void uploadVideo() {
		String url = "http://channellive2.tianqi.cn/weather/work/Upload";
		File videoFile = new File(videoUrl);
		String fileName = videoFile.getName().substring(0, videoFile.getName().length()-4);
    	try {
			HttpMultipartPost httpPost = new HttpMultipartPost(mContext, url, new AsynLoadCompleteListener() {
				@Override
				public void loadComplete(String result) {
					if (result.equals(getString(R.string.file_too_big))) {
						Toast.makeText(mContext, getString(R.string.file_too_big), Toast.LENGTH_LONG).show();
						return;
					}
					try {
						JSONObject obj = new JSONObject(result);
						if (!obj.isNull("status")) {
							if (obj.getInt("status") == 1) {//上传成功
								//删除保存在本地的文件
//								File file = new File(videoUrl);
//								if (file.exists()) {
//									file.delete();
//								}
//								if (thumbnailFile.exists()) {
//									thumbnailFile.delete();
//								}
								
								//发送广播，刷新已上传、未上传
								Intent intent = new Intent();
								intent.setAction(CONST.REFRESH_UPLOAD);
								sendBroadcast(intent);
								
								Intent intent2 = new Intent();
								intent2.setAction(CONST.REFRESH_NOTUPLOAD);
								sendBroadcast(intent2);

								uploadSuccessDialog();
							}else {//上传失败
								Toast.makeText(mContext, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
        	httpPost.setToken(MyApplication.TOKEN);
        	httpPost.setWorkstype("video");
        	httpPost.setLocation(tvPositon.getText().toString());
        	httpPost.setTitle(etTitle.getText().toString());
        	httpPost.setContent(etContent.getText().toString());
        	if (fileName.length() == 14) {
            	httpPost.setWorkTime(sdf2.format(sdf3.parse(fileName)));
			}else {
	        	httpPost.setWorkTime(tvDate.getText().toString());
			}
        	httpPost.setLatlon(lat+","+lng);
        	httpPost.setWeather_flag(weatherType);
        	httpPost.setOther_flags(eventType);
			httpPost.setVideoFile(videoFile);
			httpPost.setThumbnailFile(thumbnailFile);
	    	httpPost.execute();  
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传视频成功对话框
	 */
	private void uploadSuccessDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.fyjp_dialog_upload_success, null);
		LinearLayout llPositive = (LinearLayout) view.findViewById(R.id.llPositive);
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		
		llPositive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				setResult(RESULT_OK);
				exit();
			}
		});
	}
	
	private void exit() {
		if (configuration == null) {
			releaseTimer();
	        releaseMediaPlayer();
			finish();
		}else {
			if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
				releaseTimer();
		        releaseMediaPlayer();
				finish();
			}else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.ivBackLand) {
			exit();

		} else if (i == R.id.llSurfaceView) {
			if (mPlayer != null && mPlayer.isPlaying()) {
				if (reBottom.getVisibility() == View.VISIBLE) {
					reTop.setVisibility(View.GONE);
					reBottom.setVisibility(View.GONE);
					ivPlayLand.setVisibility(View.GONE);
				} else {
					reTop.setVisibility(View.VISIBLE);
					reBottom.setVisibility(View.VISIBLE);
					ivPlayLand.setVisibility(View.VISIBLE);
					dismissColunm();
				}
			} else {
				reTop.setVisibility(View.VISIBLE);
				reBottom.setVisibility(View.VISIBLE);
				ivPlayLand.setVisibility(View.VISIBLE);
			}

		} else if (i == R.id.ivInFull) {
			if (configuration == null) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else {
				if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				} else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
			}

		} else if (i == R.id.ivPlayLand) {
			dismissColunm();
			startPlayVideo();

		} else if (i == R.id.tvRemove) {
			deleteVideoDialog();

		} else if (i == R.id.tvUpload) {
			if (TextUtils.isEmpty(weatherType) || TextUtils.isEmpty(etTitle.getText().toString())) {
				uploadVideoDialog();
			} else {
				uploadVideo();
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
