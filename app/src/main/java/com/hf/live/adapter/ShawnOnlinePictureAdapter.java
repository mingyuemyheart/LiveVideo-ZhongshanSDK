package com.hf.live.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hf.live.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 在线预览图片
 */
public class ShawnOnlinePictureAdapter extends BaseAdapter{
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<String> mArrayList;
	private int width;
	
	private final class ViewHolder{
		ImageView imageView;
	}
	
	public ShawnOnlinePictureAdapter(Context context, List<String> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.fyjp_adapter_online_picture, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		String imgUrl = mArrayList.get(position);
		if (!TextUtils.isEmpty(imgUrl)) {
			Picasso.get().load(imgUrl).into(mHolder.imageView);
			LayoutParams params = mHolder.imageView.getLayoutParams();
			params.width = width/4;
			params.height = width/4;
			mHolder.imageView.setLayoutParams(params);
		}
		
		return convertView;
	}

}
