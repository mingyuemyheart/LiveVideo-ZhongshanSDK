package com.hf.live.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.hf.live.R;
import com.hf.live.common.CONST;
import com.hf.live.common.MyApplication;
import com.hf.live.util.CommonUtil;
import com.hf.live.util.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 欢迎界面
 */
public class ShawnWelcomeActivity extends Activity{
	
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_welcome);
		mContext = this;

		//获取用户信息
		MyApplication.getUserInfo(mContext);

		String userName = getIntent().getStringExtra("userName");
		String passWord = getIntent().getStringExtra("passWord");
		userName = "shawn1";
		passWord = "123456";
		OkHttpLogin(userName, passWord);
	}

	/**
	 * 用户注册
	 * @param userName
	 * @param passWord
	 */
	private void OkHttpRegister(final String userName, final String passWord) {
		if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord)) {
			return;
		}
		final String url = "http://channellive2.tianqi.cn/weather/user/Register";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("username", userName);
		builder.add("passwd", passWord);
		builder.add("appid", CONST.APPID);
		final RequestBody body = builder.build();
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject object = new JSONObject(result);
								if (!object.isNull("status")) {
									int status = object.getInt("status");
									if (status == 1) {//成功
										MyApplication.USERNAME = userName;
										OkHttpLogin(userName, passWord);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
		}).start();
	}

	/**
	 * 用户登录
	 */
	private void OkHttpLogin(final String userName, final String passWord) {
		final String url = "http://channellive2.tianqi.cn/weather/user/Login";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("username", userName);
		builder.add("passwd", passWord);
		final RequestBody body = builder.build();
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).post(body).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject object = new JSONObject(result);
										if (!object.isNull("status")) {
											int status  = object.getInt("status");
											if (status == 1) {//成功
												if (!object.isNull("info")) {
													JSONObject obj = object.getJSONObject("info");
													if (!obj.isNull("token")) {
														MyApplication.TOKEN = obj.getString("token");
													}
													if (!obj.isNull("phonenumber")) {
														MyApplication.USERNAME = obj.getString("phonenumber");
													}
													if (!obj.isNull("username")) {
														MyApplication.OLDUSERNAME = obj.getString("username");
													}
													if (!obj.isNull("nickname")) {
														MyApplication.NICKNAME = obj.getString("nickname");
													}
													if (!obj.isNull("mail")) {
														MyApplication.MAIL = obj.getString("mail");
													}
													if (!obj.isNull("department")) {
														MyApplication.UNIT = obj.getString("department");
													}
													if (!obj.isNull("groupid")) {
														MyApplication.GROUPID = obj.getString("groupid");
													}
													if (!obj.isNull("points")) {
														MyApplication.POINTS = obj.getString("points");
													}
													if (!obj.isNull("photo")) {
														MyApplication.PHOTO = obj.getString("photo");
													}

													MyApplication.saveUserInfo(mContext);
													MyApplication.getUserInfo(mContext);
													startActivity(new Intent(mContext, ShawnMainActivity.class));
													finish();
												}
											}else if (status == 202) {//登录失败就注册
												OkHttpRegister(userName, passWord);
											}else {
												//失败
												if (!object.isNull("msg")) {
													final String msg = object.getString("msg");
													if (msg != null) {
														Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
													}
												}
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
						});
					}
				});
			}
		}).start();
	}

