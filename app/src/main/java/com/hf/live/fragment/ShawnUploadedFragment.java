package com.hf.live.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.hf.live.R;
import com.hf.live.activity.FyjpOnlinePictureActivity;
import com.hf.live.activity.FyjpOnlineVideoActivity;
import com.hf.live.adapter.ShawnUploadedAdapter;
import com.hf.live.common.CONST;
import com.hf.live.common.MyApplication;
import com.hf.live.dto.PhotoDto;
import com.hf.live.stickygridheaders.StickyGridHeadersGridView;
import com.hf.live.util.OkHttpUtil;
import com.hf.live.view.MyDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 我的上传-已上传
 */
public class ShawnUploadedFragment extends Fragment{
	
	private ShawnUploadedAdapter mAdapter;
	private List<PhotoDto> dataList = new ArrayList<>();
	private int section = 1;
	private HashMap<String, Integer> sectionMap = new HashMap<>();
	private MyDialog mDialog;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
	private int page = 1;
	private MyBroadCastReceiver mReceiver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fyjp_fragment_uploaded, null);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		showDialog();
		initBroadCast();
		initStickyGridView(view);
		OKHttpUploaded();
	}
	
	private void initBroadCast() {
		mReceiver = new MyBroadCastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CONST.REFRESH_UPLOAD);
		getActivity().registerReceiver(mReceiver, intentFilter);
	}
	
	private class MyBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (TextUtils.equals(intent.getAction(), CONST.REFRESH_UPLOAD)) {
				dataList.clear();
				page = 1;
				OKHttpUploaded();
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			getActivity().unregisterReceiver(mReceiver);
		}
	}
	
	/**
	 * 初始化dialog
	 */
	private void showDialog() {
		if (mDialog == null) {
			mDialog = new MyDialog(getActivity());
		}
		mDialog.show();
	}
	private void cancelDialog() {
		if (mDialog != null) {
			mDialog.cancel();
		}
	}

	/**
	 * 初始化gridview
	 */
	private void initStickyGridView(View view) {
		StickyGridHeadersGridView mGridView = view.findViewById(R.id.stickyGridView);
		mAdapter = new ShawnUploadedAdapter(getActivity(), dataList);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				PhotoDto dto = dataList.get(arg2);
				Intent intent = new Intent();
				if (dto.getWorkstype().equals("imgs")) {
					intent.setClass(getActivity(), FyjpOnlinePictureActivity.class);
				}else {
					intent.setClass(getActivity(), FyjpOnlineVideoActivity.class);
				}
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", dto);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mGridView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == view.getCount() - 1) {
					page += 1;
					OKHttpUploaded();
				}
			}
			
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}
		});
	}
	
	/**
	 * 获取已上传信息
	 */
	private void OKHttpUploaded() {
		final String url = "http://channellive2.tianqi.cn/weather/work/getmywork";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("token", MyApplication.TOKEN);
		builder.add("page", page+"");
		builder.add("pagesize", "100");
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
						if (isAdded()) {
							getActivity().runOnUiThread(new Runnable() {
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
															if (!obj.isNull("id")) {
																dto.videoId = obj.getString("id");
															}
															if (!obj.isNull("title")) {
																dto.title = obj.getString("title");
															}
															if (!obj.isNull("content")) {
																dto.content = obj.getString("content");
															}
															if (!obj.isNull("create_time")) {
																dto.createTime = obj.getString("create_time");
															}
															if (!obj.isNull("location")) {
																dto.location = obj.getString("location");
															}
															if (!obj.isNull("username")) {
																dto.userName = obj.getString("username");
															}
															if (!obj.isNull("praise")) {
																dto.praiseCount = obj.getString("praise");
															}
															if (!obj.isNull("comments")) {
																dto.commentCount = obj.getString("comments");
															}
															if (!obj.isNull("work_time")) {
																dto.workTime = obj.getString("work_time");
															}
															if (!obj.isNull("workstype")) {
																dto.workstype = obj.getString("workstype");
															}
															if (!obj.isNull("worksinfo")) {
																JSONObject workObj = new JSONObject(obj.getString("worksinfo"));

																if (!workObj.isNull("thumbnail")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("thumbnail"));
																	if (!imgObj.isNull("url")) {
																		dto.imgUrl = imgObj.getString("url");
																	}
																}

																if (!workObj.isNull("video")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("video"));
																	if (!imgObj.isNull("url")) {
																		dto.videoUrl = imgObj.getString("url");
																	}
																}

																List<String> urlList = new ArrayList<>();
																if (!workObj.isNull("imgs1")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("imgs1"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																		dto.imgUrl = imgObj.getString("url");
																	}
																}
																if (!workObj.isNull("imgs2")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("imgs2"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!workObj.isNull("imgs3")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("imgs3"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!workObj.isNull("imgs4")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("imgs4"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!workObj.isNull("imgs5")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("imgs5"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!workObj.isNull("imgs6")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("imgs6"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!workObj.isNull("imgs7")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("imgs7"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!workObj.isNull("imgs8")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("imgs8"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																if (!workObj.isNull("imgs9")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("imgs9"));
																	if (!imgObj.isNull("url")) {
																		urlList.add(imgObj.getString("url"));
																	}
																}
																dto.urlList.addAll(urlList);
															}

															if (!TextUtils.isEmpty(dto.workTime)) {
																dataList.add(dto);
															}
														}
													}

													for (int i = 0; i < dataList.size(); i++) {
														PhotoDto dto2 = dataList.get(i);
														try {
															String date = sdf2.format(sdf1.parse(dto2.getWorkTime()));
															if (!sectionMap.containsKey(date)) {
																dto2.setSection(section);
																sectionMap.put(date, section);
																section++;
															}else {
																dto2.setSection(sectionMap.get(date));
															}
															dataList.set(i, dto2);
														} catch (ParseException e) {
															e.printStackTrace();
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
															Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
					}
				});
			}
		}).start();
	}
	
}
