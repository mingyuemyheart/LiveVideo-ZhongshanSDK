package com.hf.live.fragment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.hf.live.activity.FyjpDisplayPictureActivity;
import com.hf.live.activity.FyjpDisplayVideoActivity;
import com.hf.live.R;
import com.hf.live.adapter.FyjpLocalAdapter;
import com.hf.live.common.FyjpCONST;
import com.hf.live.dto.FyjpPhotoDto;
import com.hf.live.stickygridheaders.StickyGridHeadersGridView;
import com.hf.live.util.FyjpCommonUtil;

/**
 * 我的上传-未上传
 * @author shawn_sun
 */
public class FyjpShawnLocalFragment extends Fragment {
	
	private FyjpLocalAdapter localAdapter;
	private List<FyjpPhotoDto> localList = new ArrayList<>();
	private int localSection = 1;
	private HashMap<String, Integer> localSectionMap = new HashMap<>();
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
	private MyBroadCastReceiver mReceiver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fyjp_fragment_local, null);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initBroadCast();
		initLocalStickyGridView(view);
	}
	
	private void initBroadCast() {
		mReceiver = new MyBroadCastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FyjpCONST.REFRESH_NOTUPLOAD);
		getActivity().registerReceiver(mReceiver, intentFilter);
	}
	
	private class MyBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (TextUtils.equals(intent.getAction(), FyjpCONST.REFRESH_NOTUPLOAD)) {//修改后重新请求刷新数据
				getLocalVideoPic();
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			getActivity().unregisterReceiver(mReceiver);
		}
	}

	/**
	 * 获取本地没有上传视频、图片文件
	 */
	private void getLocalVideoPic() {
		localList.clear();

		//获取本地未上传视频
		File videoFiles = new File(FyjpCONST.VIDEO_ADDR);
		String[] videoArray = videoFiles.list();
		if (videoArray != null && videoArray.length > 0) {
			for (int i = 0; i < videoArray.length; i++) {
				File localFile = videoFiles.listFiles()[i];
				if (localFile.exists()) {
					String videoUrl = localFile.getPath();
					String name = localFile.getName();
					String workTime = name.substring(0, name.length()-4);

					FyjpPhotoDto dto = FyjpCommonUtil.getVideoInfo(getActivity(), workTime);
					if (dto != null) {
						dto.videoUrl = videoUrl;
						localList.add(dto);
					}
				}
			}
		}

		//获取本地未上传图片
		File picFiles = new File(FyjpCONST.PICTURE_ADDR);
		String[] picArray = picFiles.list();
		if (picArray != null && picArray.length > 0) {
			for (int i = 0; i < picArray.length; i++) {
				File localFile = picFiles.listFiles()[i];
				if (localFile.exists()) {
					String imgUrl = localFile.getPath();
					String name = localFile.getName();
					String workTime = name.substring(0, name.length()-4);

					FyjpPhotoDto dto = FyjpCommonUtil.getVideoInfo(getActivity(), workTime);
					if (dto != null) {
						dto.imgUrl = imgUrl;
						localList.add(dto);
					}
				}
			}
		}
		
		for (int i = 0; i < localList.size(); i++) {
			FyjpPhotoDto dto2 = localList.get(i);
			try {
				String date = sdf2.format(sdf3.parse(dto2.workTime));
				if (!localSectionMap.containsKey(date)) {
					dto2.setSection(localSection);
					localSectionMap.put(date, localSection);
					localSection++;
				}else {
					dto2.setSection(localSectionMap.get(date));
				}
				localList.set(i, dto2);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		//按时间排序
		Collections.sort(localList, new Comparator<FyjpPhotoDto>() {
			@Override
			public int compare(FyjpPhotoDto dto1, FyjpPhotoDto dto2) {
				long time1 = 0;
				long time2 = 0;
				try {
					time1 = sdf3.parse(dto1.workTime).getTime();
					time2 = sdf3.parse(dto2.workTime).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return (int) (time2-time1);
			}
		});

		if (localList.size() > 0 && localAdapter != null) {
			localAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 初始化gridview
	 */
	private void initLocalStickyGridView(View view) {
		getLocalVideoPic();
		StickyGridHeadersGridView localGridView = (StickyGridHeadersGridView) view.findViewById(R.id.localGridView);
		localAdapter = new FyjpLocalAdapter(getActivity(), localList);
		localGridView.setAdapter(localAdapter);
		localGridView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FyjpPhotoDto dto = localList.get(arg2);
				Intent intent = null;
				if (dto.getWorkstype().equals("imgs")) {
					List<FyjpPhotoDto> selectList = new ArrayList<>();
					selectList.clear();
					for (int i = 0; i < localList.size(); i++) {
						if (TextUtils.equals(localList.get(i).getWorkstype(), "imgs")) {
							selectList.add(localList.get(i));//把所有数据加载到照片墙list里
						}
					}
				    intent = new Intent(getActivity(), FyjpDisplayPictureActivity.class);
				    intent.putExtra("cityName", "未知位置");
				    intent.putExtra("takeTime", sdf3.format(System.currentTimeMillis()));
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList("selectList", (ArrayList<? extends Parcelable>) selectList);
				    intent.putExtras(bundle);
					startActivity(intent);
				}else {
					intent = new Intent(getActivity(), FyjpDisplayVideoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable("data", dto);
					intent.putExtras(bundle);
					startActivity(intent);

//					List<FyjpPhotoDto> selectList = new ArrayList<>();
//					selectList.clear();
//					selectList.add(dto);
//
//					intent = new Intent(getActivity(), VideoTrimActivity.class);
//					Bundle bundle = new Bundle();
//					bundle.putParcelableArrayList("selectList", (ArrayList<? extends Parcelable>) selectList);
//					intent.putExtras(bundle);
//					startActivity(intent);
				}
			}
		});
	}
	
}
