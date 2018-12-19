package com.hf.live.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hf.live.R;
import com.hf.live.dto.PhotoDto;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 视频墙
 */
public class ShawnVideoWallAdapter extends BaseAdapter{
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<PhotoDto> mArrayList;
	
	private final class ViewHolder{
		ImageView imageView,ivVideo;
		TextView tvAddress,tvTime,tvUserName,tvPraise,tvComment,tvTitle;
	}
	
	public ShawnVideoWallAdapter(Context context, List<PhotoDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.fyjp_adapter_video_wall, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.ivVideo = convertView.findViewById(R.id.ivVideo);
			mHolder.tvAddress = convertView.findViewById(R.id.tvAddress);
			mHolder.tvTime = convertView.findViewById(R.id.tvTime);
			mHolder.tvUserName = convertView.findViewById(R.id.tvUserName);
			mHolder.tvPraise = convertView.findViewById(R.id.tvPraise);
			mHolder.tvComment = convertView.findViewById(R.id.tvComment);
			mHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		PhotoDto dto = mArrayList.get(position);
		if (!TextUtils.isEmpty(dto.getLocation())) {
			mHolder.tvAddress.setText(dto.getLocation());
		}else {
			mHolder.tvAddress.setText(mContext.getString(R.string.no_location));
		}
		
		if (!TextUtils.isEmpty(dto.nickName)) {
			mHolder.tvUserName.setText(dto.nickName);
		}else if (!TextUtils.isEmpty(dto.getUserName())) {
			mHolder.tvUserName.setText(dto.getUserName());
		}else if (!TextUtils.isEmpty(dto.phoneNumber)) {
			if (dto.phoneNumber.length() >= 7) {
				mHolder.tvUserName.setText(dto.phoneNumber.replace(dto.phoneNumber.substring(3, 7), "****"));
			}else {
				mHolder.tvUserName.setText(dto.phoneNumber);
			}
		}
		mHolder.tvPraise.setText(dto.getPraiseCount());
		mHolder.tvComment.setText(dto.getCommentCount());
		mHolder.tvTitle.setText(dto.getTitle());
		
		if (!TextUtils.isEmpty(dto.getWorkTime())) {
			mHolder.tvTime.setText(mContext.getResources().getString(R.string.cell_upload)+": "+dto.getWorkTime());
		}else {
			mHolder.tvTime.setText(mContext.getResources().getString(R.string.cell_upload)+": "+"--");
		}
		
		Picasso.get().load(dto.imgUrl).into(mHolder.imageView);
		
		if (dto.getWorkstype().equals("imgs")) {
			mHolder.ivVideo.setVisibility(View.INVISIBLE);
		}else {
			mHolder.ivVideo.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

}
