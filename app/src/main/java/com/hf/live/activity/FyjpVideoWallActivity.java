package com.hf.live.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hf.live.R;
import com.hf.live.adapter.ShawnVideoWallAdapter;
import com.hf.live.common.CONST;
import com.hf.live.dto.PhotoDto;
import com.hf.live.fragment.ShawnVideoWallFragment;
import com.hf.live.util.CommonUtil;
import com.hf.live.util.OkHttpUtil;
import com.hf.live.view.MainViewPager;

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
public class FyjpVideoWallActivity extends FyjpBaseActivity implements OnClickListener{
	
	private Context mContext;
	private LinearLayout llUploadYes,llUploadNo,llSearch;//已上传
	private TextView tvUploadYes,tvUploadNo,tvCancel;
	private int page = 1;
	private String order = "";//排序条件
	private String search = "";//搜索条件
	private RelativeLayout reTitle;
	private ImageView ivSearch;//搜索按钮
	private EditText etSearch;
	private ListView searchListView;
	private ShawnVideoWallAdapter searchAdapter;
	private List<PhotoDto> searchList = new ArrayList<>();
	private MainViewPager viewPager;
	private List<Fragment> fragments = new ArrayList<>();
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fyjp_activity_video_wall);
		mContext = this;
		initWidget();
		initSearchListView();
		initViewPager();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		ivSearch = findViewById(R.id.ivSearch);
		ivSearch.setOnClickListener(this);
		llUploadYes = findViewById(R.id.llUploadYes);
		llUploadYes.setOnClickListener(new MyOnClickListener(0));
		llUploadNo = findViewById(R.id.llUploadNo);
		llUploadNo.setOnClickListener(new MyOnClickListener(1));
		tvUploadYes = findViewById(R.id.tvUploadYes);
		tvUploadNo = findViewById(R.id.tvUploadNo);
		llSearch = findViewById(R.id.llSearch);
		etSearch = findViewById(R.id.etSearch);
		etSearch.addTextChangedListener(watcher);
		tvCancel = findViewById(R.id.tvCancel);
		tvCancel.setOnClickListener(this);
		reTitle = findViewById(R.id.reTitle);
		progressBar = findViewById(R.id.progressBar);
	}
	
	/**
	 * 搜索框监听器
	 */
	private TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			if (!TextUtils.isEmpty(arg0.toString())) {
				progressBar.setVisibility(View.VISIBLE);
				order = "";
				search = arg0.toString();
				page = 1;
				searchList.clear();
				OkHttpVideoList();
			}else {
				searchList.clear();
				if (searchAdapter != null) {
					searchAdapter.notifyDataSetChanged();
				}
			}
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void afterTextChanged(Editable arg0) {
		}
	};
	
	/**
	 * 相机动画
	 * @param flag false从右到左，true从左到右
	 */
	private void startAnimation(final boolean flag) {
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation animation0;
		if (!flag) {
			ivSearch.setClickable(false);
			animation0 = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,-1.0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f);
		}else {
			ivSearch.setClickable(true);
			animation0 = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF,-1.0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f);
		}
		animation0.setDuration(500);
		animup.addAnimation(animation0);
		animup.setFillAfter(true);
		animup.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				if (viewPager != null) {
					if (!flag) {
						reTitle.setVisibility(View.GONE);
						llSearch.setVisibility(View.VISIBLE);
						viewPager.setVisibility(View.GONE);
						llUploadYes.setVisibility(View.GONE);
						llUploadNo.setVisibility(View.GONE);
						searchListView.setVisibility(View.VISIBLE);
						reTitle.setClickable(false);
					}else {
						reTitle.setVisibility(View.VISIBLE);
						llSearch.setVisibility(View.GONE);
						viewPager.setVisibility(View.VISIBLE);
						llUploadYes.setVisibility(View.VISIBLE);
						llUploadNo.setVisibility(View.VISIBLE);
						searchListView.setVisibility(View.GONE);
						reTitle.setClickable(true);
					}
				}
			}
		});
		reTitle.startAnimation(animup);
		
		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation animation1;
		if (!flag) {
			tvCancel.setClickable(true);
			animation1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF,1.0f,
				Animation.RELATIVE_TO_SELF,0f,
				Animation.RELATIVE_TO_SELF,0f,
				Animation.RELATIVE_TO_SELF,0f);
		}else {
			tvCancel.setClickable(false);
			animation1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF,0f,
				Animation.RELATIVE_TO_SELF,1.0f,
				Animation.RELATIVE_TO_SELF,0f,
				Animation.RELATIVE_TO_SELF,0f);
		}
		animation1.setDuration(500);
		animdn.addAnimation(animation1);
		animdn.setFillAfter(true);
		llSearch.startAnimation(animdn);	
	}
	
	/**
	 * 初始化viewPager
	 */
	private void initViewPager() {
		for (int i = 0; i < 2; i++) {
			Fragment fragment = new ShawnVideoWallFragment();
			Bundle bundle = new Bundle();
			if (i == 0) {
				bundle.putString("order", "");
			}else {
				bundle.putString("order", "praise");
			}
			fragment.setArguments(bundle);
			fragments.add(fragment);
		}
			
		viewPager = findViewById(R.id.viewPager);
		viewPager.setSlipping(true);//设置ViewPager是否可以滑动
		viewPager.setOffscreenPageLimit(fragments.size());
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		viewPager.setAdapter(new MyPagerAdapter());
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				llUploadYes.setBackgroundResource(R.drawable.red_bg);
				llUploadNo.setBackgroundResource(R.drawable.white_bg);
				tvUploadYes.setTextColor(getResources().getColor(R.color.text_color1));
				tvUploadNo.setTextColor(getResources().getColor(R.color.black));
			}else if (arg0 == 1) {
				llUploadYes.setBackgroundResource(R.drawable.white_bg);
				llUploadNo.setBackgroundResource(R.drawable.red_bg);
				tvUploadYes.setTextColor(getResources().getColor(R.color.black));
				tvUploadNo.setTextColor(getResources().getColor(R.color.text_color1));
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * @ClassName: MyOnClickListener
	 * @Description: TODO头标点击监听
	 * @author Panyy
	 * @date 2013 2013年11月6日 下午2:46:08
	 *
	 */
	private class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			if (viewPager != null) {
				viewPager.setCurrentItem(index);
			}
		}
	};

	/**
	 * @ClassName: MyPagerAdapter
	 * @Description: TODO填充ViewPager的数据适配器
	 * @author Panyy
	 * @date 2013 2013年11月6日 下午2:37:47
	 *
	 */
	private class MyPagerAdapter extends PagerAdapter {
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(fragments.get(position).getView());
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = fragments.get(position);
			if (!fragment.isAdded()) { // 如果fragment还没有added
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.add(fragment, fragment.getClass().getSimpleName());
				ft.commit();
				/**
				 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
				 * 会在进程的主线程中,用异步的方式来执行。
				 * 如果想要立即执行这个等待中的操作,就要调用这个方法(只能在主线程中调用)。
				 * 要注意的是,所有的回调和相关的行为都会在这个调用中被执行完成,因此要仔细确认这个方法的调用位置。
				 */
				getFragmentManager().executePendingTransactions();
			}

			if (fragment.getView().getParent() == null) {
				container.addView(fragment.getView()); // 为viewpager增加布局
			}
			return fragment.getView();
		}
	}
	
	/**
	 * 初始化listview
	 */
	private void initSearchListView() {
		searchListView = findViewById(R.id.searchListView);
		searchAdapter = new ShawnVideoWallAdapter(mContext, searchList);
		searchListView.setAdapter(searchAdapter);
		searchListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				PhotoDto dto = searchList.get(arg2);
				Intent intent = new Intent();
				if (dto.workstype.equals("imgs")) {
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
		searchListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == view.getCount() - 1) {
					page += 1;
					OkHttpVideoList();
				}
			}
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
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
		builder.add("appid", CONST.APPID);
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
															searchList.add(dto);
														}
													}
												}

												if (searchAdapter != null) {
													searchAdapter.notifyDataSetChanged();
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

								progressBar.setVisibility(View.GONE);
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

		} else if (i == R.id.ivSearch) {
			startAnimation(false);

		} else if (i == R.id.tvCancel) {
			CommonUtil.hideInputSoft(etSearch, mContext);
			startAnimation(true);

		} else {
		}
	}

}
