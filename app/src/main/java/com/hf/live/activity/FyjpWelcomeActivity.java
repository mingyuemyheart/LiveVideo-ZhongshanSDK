package com.hf.live.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.hf.live.R;
import com.hf.live.common.FyjpApplication;
import com.hf.live.common.FyjpCONST;
import com.hf.live.util.FyjpOkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

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
public class FyjpWelcomeActivity extends Activity{
	
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fyjp_activity_welcome);
		mContext = this;

		//获取用户信息
		FyjpApplication.getUserInfo(mContext);

		String userName,passWord;
		if (getIntent().hasExtra("userName") && getIntent().hasExtra("passWord")) {
			userName = getIntent().getStringExtra("userName");
			passWord = getIntent().getStringExtra("passWord");
		}else {
			userName = "shawn1";
			passWord = "123456";
		}
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
		builder.add("appid", FyjpCONST.APPID);
		final RequestBody body = builder.build();
		new Thread(new Runnable() {
			@Override
			public void run() {
				FyjpOkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
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
										FyjpApplication.USERNAME = userName;
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
				FyjpOkHttpUtil.enqueue(new Request.Builder().url(url).post(body).build(), new Callback() {
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
														FyjpApplication.TOKEN = obj.getString("token");
													}
													if (!obj.isNull("phonenumber")) {
														FyjpApplication.USERNAME = obj.getString("phonenumber");
													}
													if (!obj.isNull("username")) {
														FyjpApplication.OLDUSERNAME = obj.getString("username");
													}
													if (!obj.isNull("nickname")) {
														FyjpApplication.NICKNAME = obj.getString("nickname");
													}
													if (!obj.isNull("mail")) {
														FyjpApplication.MAIL = obj.getString("mail");
													}
													if (!obj.isNull("department")) {
														FyjpApplication.UNIT = obj.getString("department");
													}
													if (!obj.isNull("groupid")) {
														FyjpApplication.GROUPID = obj.getString("groupid");
													}
													if (!obj.isNull("points")) {
														FyjpApplication.POINTS = obj.getString("points");
													}
													if (!obj.isNull("photo")) {
														FyjpApplication.PHOTO = obj.getString("photo");
													}

													FyjpApplication.saveUserInfo(mContext);
													FyjpApplication.getUserInfo(mContext);
													startActivity(new Intent(mContext, FyjpMainActivity.class));
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
//		if (TextUtils.isEmpty(FyjpApplication.TOKEN)) {
//			return;
//		}
//		final String url = "http://channellive2.tianqi.cn/Weather/User/getUser2";//刷新token
//		FormBody.Builder builder = new FormBody.Builder();
//		builder.add("token", FyjpApplication.TOKEN);
//		final RequestBody body = builder.build();
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				FyjpOkHttpUtil.enqueue(new Request.Builder().url(url).post(body).build(), new Callback() {
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
//														FyjpApplication.TOKEN = obj.getString("token");
//													}
//													if (!obj.isNull("phonenumber")) {
//														FyjpApplication.USERNAME = obj.getString("phonenumber");
//													}
//													if (!obj.isNull("username")) {
//														FyjpApplication.OLDUSERNAME = obj.getString("username");
//													}
//													if (!obj.isNull("nickname")) {
//														FyjpApplication.NICKNAME = obj.getString("nickname");
//													}
//													if (!obj.isNull("mail")) {
//														FyjpApplication.MAIL = obj.getString("mail");
//													}
//													if (!obj.isNull("department")) {
//														FyjpApplication.UNIT = obj.getString("department");
//													}
//													if (!obj.isNull("groupid")) {
//														FyjpApplication.GROUPID = obj.getString("groupid");
//													}
//													if (!obj.isNull("points")) {
//														FyjpApplication.POINTS = obj.getString("points");
//													}
//													if (!obj.isNull("photo")) {
//														FyjpApplication.PHOTO = obj.getString("photo");
//													}
//
//													FyjpApplication.saveUserInfo(mContext);
//													startActivity(new Intent(mContext, FyjpMainActivity.class));
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
//					File files = new File(FyjpCONST.SDCARD_PATH);
//					if (!files.exists()) {
//						files.mkdirs();
//					}
//
//					fos = new FileOutputStream(FyjpCONST.PORTRAIT_ADDR);
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
//			Bitmap bitmap = FyjpCommonUtil.getHttpBitmap(imgUrl);
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
