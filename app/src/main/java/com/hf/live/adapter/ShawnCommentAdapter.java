package com.hf.live.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hf.live.R;
import com.hf.live.dto.PhotoDto;
import com.hf.live.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 对视频、图片评论的列表适配器
 */
public class ShawnCommentAdapter extends BaseAdapter{
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<PhotoDto> mArrayList;
	
	private final class ViewHolder{
		CircleImageView ivPortrait;
		TextView tvUserName,tvTime,tvComment;
	}
	
	public ShawnCommentAdapter(Context context, List<PhotoDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.fyjp_adapter_comment, null);
			mHolder = new ViewHolder();
			mHolder.ivPortrait = convertView.findViewById(R.id.ivPortrait);
			mHolder.tvUserName = convertView.findViewById(R.id.tvUserName);
			mHolder.tvTime = convertView.findViewById(R.id.tvTime);
			mHolder.tvComment = convertView.findViewById(R.id.tvComment);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		PhotoDto dto = mArrayList.get(position);
		if (!TextUtils.isEmpty(dto.userName)) {
			mHolder.tvUserName.setText(dto.userName);
		}
		if (!TextUtils.isEmpty(dto.createTime)) {
			mHolder.tvTime.setText(dto.createTime);
		}
		if (!TextUtils.isEmpty(dto.comment)) {
			mHolder.tvComment.setText(dto.comment);
		}

		if (!TextUtils.isEmpty(dto.portraitUrl)) {
			Picasso.get().load(dto.portraitUrl).error(R.drawable.iv_portrait).into(mHolder.ivPortrait);
		}else {
			mHolder.ivPortrait.setImageResource(R.drawable.iv_portrait);
		}

		return convertView;
	}
	
}