//	/**
//	 * 刷新用户信息，主要为了刷新token
//	 */
//	private void OkHttpToken(final String userName, final String passWord) {
//		if (TextUtils.isEmpty(MyApplication.TOKEN)) {
//			return;
//		}
//		final String url = "http://channellive2.tianqi.cn/Weather/User/getUser2";//刷新token
//		FormBody.Builder builder = new FormBody.Builder();
//		builder.add("token", MyApplication.TOKEN);
//		final RequestBody body = builder.build();
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				OkHttpUtil.enqueue(new Request.Builder().url(url).post(body).build(), new Callback() {
//					@Override
//					public void onFailure(Call call, IOException e) {
//					}
//					@Override
//					public void onResponse(Call call, Response response) throws IOException {
//						if (!response.isSuccessful()) {
//							return;
//						}
//						final String result = response.body().string();
//						runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								if (!TextUtils.isEmpty(result)) {
//									try {
//										JSONObject object = new JSONObject(result);
//										if (!object.isNull("status")) {
//											int status  = object.getInt("status");
//											if (status == 1) {//成功
//												if (!object.isNull("info")) {
//													JSONObject obj = object.getJSONObject("info");
//													if (!obj.isNull("token")) {
//														MyApplication.TOKEN = obj.getString("token");
//													}
//													if (!obj.isNull("phonenumber")) {
//														MyApplication.USERNAME = obj.getString("phonenumber");
//													}
//													if (!obj.isNull("username")) {
//														MyApplication.OLDUSERNAME = obj.getString("username");
//													}
//													if (!obj.isNull("nickname")) {
//														MyApplication.NICKNAME = obj.getString("nickname");
//													}
//													if (!obj.isNull("mail")) {
//														MyApplication.MAIL = obj.getString("mail");
//													}
//													if (!obj.isNull("department")) {
//														MyApplication.UNIT = obj.getString("department");
//													}
//													if (!obj.isNull("groupid")) {
//														MyApplication.GROUPID = obj.getString("groupid");
//													}
//													if (!obj.isNull("points")) {
//														MyApplication.POINTS = obj.getString("points");
//													}
//													if (!obj.isNull("photo")) {
//														MyApplication.PHOTO = obj.getString("photo");
//													}
//
//													MyApplication.saveUserInfo(mContext);
//													startActivity(new Intent(mContext, ShawnMainActivity.class));
//													finish();
//
//												}
//											}else if (status == 401) {//token无效
//												OkHttpLogin(userName, passWord);
//											}else {
//												//失败
//												if (!object.isNull("msg")) {
//													final String msg = object.getString("msg");
//													if (msg != null) {
//														Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
//													}
//												}
//											}
//										}
//									} catch (JSONException e) {
//										e.printStackTrace();
//									}
//								}
//							}
//						});
//
//					}
//				});
//			}
//		}).start();
//	}
	
//	/**
//	 * 下载头像保存在本地
//	 */
//	private void downloadPortrait(String imgUrl) {
//		AsynLoadTask task = new AsynLoadTask(new AsynLoadCompleteListener() {
//			@Override
//			public void loadComplete(Bitmap bitmap) {
//				FileOutputStream fos;
//				try {
//					File files = new File(CONST.SDCARD_PATH);
//					if (!files.exists()) {
//						files.mkdirs();
//					}
//
//					fos = new FileOutputStream(CONST.PORTRAIT_ADDR);
//					if (bitmap != null) {
//						bitmap.compress(CompressFormat.PNG, 100, fos);
//						if (!bitmap.isRecycled()) {
//							bitmap.recycle();
//						}
//					}
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
//		}, imgUrl);
//        task.execute();
//	}
//
//	private interface AsynLoadCompleteListener {
//		void loadComplete(Bitmap bitmap);
//	}
//
//	private class AsynLoadTask extends AsyncTask<Void, Bitmap, Bitmap> {
//
//		private String imgUrl;
//		private AsynLoadCompleteListener completeListener;
//
//		private AsynLoadTask(AsynLoadCompleteListener completeListener, String imgUrl) {
//			this.imgUrl = imgUrl;
//			this.completeListener = completeListener;
//		}
//
//		@Override
//		protected void onPreExecute() {
//		}
//
//		@Override
//		protected void onProgressUpdate(Bitmap... values) {
//		}
//
//		@Override
//		protected Bitmap doInBackground(Void... params) {
//			Bitmap bitmap = CommonUtil.getHttpBitmap(imgUrl);
//			return bitmap;
//		}
//
//		@Override
//		protected void onPostExecute(Bitmap bitmap) {
//			if (completeListener != null) {
//				completeListener.loadComplete(bitmap);
//            }
//		}
//	}
	
	@Override
	public boolean onKeyDown(int KeyCode, KeyEvent event){
		if (KeyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(KeyCode, event);
	}
	
}
