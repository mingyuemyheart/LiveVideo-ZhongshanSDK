package com.hf.live.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hf.live.R;
import com.hf.live.common.FyjpApplication;
import com.hf.live.common.FyjpCONST;
import com.hf.live.util.FyjpDataCleanManager;

import java.io.File;

/**
 * 设置
 */
public class FyjpSettingActivity extends FyjpBaseActivity implements OnClickListener{
	
	private Context mContext;
	private TextView tvLocalSave,tvLocalCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fyjp_activity_setting);
		mContext = this;
		initWidget();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.my_setting));
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		LinearLayout llLocalSave = findViewById(R.id.llLocalSave);
		llLocalSave.setOnClickListener(this);
		LinearLayout llLocalCache = findViewById(R.id.llLocalCache);
		llLocalCache.setOnClickListener(this);
		tvLocalSave = findViewById(R.id.tvLocalSave);
		tvLocalCache = findViewById(R.id.tvLocalCache);
		TextView tvLogout = findViewById(R.id.tvLogout);
		tvLogout.setOnClickListener(this);

		try {
			tvLocalSave.setText(FyjpDataCleanManager.getLocalSaveSize(mContext));
			tvLocalCache.setText(FyjpDataCleanManager.getCacheSize(mContext));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 删除对话框
	 * @param message 标题
	 * @param content 内容
	 * @param flag 0删除本地存储，1删除缓存
	 */
	private void dialogDelete(String message, String content, final int flag) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.fyjp_dialog_delete, null);
		TextView tvMessage = view.findViewById(R.id.tvMessage);
		TextView tvContent = view.findViewById(R.id.tvContent);
		LinearLayout llNegative = view.findViewById(R.id.llNegative);
		LinearLayout llPositive = view.findViewById(R.id.llPositive);
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();
		
		tvMessage.setText(message);
		tvContent.setText(content);
		tvContent.setVisibility(View.VISIBLE);
		llNegative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		
		llPositive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (flag == 0) {
					FyjpDataCleanManager.clearLocalSave(mContext);
					try {
						tvLocalSave.setText(FyjpDataCleanManager.getLocalSaveSize(mContext));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					FyjpDataCleanManager.clearCache(mContext);
					try {
						tvLocalCache.setText(FyjpDataCleanManager.getCacheSize(mContext));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				dialog.dismiss();
			}
		});
	}
	
	/**
	 * 退出登录对话框
	 * @param message 标题
	 */
	private void dialogLogout(String message) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.fyjp_dialog_delete, null);
		TextView tvMessage = view.findViewById(R.id.tvMessage);
		LinearLayout llNegative = view.findViewById(R.id.llNegative);
		LinearLayout llPositive = view.findViewById(R.id.llPositive);

		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();

		tvMessage.setText(message);
		llNegative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		llPositive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				//清除sharedPreferance里保存的用户信息
				FyjpApplication.clearUserInfo(mContext);

				//删除本地保存的头像
				File file = new File(FyjpCONST.PORTRAIT_ADDR);
				if (file.exists()) {
					file.delete();
				}

				file = new File(FyjpCONST.OLD_PORTRAIT_ADDR);
				if (file.exists()) {
					file.delete();
				}

//				FyjpApplication.destoryActivity("FyjpMainActivity");
//				FyjpApplication.destoryActivity("FyjpPersonCenterActivity");
//				startActivity(new Intent(mContext, LoginActivity.class));
//				finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.llBack) {
			finish();

		} else if (i == R.id.llLocalSave) {
			dialogDelete(getString(R.string.sure_delete), getString(R.string.local_content), 0);

		} else if (i == R.id.llLocalCache) {
			dialogDelete(getString(R.string.sure_delete), getString(R.string.cache_content), 1);

		} else if (i == R.id.tvLogout) {
			dialogLogout(getString(R.string.sure_logout));

		} else {
		}
	}
	

}
