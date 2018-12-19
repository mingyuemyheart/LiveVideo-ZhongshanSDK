package com.hf.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hf.live.R;
import com.hf.live.common.CONST;
import com.hf.live.common.MyApplication;
import com.hf.live.util.OkHttpUtil;
import com.hf.live.view.CircleImageView;
import com.squareup.picasso.Picasso;

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
 * 个人中心
 */
public class ShawnPersonCenterActivity extends ShawnBaseActivity implements OnClickListener{
	
	private Context mContext;
	private CircleImageView ivPortrait;//头像
	private TextView tvUserName,tvNewsCount;
	private LinearLayout llCheck;//视频审核
	private RelativeLayout reNewsCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_person_center);
		MyApplication.addDestoryActivity(ShawnPersonCenterActivity.this, "ShawnPersonCenterActivity");
		mContext = this;
		initWidget();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.my));
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		ivPortrait = findViewById(R.id.ivPortrait);
		ivPortrait.setOnClickListener(this);
		tvUserName = findViewById(R.id.tvUserName);
		LinearLayout llMyVideo = findViewById(R.id.llMyVideo);
		llMyVideo.setOnClickListener(this);
		LinearLayout llMyMsg = findViewById(R.id.llMyMsg);
		llMyMsg.setOnClickListener(this);
		llCheck = findViewById(R.id.llCheck);
		llCheck.setOnClickListener(this);
		LinearLayout llMySetting = findViewById(R.id.llMySetting);
		llMySetting.setOnClickListener(this);
		reNewsCount = findViewById(R.id.reNewsCount);
		tvNewsCount = findViewById(R.id.tvNewsCount);
		LinearLayout llResponse = findViewById(R.id.llResponse);
		llResponse.setOnClickListener(this);
		LinearLayout llMyAbout = findViewById(R.id.llMyAbout);
		llMyAbout.setOnClickListener(this);

		refreshUserInfo();
	}
	
	private void refreshUserInfo() {
		if (!TextUtils.isEmpty(MyApplication.PHOTO)) {
			Picasso.get().load(MyApplication.PHOTO).into(ivPortrait);
		}
		if (!TextUtils.isEmpty(MyApplication.OLDUSERNAME)) {
			tvUserName.setText(MyApplication.OLDUSERNAME);
		}
		if (TextUtils.equals(MyApplication.GROUPID, "100")) {
			llCheck.setVisibility(View.VISIBLE);
		}else {
			llCheck.setVisibility(View.GONE);
		}

		//获取我的消息条数
		OkHttpNewsCount();
	}
	
	/**
	 * 获取我的消息条数
	 */
	private void OkHttpNewsCount() {
		final String url = "http://channellive2.tianqi.cn/weather/message/newcount";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("token", MyApplication.TOKEN);
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
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject object = new JSONObject(result);
										if (!object.isNull("status")) {
											int status  = object.getInt("status");
											if (status == 1) {//成功
												if (!object.isNull("count")) {
													String count = object.getString("count");
													if (Integer.valueOf(count) > 99) {
														tvNewsCount.setText("99+");
													}else {
														tvNewsCount.setText(count);
													}
													if (count.equals("0")) {
														reNewsCount.setVisibility(View.GONE);
													}else {
														reNewsCount.setVisibility(View.VISIBLE);
													}
												}
											}else {
												//失败
												if (!object.isNull("msg")) {
													String msg = object.getString("msg");
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
	
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.llBack) {
			finish();

		} else if (i == R.id.ivPortrait) {
			startActivityForResult(new Intent(mContext, ShawnPersonInfoActivity.class), 0);

		} else if (i == R.id.llMyVideo) {
			startActivity(new Intent(mContext, ShawnUploadedActivity.class));

		} else if (i == R.id.llMyMsg) {
			startActivityForResult(new Intent(mContext, ShawnMsgActivity.class), 1);

		} else if (i == R.id.llCheck) {
			startActivity(new Intent(mContext, ShawnCheckActivity.class));

		} else if (i == R.id.llMySetting) {
			startActivity(new Intent(mContext, ShawnSettingActivity.class));

		} else if (i == R.id.llMyAbout) {
			startActivity(new Intent(mContext, ShawnAboutActivity.class));

		} else if (i == R.id.llResponse) {
			Intent intent = new Intent(mContext, ShawnResponseActivity.class);
			intent.putExtra("activityName", "免责声明");
			intent.putExtra("dataUrl", "file:///android_asset/response.html");
			startActivity(intent);

		} else {
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
			case 1:
				refreshUserInfo();
				break;

			default:
				break;
			}
		}
	}
}
