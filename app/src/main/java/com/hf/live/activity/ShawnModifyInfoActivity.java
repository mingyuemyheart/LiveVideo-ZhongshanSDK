package com.hf.live.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hf.live.R;
import com.hf.live.common.MyApplication;
import com.hf.live.util.OkHttpUtil;

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
 * 修改用户信息
 * @author shawn_sun
 */
public class ShawnModifyInfoActivity extends ShawnBaseActivity implements OnClickListener{
	
	private Context mContext;
	private EditText etContent;
	private ImageView ivClear;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_modify_info);
		mContext = this;
		initWidget();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		etContent = findViewById(R.id.etContent);
		etContent.addTextChangedListener(watcher);
		TextView tvControl = findViewById(R.id.tvControl);
		tvControl.setOnClickListener(this);
		tvControl.setVisibility(View.VISIBLE);
		tvControl.setTextColor(0xff960c1b);
		tvControl.setText("保存");
		ivClear = findViewById(R.id.ivClear);
		ivClear.setOnClickListener(this);
		
		if (getIntent().hasExtra("title")) {
			title = getIntent().getStringExtra("title");
			if (title != null) {
				tvTitle.setText(title);
			}
		}
		
		if (getIntent().hasExtra("content")) {
			String content = getIntent().getStringExtra("content");
			if (content != null) {
				etContent.setText(content);
				etContent.setSelection(content.length());
			}
		}
	}
	
	/**
	 * 监听etcontent内容变化
	 */
	private TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void afterTextChanged(Editable arg0) {
			if (!TextUtils.isEmpty(etContent.getText().toString())) {
				ivClear.setVisibility(View.VISIBLE);
			}else {
				ivClear.setVisibility(View.GONE);
			}
		}
	};
	
	/**
	 * 修改个人信息
	 */
	private void OkHttpModify() {
		final String url = "http://channellive2.tianqi.cn/weather/user/update";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("token", MyApplication.TOKEN);
		if (TextUtils.equals(title, "昵称")) {
			builder.add("nickname", etContent.getText().toString().trim());
		}else if (TextUtils.equals(title, "邮箱")) {
			builder.add("mail", etContent.getText().toString().trim());
		}else if (TextUtils.equals(title, "单位名称")) {
			builder.add("department", etContent.getText().toString().trim());
		}
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
											int status = object.getInt("status");
											if (status == 1) {//成功
												if (!object.isNull("info")) {
													JSONObject obj = new JSONObject(object.getString("info"));
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

													MyApplication.saveUserInfo(mContext);
													setResult(RESULT_OK);
													finish();

												}
											}else {
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
	
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.llBack) {
			finish();

		} else if (i == R.id.ivClear) {
			etContent.setText("");

		} else if (i == R.id.tvControl) {
			if (!TextUtils.isEmpty(etContent.getText().toString().trim())) {
				OkHttpModify();
			} else {
				if (title != null) {
					Toast.makeText(mContext, "请输入" + title, Toast.LENGTH_SHORT).show();
				}
			}

		} else {
		}
	}
	
}
