package com.hf.live.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hf.live.R;
import com.hf.live.dto.UploadVideoDto;

import java.util.List;

/**
 * 天气类型
 * @author shawn_sun
 */
public class ShawnWeatherTypeAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<UploadVideoDto> mArrayList;
	
	private final class ViewHolder{
		ImageView imageView;
		TextView tvType;
	}
	
	public ShawnWeatherTypeAdapter(Context context, List<UploadVideoDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.fyjp_adapter_weather_type, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.tvType = convertView.findViewById(R.id.tvType);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		UploadVideoDto dto = mArrayList.get(position);
		//wt01雪，wt02雨，wt03冰雹，wt04晴，wt05霾，wt06大风，wt07沙尘
		if (TextUtils.equals(dto.weatherType, "wt01")) {
			if (dto.isSelected) {
				mHolder.imageView.setImageResource(R.drawable.iv_snow_selected);
			}else {
				mHolder.imageView.setImageResource(R.drawable.iv_snow_unselected);
			}
		}else if (TextUtils.equals(dto.weatherType, "wt02")) {
			if (dto.isSelected) {
				mHolder.imageView.setImageResource(R.drawable.iv_rain_selected);
			}else {
				mHolder.imageView.setImageResource(R.drawable.iv_rain_unselected);
			}
		}else if (TextUtils.equals(dto.weatherType, "wt03")) {
			if (dto.isSelected) {
				mHolder.imageView.setImageResource(R.drawable.iv_hail_selected);
			}else {
				mHolder.imageView.setImageResource(R.drawable.iv_hail_unselected);
			}
		}else if (TextUtils.equals(dto.weatherType, "wt04")) {
			if (dto.isSelected) {
				mHolder.imageView.setImageResource(R.drawable.iv_sunny_selected);
			}else {
				mHolder.imageView.setImageResource(R.drawable.iv_sunny_unselected);
			}
		}else if (TextUtils.equals(dto.weatherType, "wt05")) {
			if (dto.isSelected) {
				mHolder.imageView.setImageResource(R.drawable.iv_haze_selected);
			}else {
				mHolder.imageView.setImageResource(R.drawable.iv_haze_unselected);
			}
		}else if (TextUtils.equals(dto.weatherType, "wt06")) {
			if (dto.isSelected) {
				mHolder.imageView.setImageResource(R.drawable.iv_wind_selected);
			}else {
				mHolder.imageView.setImageResource(R.drawable.iv_wind_unselected);
			}
		}else if (TextUtils.equals(dto.weatherType, "wt07")) {
			if (dto.isSelected) {
				mHolder.imageView.setImageResource(R.drawable.iv_storm_selected);
			}else {
				mHolder.imageView.setImageResource(R.drawable.iv_storm_unselected);
			}
		}
		
		mHolder.tvType.setText(dto.weatherName);
		if (dto.isSelected) {
			mHolder.tvType.setTextColor(Color.WHITE);
		}else {
			mHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.text_color4));
		}
		
		return convertView;
	}

}
