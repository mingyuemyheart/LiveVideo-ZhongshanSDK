package com.hf.live.common;

import android.os.Environment;

import com.hf.live.R;

public class FyjpCONST {

	public static String APPID = "10";

	//广播
	public static String REFRESH_UPLOAD = "refresh_upload";//刷新已上传的图片、视频信息
	public static String REFRESH_NOTUPLOAD = "refresh_notupload";//刷新未上传的图片、视频信息

	//下拉刷新progresBar四种颜色
	public static final int color1 = R.color.refresh_color1;
	public static final int color2 = R.color.refresh_color2;
	public static final int color3 = R.color.refresh_color3;
	public static final int color4 = R.color.refresh_color4;
	
	//通用
	public static String SDCARD_PATH = Environment.getExternalStorageDirectory()+"/FYJP";
	public static String PORTRAIT_ADDR = SDCARD_PATH + "/portrait.png";//头像保存的路径
	public static String OLD_PORTRAIT_ADDR = SDCARD_PATH + "/oldportrait.png";//头像保存的路径
	public static String VIDEO_ADDR = SDCARD_PATH + "/video";//拍摄视频保存的路径
	public static String DOWNLOAD_ADDR = SDCARD_PATH + "/download";//下载视频保存的路径
	public static String THUMBNAIL_ADDR = SDCARD_PATH + "/thumbnail";//缩略图保存的路径
	public static String PICTURE_ADDR = SDCARD_PATH + "/picture/";//拍照保存的路径
	public static String VIDEOTYPE = ".mp4";//mp4格式播放视频要快，比.3gp速度快很多
	public static int TIME = 120;//视频录制时间限定为120秒
	public static int standarH = 608;//当视频高度太高时，给一个定值

}
