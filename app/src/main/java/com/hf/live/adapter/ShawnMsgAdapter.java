package com.hf.live.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hf.live.R;
import com.hf.live.dto.PhotoDto;

import net.tsz.afinal.FinalBitmap;

import java.util.List;

/**
 * 我的消息
 */
public class ShawnMsgAdapter extends BaseAdapter{
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<PhotoDto> mArrayList;
	
	private final class ViewHolder{
		ImageView ivPortrait,imageView,ivVideo;
		TextView tvUserName,tvDate,tvContent,tvScore,tvPosition,tvTime,tvTitle;
		RelativeLayout rePortrait;
		LinearLayout llVideo;
	}
	
	public ShawnMsgAdapter(Context context, List<PhotoDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_msg, null);
			mHolder = new ViewHolder();
			mHolder.ivPortrait = convertView.findViewById(R.id.ivPortrait);
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.ivVideo = convertView.findViewById(R.id.ivVideo);
			mHolder.tvUserName = convertView.findViewById(R.id.tvUserName);
			mHolder.tvDate = convertView.findViewById(R.id.tvDate);
			mHolder.tvContent = convertView.findViewById(R.id.tvContent);
			mHolder.tvScore = convertView.findViewById(R.id.tvScore);
			mHolder.tvPosition = convertView.findViewById(R.id.tvPosition);
			mHolder.tvTime = convertView.findViewById(R.id.tvTime);
			mHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
			mHolder.rePortrait = convertView.findViewById(R.id.rePortrait);
			mHolder.llVideo = convertView.findViewById(R.id.llVideo);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		PhotoDto dto = mArrayList.get(position);
		mHolder.tvUserName.setText(dto.getUserName());
		mHolder.tvDate.setText(dto.getCreateTime());
		mHolder.tvContent.setText(dto.getMsgContent());
		if (!TextUtils.isEmpty(dto.getLocation())) {
			mHolder.tvPosition.setText(dto.getLocation());
		}else {
			mHolder.tvPosition.setText(mContext.getString(R.string.no_location));
		}
		mHolder.tvTitle.setText(dto.getTitle());
		mHolder.tvScore.setText("+"+dto.getScore());
		if (dto.getScore().equals("0")) {
			mHolder.tvScore.setVisibility(View.INVISIBLE);
			mHolder.llVideo.setVisibility(View.VISIBLE);
			mHolder.rePortrait.setVisibility(View.VISIBLE);
		}else {
			mHolder.tvScore.setVisibility(View.VISIBLE);
			mHolder.llVideo.setVisibility(View.GONE);
			mHolder.rePortrait.setVisibility(View.GONE);
		}
		
		if (!TextUtils.isEmpty(dto.getWorkTime())) {
			mHolder.tvTime.setText(mContext.getResources().getString(R.string.cell_upload)+": "+dto.getWorkTime());
		}else {
			mHolder.tvTime.setText(mContext.getResources().getString(R.string.cell_upload)+": "+"--");
		}
		
		if (dto.getWorkstype().equals("imgs")) {
			mHolder.ivVideo.setVisibility(View.INVISIBLE);
		}else {
			mHolder.ivVideo.setVisibility(View.VISIBLE);
		}
		
		LayoutParams lp = mHolder.ivPortrait.getLayoutParams();
		int width = lp.width;
		FinalBitmap portraitBitmap = FinalBitmap.create(mContext);
		portraitBitmap.display(mHolder.ivPortrait, dto.getPortraitUrl(), null, width);
		
		
		if (dto.imgUrl != null) {
			FinalBitmap finalBitmap = FinalBitmap.create(mContext);
			finalBitmap.display(mHolder.imageView, dto.imgUrl, null, 0);
		}
		
		return convertView;
	}

}
