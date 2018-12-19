package com.hf.live.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hf.live.R;
import com.hf.live.dto.PhotoDto;

import java.util.List;

/**
 * 视频录制
 */
public class ShawnCameraAdapter extends BaseAdapter{
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<PhotoDto> mArrayList;
	
	private final class ViewHolder{
		ImageView imageView;
	}
	
	public ShawnCameraAdapter(Context context, List<PhotoDto> mArrayList) {
		mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = mInflater.inflate(R.layout.fyjp_adapter_camera, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		PhotoDto dto = mArrayList.get(position);
		if (dto.isState()) {
			mHolder.imageView.setImageResource(R.drawable.iv_select);
		}else {
			mHolder.imageView.setImageResource(R.drawable.iv_unselect);
		}
		
		return convertView;
	}

}
