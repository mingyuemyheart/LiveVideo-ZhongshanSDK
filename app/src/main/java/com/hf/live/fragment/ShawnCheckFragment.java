package com.hf.live.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.hf.live.R;
import com.hf.live.activity.ShawnOnlinePictureActivity;
import com.hf.live.activity.ShawnOnlineVideoActivity;
import com.hf.live.adapter.ShawnCheckAdapter;
import com.hf.live.common.CONST;
import com.hf.live.dto.PhotoDto;
import com.hf.live.swipemenulistview.SwipeMenu;
import com.hf.live.swipemenulistview.SwipeMenuCreator;
import com.hf.live.swipemenulistview.SwipeMenuItem;
import com.hf.live.swipemenulistview.SwipeMenuListView;
import com.hf.live.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.hf.live.util.CommonUtil;
import com.hf.live.util.OkHttpUtil;
import com.hf.live.view.RefreshLayout;
import com.hf.live.view.RefreshLayout.OnLoadListener;
import com.hf.live.view.RefreshLayout.OnRefreshListener;

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
 * 视频审核
 * @author shawn_sun
 */
public class ShawnCheckFragment extends Fragment implements OnRefreshListener, OnLoadListener{
	
	private ShawnCheckAdapter mAdapter = null;
	private List<PhotoDto> dataList = new ArrayList<>();
	private int page = 1;
	private int pageSize = 20;
	private String order = "";
	private RefreshLayout refreshLayout = null;//下拉刷新布局
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.shawn_fragment_check, null);
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
		refreshLayout.setColor(CONST.color1, CONST.color2, CONST.color3, CONST.color4);
		refreshLayout.setMode(RefreshLayout.Mode.BOTH);
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
		SwipeMenuListView mListView = view.findViewById(R.id.listView);
		mAdapter = new ShawnCheckAdapter(getActivity(), dataList);
		mListView.setAdapter(mAdapter);
		
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				switch (menu.getViewType()) {
				case 0:
					createMenu1(menu, getString(R.string.pass_check2), getString(R.string.refuse_check2));
					break;
				case 1:
					createMenu1(menu, getString(R.string.not_check2), getString(R.string.refuse_check2));
					break;
				case 2:
					createMenu1(menu, getString(R.string.not_check2), getString(R.string.pass_check2));
					break;
				}
			}

			private void createMenu1(SwipeMenu menu, String name1, String name2) {
				SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
				item1.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
				item1.setWidth((int) CommonUtil.dip2px(getActivity(), 50));
				item1.setTitle(name1);
				item1.setTitleColor(getResources().getColor(R.color.white));
				item1.setTitleSize(14);
				menu.addMenuItem(item1);
				SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
				item2.setBackground(new ColorDrawable(Color.rgb(0xE5, 0x18, 0x5E)));
				item2.setWidth((int) CommonUtil.dip2px(getActivity(), 50));
				item2.setTitle(name2);
				item2.setTitleColor(getResources().getColor(R.color.white));
				item2.setTitleSize(14);
				menu.addMenuItem(item2);
			}
		};
		mListView.setMenuCreator(creator);
		
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				PhotoDto dto = dataList.get(position);
				switch (index) {
				case 0:
					if (menu.getViewType() == 0) {
						dto.status = "2";
						OkHttpCheck(dto.getVideoId(), "2");
					}else if (menu.getViewType() == 1) {
						dto.status = "1";
						OkHttpCheck(dto.getVideoId(), "1");
					}else if (menu.getViewType() == 2) {
						dto.status = "1";
						OkHttpCheck(dto.getVideoId(), "1");
					}
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
					break;
				case 1:
					if (menu.getViewType() == 0) {
						dto.status = "3";
						OkHttpCheck(dto.getVideoId(), "3");
					}else if (menu.getViewType() == 1) {
						dto.status = "3";
						OkHttpCheck(dto.getVideoId(), "3");
					}else if (menu.getViewType() == 2) {
						dto.status = "2";
						OkHttpCheck(dto.getVideoId(), "2");
					}
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
					break;
				}
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				PhotoDto dto = dataList.get(arg2);
				Intent intent = new Intent();
				if (dto.getWorkstype().equals("imgs")) {
					intent.setClass(getActivity(), ShawnOnlinePictureActivity.class);
				}else {
					intent.setClass(getActivity(), ShawnOnlineVideoActivity.class);
				}
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", dto);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
	}
	
	/**
	 * 视频审核
	 */
	private void OkHttpCheck(final String id, final String status) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final String url = "http://channellive2.tianqi.cn/Weather/work/workCheck";
				FormBody.Builder builder = new FormBody.Builder();
				builder.add("wid", id);
				builder.add("status", status);
				RequestBody body = builder.build();
				OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
					}
				});
			}
		}).start();
	}

	/**
	 * 获取视频列表
	 */
	private void OkHttpVideoList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url;
				if (TextUtils.equals(order, "")) {
					url = String.format("http://channellive2.tianqi.cn/weather/work/getallwork?page=%s&pagesize=%s", page, pageSize);
				}else {
					url = String.format("http://channellive2.tianqi.cn/weather/work/getallwork?page=%s&pagesize=%s&order=&s", page, pageSize, order);
				}
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
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
															if (!obj.isNull("uid")) {
																dto.uid = obj.getString("uid");
															}
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
															if (!obj.isNull("status")) {
																dto.status = obj.getString("status");
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
