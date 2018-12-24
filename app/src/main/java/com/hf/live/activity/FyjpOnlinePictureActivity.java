package com.hf.live.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hf.live.R;
import com.hf.live.adapter.FyjpMyViewPagerAdapter;
import com.hf.live.adapter.FyjpCommentAdapter;
import com.hf.live.adapter.FyjpOnlinePictureAdapter;
import com.hf.live.common.FyjpApplication;
import com.hf.live.common.FyjpCONST;
import com.hf.live.dto.FyjpPhotoDto;
import com.hf.live.util.FyjpCommonUtil;
import com.hf.live.util.FyjpEmojiMapUtil;
import com.hf.live.util.FyjpOkHttpUtil;
import com.hf.live.view.FyjpPhotoView;
import com.squareup.picasso.Picasso;

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
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 在线预览图片
 */
public class FyjpOnlinePictureActivity extends FyjpBaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private GridView mGridView = null;
	private ViewPager mViewPager = null;
	private ImageView[] ivTips = null;//装载点的数组
	private ViewGroup viewGroup = null;
	private RelativeLayout rePager = null;
	private FyjpPhotoDto data = null;
	private List<String> urlList = new ArrayList<>();//存放图片的list
	
	private FyjpCommentAdapter mAdapter = null;
	private List<FyjpPhotoDto> mList = new ArrayList<>();
	private boolean praiseState = false;//点赞状态
	private LinearLayout llListView,llSubmit;
	private TextView tvCommentCount = null;//评论次数
	private EditText etComment = null;
	private ImageView ivPraise = null;//点赞
	private RelativeLayout reOperate = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fyjp_activity_online_picture);
		mContext = this;
		initWidget();
		initGridView();
		initViewPager();
		initListView();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		rePager = findViewById(R.id.rePager);
		viewGroup = findViewById(R.id.viewGroup);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		llListView = findViewById(R.id.llListView);
		TextView tvPosition = findViewById(R.id.tvPosition);
		TextView tvTitle = findViewById(R.id.tvTitle);
		TextView tvTime = findViewById(R.id.tvTime);
		tvCommentCount = findViewById(R.id.tvCommentCount);
		ImageView ivComment = findViewById(R.id.ivComment);
		ivComment.setOnClickListener(this);
		ivPraise = findViewById(R.id.ivPraise);
		ivPraise.setOnClickListener(this);
		llSubmit = findViewById(R.id.llSubmit);
		etComment = findViewById(R.id.etComment);
		TextView tvSubmit = findViewById(R.id.tvSubmit);
		tvSubmit.setOnClickListener(this);
		reOperate = findViewById(R.id.reOperate);
		
		if (getIntent().hasExtra("data")) {
			data = getIntent().getExtras().getParcelable("data");
			if (data != null) {
				urlList.clear();
				urlList = data.getUrlList();
				
				tvPosition.setText(data.getLocation());
				tvTitle.setText(data.getTitle());
				tvTime.setText(data.getWorkTime());
				tvCommentCount.setText(getString(R.string.comment) + "（"+data.getCommentCount()+"）");
				
				//获取点赞状态
				SharedPreferences sharedPreferences = getSharedPreferences(data.getVideoId(), Context.MODE_PRIVATE);
				if (sharedPreferences.getBoolean("praiseState", false)) {
					praiseState = true;
					ivPraise.setImageResource(R.drawable.fyjp_icon_like);
				}else {
					praiseState = false;
					ivPraise.setImageResource(R.drawable.fyjp_icon_unlike);
				}

				//获取评论列表
				OkHttpCommentList();
			}
		}
	}
	
	/**
	 * 初始化gridview
	 */
	private void initGridView() {
		mGridView = findViewById(R.id.gridView);
		FyjpOnlinePictureAdapter gridAdapter = new FyjpOnlinePictureAdapter(mContext, urlList);
		mGridView.setAdapter(gridAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				mGridView.setVisibility(View.GONE);
				llListView.setVisibility(View.GONE);
				reOperate.setVisibility(View.GONE);
				llSubmit.setVisibility(View.GONE);
				rePager.setVisibility(View.VISIBLE);
				if (mViewPager != null) {
					mViewPager.setCurrentItem(arg2);
				}
			}
		});
	}
	
	/**
	 * 初始化viewPager
	 */
	private void initViewPager() {
		ImageView[] imageArray = new ImageView[urlList.size()];
		for (int i = 0; i < urlList.size(); i++) {
			ImageView image = new ImageView(mContext);
			Picasso.get().load(urlList.get(i)).into(image);
			imageArray[i] = image;
		}
		
		ivTips = new ImageView[urlList.size()];
		viewGroup.removeAllViews();
		for (int i = 0; i < urlList.size(); i++) {
			ImageView imageView = new ImageView(mContext);
			imageView.setLayoutParams(new LayoutParams(5, 5));  
			ivTips[i] = imageView;  
			if(i == 0){  
				ivTips[i].setBackgroundResource(R.drawable.fyjp_point_white);
			}else{  
				ivTips[i].setBackgroundResource(R.drawable.fyjp_point_gray);
			}  
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));  
			layoutParams.leftMargin = 10;  
			layoutParams.rightMargin = 10;  
			viewGroup.addView(imageView, layoutParams);  
		}
		
		mViewPager = findViewById(R.id.viewPager);
		FyjpMyViewPagerAdapter pagerAdapter = new FyjpMyViewPagerAdapter(imageArray);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				for (int i = 0; i < urlList.size(); i++) {
					if(i == arg0){  
						ivTips[i].setBackgroundResource(R.drawable.fyjp_point_white);
					}else{  
						ivTips[i].setBackgroundResource(R.drawable.fyjp_point_gray);
					} 
					
					View childAt = mViewPager.getChildAt(i);
                    try {
                        if (childAt != null && childAt instanceof FyjpPhotoView) {
                        	FyjpPhotoView photoView = (FyjpPhotoView) childAt;//得到viewPager里面的页面
                        	PhotoViewAttacher mAttacher = new PhotoViewAttacher(photoView);//把得到的photoView放到这个负责变形的类当中
                            mAttacher.getDisplayMatrix().reset();//得到这个页面的显示状态，然后重置为默认状态
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	
	/**
	 * 初始化listview
	 */
	private void initListView() {
		ListView mListView = findViewById(R.id.listView);
		mAdapter = new FyjpCommentAdapter(mContext, mList);
		mListView.setAdapter(mAdapter);
	}

	/**
	 * 获取评论列表
	 */
	private void OkHttpCommentList() {
		final String url = "http://channellive2.tianqi.cn/weather/comment/getcomment";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("wid", data.getVideoId());
		builder.add("page", "1");
		builder.add("pagesize", "1000");
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
													int length = array.length();
													if (length <= 0) {
														return;
													}
													mList.clear();
													for (int i = 0; i < array.length(); i++) {
														JSONObject obj = array.getJSONObject(i);
														FyjpPhotoDto dto = new FyjpPhotoDto();
														if (!obj.isNull("create_time")) {
															dto.createTime = obj.getString("create_time");
														}
														if (!obj.isNull("username")) {
															dto.userName = obj.getString("username");
														}
														if (!obj.isNull("comment")) {
															dto.comment = FyjpEmojiMapUtil.replaceCheatSheetEmojis(obj.getString("comment"));
														}
														if (!obj.isNull("photo")) {
															dto.portraitUrl = obj.getString("photo");
														}
														mList.add(dto);
													}
												}

												tvCommentCount.setText(getString(R.string.comment) + "（"+mList.size()+"）");
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
							}
						});
					}
				});
			}
		}).start();
	}

	/**
	 * 提交评论
	 */
	private void OkHttpSubmitComment() {
		final String url = "http://channellive2.tianqi.cn/weather/comment/savecomment";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("token", FyjpApplication.TOKEN);
		builder.add("wid", data.videoId);
		builder.add("comment", FyjpEmojiMapUtil.replaceUnicodeEmojis(etComment.getText().toString()));
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
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject object = new JSONObject(result);
										if (!object.isNull("status")) {
											int status  = object.getInt("status");
											if (status == 1) {//成功
												etComment.setText("");
												OkHttpCommentList();
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
	
	/**
	 * 隐藏虚拟键盘
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (etComment != null) {
			FyjpCommonUtil.hideInputSoft(etComment, mContext);
		}
		if (llSubmit != null) {
			llSubmit.setVisibility(View.GONE);
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 点赞
	 */
	private void OkHttpPraise() {
		final String url = "http://channellive2.tianqi.cn/weather/work/praise";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("token", FyjpApplication.TOKEN);
		builder.add("id", data.videoId);
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
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject object = new JSONObject(result);
										if (!object.isNull("status")) {
											int status  = object.getInt("status");
											if (status == 1) {//成功
												//保存点赞状态
												SharedPreferences sharedPreferences = getSharedPreferences(data.videoId, Context.MODE_PRIVATE);
												Editor editor = sharedPreferences.edit();
												editor.putBoolean("praiseState", true);
												editor.apply();
												ivPraise.setImageResource(R.drawable.fyjp_icon_like);
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

	private void commentAnimation(boolean flag, final LinearLayout llLayout) {
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation animation;
		if (!flag) {
			animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 1f,
					Animation.RELATIVE_TO_SELF, 0);
		}else {
			animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,1.0f);
		}
		animation.setDuration(200);
		animationSet.addAnimation(animation);
		animationSet.setFillAfter(true);
		llLayout.startAnimation(animationSet);
		animationSet.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				llLayout.clearAnimation();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mGridView.getVisibility() == View.GONE || llSubmit.getVisibility() == View.VISIBLE) {
			if (mGridView.getVisibility() == View.GONE) {
				mGridView.setVisibility(View.VISIBLE);
				llListView.setVisibility(View.VISIBLE);
				reOperate.setVisibility(View.VISIBLE);
				rePager.setVisibility(View.GONE);
			}
			if (llSubmit.getVisibility() == View.VISIBLE) {
				commentAnimation(true, llSubmit);
				llSubmit.setVisibility(View.GONE);
			}
			return false;
		}else {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.llBack) {
			if (mGridView.getVisibility() == View.GONE || llSubmit.getVisibility() == View.VISIBLE) {
				if (mGridView.getVisibility() == View.GONE) {
					mGridView.setVisibility(View.VISIBLE);
					llListView.setVisibility(View.VISIBLE);
					reOperate.setVisibility(View.VISIBLE);
					rePager.setVisibility(View.GONE);
				}
				if (llSubmit.getVisibility() == View.VISIBLE) {
					commentAnimation(true, llSubmit);
					llSubmit.setVisibility(View.GONE);
				}
			} else {
				finish();
			}

		} else if (i == R.id.tvSubmit) {
			if (!TextUtils.isEmpty(etComment.getText().toString())) {
				FyjpCommonUtil.hideInputSoft(etComment, mContext);
				OkHttpSubmitComment();
			}

		} else if (i == R.id.ivComment) {
			if (FyjpApplication.TOKEN != null) {
				if (llSubmit.getVisibility() == View.GONE) {
					commentAnimation(false, llSubmit);
					llSubmit.setVisibility(View.VISIBLE);
				} else {
					commentAnimation(true, llSubmit);
					llSubmit.setVisibility(View.GONE);
				}
			}

		} else if (i == R.id.ivPraise) {
			if (praiseState) {
				return;
			} else {
				OkHttpPraise();
			}

		} else {
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				llSubmit.setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}
		}
	}
	
}
