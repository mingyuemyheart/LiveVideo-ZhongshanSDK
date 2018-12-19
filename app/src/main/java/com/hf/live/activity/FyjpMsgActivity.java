package com.hf.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hf.live.R;
import com.hf.live.adapter.ShawnMsgAdapter;
import com.hf.live.common.CONST;
import com.hf.live.common.MyApplication;
import com.hf.live.dto.PhotoDto;
import com.hf.live.util.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 我的消息
 */
public class FyjpMsgActivity extends FyjpBaseActivity implements OnClickListener{
	
	private Context mContext;
	private ShawnMsgAdapter mAdapter;
	private List<PhotoDto> dataList = new ArrayList<>();
	private int page = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fyjp_activity_msg);
		mContext = this;
		showDialog();
		initWidget();
		initListView();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.my_msg));

		OkHttpNews();
	}
	
	/**
	 * 初始化listview
	 */
	private void initListView() {
		ListView mListView = findViewById(R.id.listView);
		mAdapter = new ShawnMsgAdapter(mContext, dataList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				PhotoDto dto = dataList.get(arg2);
				Intent intent = new Intent();
				if (dto.getWorkstype().equals("imgs")) {
					intent.setClass(mContext, FyjpOnlinePictureActivity.class);
				}else {
					intent.setClass(mContext, FyjpOnlineVideoActivity.class);
				}
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", dto);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == view.getCount() - 1) {
					page += 1;
					OkHttpNews();
				}
			}
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}
		});
	}
	
	/**
	 * 获取我的消息
	 */
	private void OkHttpNews() {
		final String url = "http://channellive2.tianqi.cn/weather/message/newmessage";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("token", MyApplication.TOKEN);
		builder.add("page", page+"");
		builder.add("pagesize", "50");
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
												if (!object.isNull("info")) {
													JSONArray array = object.getJSONArray("info");
													for (int i = 0; i < array.length(); i++) {
														JSONObject obj = array.getJSONObject(i);
														PhotoDto dto = new PhotoDto();
														if (!obj.isNull("username")) {
															dto.userName = obj.getString("username");
														}
														if (!obj.isNull("create_time")) {
															dto.createTime = obj.getString("create_time");
														}
														if (!obj.isNull("content")) {
															dto.msgContent = obj.getString("content");
														}
														if (!obj.isNull("points")) {
															dto.score = obj.getString("points");
														}
														if (!obj.isNull("wid")) {
															dto.videoId = obj.getString("wid");
														}
														if (!obj.isNull("location")) {
															dto.location = obj.getString("location");
														}
														if (!obj.isNull("title")) {
															dto.title = obj.getString("title");
														}
														if (!obj.isNull("comments")) {
															dto.commentCount = obj.getString("comments");
														}
														if (!obj.isNull("photo")) {
															dto.portraitUrl = obj.getString("photo");
														}
														if (!obj.isNull("work_time")) {
															dto.workTime = obj.getString("work_time");
														}
														if (!obj.isNull("workstype")) {
															dto.workstype = obj.getString("workstype");
														}
														if (!obj.isNull("praise")) {
															dto.praiseCount = obj.getString("praise");
														}
														if (!obj.isNull("worksinfo")) {
															if (!TextUtils.isEmpty(obj.getString("worksinfo"))) {
																JSONObject itemObj = new JSONObject(obj.getString("worksinfo"));
																if (!itemObj.isNull("thumbnail")) {
																	JSONObject imgObj = new JSONObject(itemObj.getString("thumbnail"));
																	if (!imgObj.isNull("url")) {
																		dto.imgUrl = imgObj.getString("url");
																	}
																}

																if (!itemObj.isNull("video")) {
																	JSONObject imgObj = new JSONObject(itemObj.getString("video"));
																	if (!imgObj.isNull("url")) {
																		dto.videoUrl = imgObj.getString("url");
																	}
																}

																List<String> urlList = new ArrayList<>();
																if (!itemObj.isNull("imgs1")) {
																	JSONObject imgObj = new JSONObject(itemObj.getString("imgs1"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																		dto.imgUrl = imgObj.getString("url");
																	}
																}
																if (!itemObj.isNull("imgs2")) {
																	JSONObject imgObj = new JSONObject(itemObj.getString("imgs2"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!itemObj.isNull("imgs3")) {
																	JSONObject imgObj = new JSONObject(itemObj.getString("imgs3"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!itemObj.isNull("imgs4")) {
																	JSONObject imgObj = new JSONObject(itemObj.getString("imgs4"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!itemObj.isNull("imgs5")) {
																	JSONObject imgObj = new JSONObject(itemObj.getString("imgs5"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!itemObj.isNull("imgs6")) {
																	JSONObject imgObj = new JSONObject(itemObj.getString("imgs6"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!itemObj.isNull("imgs7")) {
																	JSONObject imgObj = new JSONObject(itemObj.getString("imgs7"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!itemObj.isNull("imgs8")) {
																	JSONObject imgObj = new JSONObject(itemObj.getString("imgs8"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!itemObj.isNull("imgs9")) {
																	JSONObject imgObj = new JSONObject(itemObj.getString("imgs9"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																dto.setUrlList(urlList);
															}
														}

														if (!TextUtils.isEmpty(dto.getWorkTime())) {
															dataList.add(dto);
														}
													}
												}

												if (mAdapter != null) {
													mAdapter.notifyDataSetChanged();
												}

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

								cancelDialog();
							}
						});
					}
				});
			}
		}).start();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.llBack) {
			setResult(RESULT_OK);
			finish();

		} else {
		}
	}

}
