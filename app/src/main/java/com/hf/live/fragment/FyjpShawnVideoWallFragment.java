package com.hf.live.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hf.live.R;
import com.hf.live.activity.FyjpOnlinePictureActivity;
import com.hf.live.activity.FyjpOnlineVideoActivity;
import com.hf.live.adapter.FyjpVideoWallAdapter;
import com.hf.live.common.FyjpCONST;
import com.hf.live.dto.FyjpPhotoDto;
import com.hf.live.util.FyjpOkHttpUtil;
import com.hf.live.view.FyjpRefreshLayout;
import com.hf.live.view.FyjpRefreshLayout.OnLoadListener;
import com.hf.live.view.FyjpRefreshLayout.OnRefreshListener;

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
 * 视频墙
 * @author shawn_sun
 */
public class FyjpShawnVideoWallFragment extends Fragment implements OnRefreshListener, OnLoadListener{
	
	private FyjpVideoWallAdapter mAdapter;
	private List<FyjpPhotoDto> dataList = new ArrayList<>();
	private String order = "";//排序条件
	private String search = "";//搜索条件
	private int page = 1;
	private FyjpRefreshLayout refreshLayout = null;//下拉刷新布局

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fyjp_fragment_video_wall, null);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initRefreshLayout(view);
		initListView(view);

		order = getArguments().getString("order");
		refresh();
	}
	
	/**
	 * 初始化下拉刷新布局
	 */
	private void initRefreshLayout(View view) {
		refreshLayout = view.findViewById(R.id.refreshLayout);
		refreshLayout.setColor(FyjpCONST.color1, FyjpCONST.color2, FyjpCONST.color3, FyjpCONST.color4);
		refreshLayout.setMode(FyjpRefreshLayout.Mode.BOTH);
		refreshLayout.setLoadNoFull(false);
		refreshLayout.setOnRefreshListener(this);
		refreshLayout.setOnLoadListener(this);
	}
	
	@Override
	public void onRefresh() {
		refresh();
	}
	
	@Override
	public void onLoad() {
		page += 1;
		OkHttpVideoList();
	}
	
	private void refresh() {
		dataList.clear();
		page = 1;
		OkHttpVideoList();
	}
	
	/**
	 * 初始化listview
	 */
	private void initListView(View view) {
		ListView mListView = view.findViewById(R.id.listView);
		mAdapter = new FyjpVideoWallAdapter(getActivity(), dataList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FyjpPhotoDto dto = dataList.get(arg2);
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
	}

	/**
	 * 获取视频列表
	 */
	private void OkHttpVideoList() {
		final String url = "http://channellive2.tianqi.cn/weather/work/getwork";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("page", page+"");
		builder.add("pagesize", "20");
		builder.add("appid", FyjpCONST.APPID);
		if (!TextUtils.isEmpty(order)) {
			builder.add("order", order);
		}
		if (!TextUtils.isEmpty(search)) {
			builder.add("search", search);
		}
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
															FyjpPhotoDto dto = new FyjpPhotoDto();
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
															if (!obj.isNull("nickname")) {
																dto.nickName = obj.getString("nickname");
															}
															if (!obj.isNull("username")) {
																dto.userName = obj.getString("username");
															}
															if (!obj.isNull("phonenumber")) {
																dto.phoneNumber = obj.getString("phonenumber");
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
															if (!obj.isNull("weather_flag")) {
																dto.weatherFlag = obj.getString("weather_flag");
															}
															if (!obj.isNull("et01")) {
																dto.otherFlag = obj.getString("et01");
															}
															if (!obj.isNull("worksinfo")) {
																JSONObject workObj = new JSONObject(obj.getString("worksinfo"));
																if (!workObj.isNull("thumbnail")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("thumbnail"));
																	if (!imgObj.isNull("url")) {
																		//视频缩略图
																		dto.imgUrl = imgObj.getString("url");
																	}
																}
																if (!workObj.isNull("video")) {
																	JSONObject imgObj = new JSONObject(workObj.getString("video"));
																	if (!imgObj.isNull("url")) {
																		//视频地址
																		dto.videoUrl = imgObj.getString("url");
																	}
																}

																//上传的图片地址，最多9张
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
														String msg = object.getString("msg");
													}
												}
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}

									refreshLayout.setRefreshing(false);
									refreshLayout.setLoading(false);
								}
							});
						}
					}
				});
			}
		}).start();
	}

}
