package com.hf.live.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hf.live.R;
import com.hf.live.common.FyjpApplication;
import com.hf.live.common.FyjpCONST;
import com.hf.live.util.FyjpAuthorityUtil;
import com.hf.live.util.FyjpOkHttpUtil;
import com.hf.live.view.FyjpCircleImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 个人信息
 */
public class FyjpPersonInfoActivity extends FyjpBaseActivity implements OnClickListener{
	
	private Context mContext;
	private FyjpCircleImageView ivPortrait;
	private TextView tvNickName,tvMail,tvUnit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fyjp_activity_person_info);
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
		tvTitle.setText("个人信息");
		TextView tvScore = findViewById(R.id.tvScore);
		TextView tvPhone = findViewById(R.id.tvPhone);
		LinearLayout llPortrait = findViewById(R.id.llPortrait);
		llPortrait.setOnClickListener(this);
		ivPortrait = findViewById(R.id.ivPortrait);
		LinearLayout llNickName = findViewById(R.id.llNickName);
		llNickName.setOnClickListener(this);
		tvNickName = findViewById(R.id.tvNickName);
		LinearLayout llMail = findViewById(R.id.llMail);
		llMail.setOnClickListener(this);
		tvMail = findViewById(R.id.tvMail);
		LinearLayout llUnit = findViewById(R.id.llUnit);
		llUnit.setOnClickListener(this);
		tvUnit = findViewById(R.id.tvUnit);

		if (!TextUtils.isEmpty(FyjpApplication.PHOTO)) {
			Picasso.get().load(FyjpApplication.PHOTO).into(ivPortrait);
		}
		if (!TextUtils.isEmpty(FyjpApplication.NICKNAME)) {
			tvNickName.setText(FyjpApplication.NICKNAME);
		}
		if (!TextUtils.isEmpty(FyjpApplication.OLDUSERNAME)) {
			tvPhone.setText(FyjpApplication.OLDUSERNAME);
		}
		if (!TextUtils.isEmpty(FyjpApplication.MAIL)) {
			tvMail.setText(FyjpApplication.MAIL);
		}
		if (!TextUtils.isEmpty(FyjpApplication.POINTS)) {
			tvScore.setText(FyjpApplication.POINTS);
		}
		if (!TextUtils.isEmpty(FyjpApplication.UNIT)) {
			tvUnit.setText(FyjpApplication.UNIT);
		}
	}
	
	/**
	 * 获取相册
	 */
	private void getAlbum() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.putExtra("crop", "false");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
        startActivityForResult(intent, 0);
	}

	/**
	 * 上传头像
	 */
	private void OkHttpPostPortrait() {
		final String url = "http://channellive2.tianqi.cn/weather/user/update";
		File file = new File(FyjpCONST.PORTRAIT_ADDR);
		if (!file.exists()) {
			return;
		}
		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MultipartBody.FORM);
		builder.addFormDataPart("token", FyjpApplication.TOKEN);
		builder.addFormDataPart("photo", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
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
						String result = response.body().string();
						Log.e("result", result);
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

		} else if (i == R.id.llPortrait) {
			checkAuthority();

		} else if (i == R.id.llNickName) {
			Intent intent = new Intent(mContext, FyjpModifyInfoActivity.class);
			intent.putExtra("title", "昵称");
			intent.putExtra("content", FyjpApplication.NICKNAME);
			startActivityForResult(intent, 1);

		} else if (i == R.id.llMail) {
			Intent intent;
			intent = new Intent(mContext, FyjpModifyInfoActivity.class);
			intent.putExtra("title", "邮箱");
			intent.putExtra("content", FyjpApplication.MAIL);
			startActivityForResult(intent, 2);

		} else if (i == R.id.llUnit) {
			Intent intent;
			intent = new Intent(mContext, FyjpModifyInfoActivity.class);
			intent.putExtra("title", "单位名称");
			intent.putExtra("content", FyjpApplication.UNIT);
			startActivityForResult(intent, 3);

		} else {
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				if (data == null) {
					return;
				}
				Bitmap bitmap = null;
				Uri uri = data.getData();
				if (uri == null) {
					bitmap = data.getParcelableExtra("data");
				}else {
					try {
						ContentResolver resolver = getContentResolver();
						bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
				}
				
				try {
					File files = new File(FyjpCONST.SDCARD_PATH);
					if (!files.exists()) {
						files.mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(FyjpCONST.PORTRAIT_ADDR);
					if (bitmap != null) {
						bitmap.compress(CompressFormat.PNG, 10, fos);
						ivPortrait.setImageBitmap(bitmap);
						OkHttpPostPortrait();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				break;
			case 1:
				if (!TextUtils.isEmpty(FyjpApplication.NICKNAME)) {
					tvNickName.setText(FyjpApplication.NICKNAME);
				}
				break;
			case 2:
				if (!TextUtils.isEmpty(FyjpApplication.MAIL)) {
					tvMail.setText(FyjpApplication.MAIL);
				}
				break;
			case 3:
				if (!TextUtils.isEmpty(FyjpApplication.UNIT)) {
					tvUnit.setText(FyjpApplication.UNIT);
				}
				break;

			default:
				break;
			}
		}
	}

    //需要申请的所有权限
    private String[] allPermissions = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    //拒绝的权限集合
    public static List<String> deniedList = new ArrayList<>();
    /**
     * 申请定位权限
     */
    private void checkAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            getAlbum();
        }else {
            deniedList.clear();
            for (String permission : allPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permission);
                }
            }
            if (deniedList.isEmpty()) {//所有权限都授予
                getAlbum();
            }else {
                String[] permissions = deniedList.toArray(new String[deniedList.size()]);//将list转成数组
                ActivityCompat.requestPermissions(this, permissions, FyjpAuthorityUtil.AUTHOR_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case FyjpAuthorityUtil.AUTHOR_STORAGE:
                if (grantResults.length > 0) {
                    boolean isAllGranted = true;//是否全部授权
                    for (int gResult : grantResults) {
                        if (gResult != PackageManager.PERMISSION_GRANTED) {
                            isAllGranted = false;
                            break;
                        }
                    }
                    if (isAllGranted) {//所有权限都授予
                        getAlbum();
                    }else {//只要有一个没有授权，就提示进入设置界面设置
                        checkAuthority();
                    }
                }else {
                    for (String permission : permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                            checkAuthority();
                            break;
                        }
                    }
                }
                break;
        }
    }
	
}
