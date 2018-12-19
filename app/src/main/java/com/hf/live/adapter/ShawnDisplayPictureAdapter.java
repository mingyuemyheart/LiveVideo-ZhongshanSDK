package com.hf.live.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hf.live.R;
import com.hf.live.dto.PhotoDto;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * 图片预览、上传
 */
public class ShawnDisplayPictureAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<PhotoDto> mArrayList;
	private int width;
	
	private final class ViewHolder{
		ImageView imageView;
		ImageView imageView1;
	}
	
	public ShawnDisplayPictureAdapter(Context context, List<PhotoDto> mArrayList) {
		mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fyjp_adapter_display_picture, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.imageView1 = convertView.findViewById(R.id.imageView1);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		PhotoDto dto = mArrayList.get(position);
		if (!TextUtils.isEmpty(dto.imgUrl)) {
			File file = new File(dto.imgUrl);
			if (file.exists()) {
				mHolder.imageView.setImageBitmap(BitmapFactory.decodeFile(dto.imgUrl));
			}else {
				Picasso.get().load(dto.imgUrl).into(mHolder.imageView);
			}
			LayoutParams params = mHolder.imageView.getLayoutParams();
			params.width = width/4;
			params.height = width/4;
			mHolder.imageView.setLayoutParams(params);
		}
		
		if (dto.isSelected) {
			mHolder.imageView1.setVisibility(View.VISIBLE);
		}else {
			mHolder.imageView1.setVisibility(View.GONE);
		}
		
		return convertView;
	}

}
