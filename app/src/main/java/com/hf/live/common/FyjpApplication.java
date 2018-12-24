package com.hf.live.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FyjpApplication extends Application{
	
	private static Map<String,Activity> destoryMap = new HashMap<>();

	@Override
	public void onCreate() {
		super.onCreate();
	}

	/**
     * 添加到销毁队列
     * @param activity 要销毁的activity
     */
    public static void addDestoryActivity(Activity activity,String activityName) {
        destoryMap.put(activityName,activity);
    }
    
	/**
	*销毁指定Activity
	*/
    public static void destoryActivity(String activityName) {
       Set<String> keySet=destoryMap.keySet();
        for (String key:keySet){
            destoryMap.get(key).finish();
        }
    }

	//本地保存用户信息参数
	public static String OLDUSERNAME = "";//手机号
	public static String USERNAME = "";//手机号
	public static String GROUPID = "";//用户组id
	public static String TOKEN = "";//token
	public static String POINTS = "";//积分
	public static String PHOTO = "";//头像地址
	public static String NICKNAME = "";//昵称
	public static String MAIL = "";//邮箱
	public static String UNIT = "";//单位名称
	public static String USERINFO = "userInfo";//userInfo sharedPreferance名称
	public static class UserInfo {
		public static final String oldUserName = "oldUserName";
		public static final String userName = "uName";
		public static final String groupId = "groupId";
		public static final String token = "token";
		public static final String points = "points";
		public static final String photo = "photo";
		public static final String nickName = "nickName";
		public static final String mail = "mail";
		public static final String unit = "unit";
	}

	/**
	 * 清除用户信息
	 */
	public static void clearUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.apply();
		TOKEN = "";
		OLDUSERNAME = "";
		USERNAME = "";
		NICKNAME = "";
		GROUPID = "";
		POINTS = "";
		PHOTO = "";
		MAIL = "";
		UNIT = "";
	}

	/**
	 * 保存用户信息
	 */
	public static void saveUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(UserInfo.token, TOKEN);
		editor.putString(UserInfo.oldUserName, OLDUSERNAME);
		editor.putString(UserInfo.userName, USERNAME);
		editor.putString(UserInfo.nickName, NICKNAME);
		editor.putString(UserInfo.groupId, GROUPID);
		editor.putString(UserInfo.points, POINTS);
		editor.putString(UserInfo.photo, PHOTO);
		editor.putString(UserInfo.mail, MAIL);
		editor.putString(UserInfo.unit, UNIT);
		editor.apply();
	}

	/**
	 * 获取用户信息
	 */
	public static void getUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		TOKEN = sharedPreferences.getString(UserInfo.token, "");
		OLDUSERNAME = sharedPreferences.getString(UserInfo.oldUserName, "");
		USERNAME = sharedPreferences.getString(UserInfo.userName, "");
		NICKNAME = sharedPreferences.getString(UserInfo.nickName, "");
		GROUPID = sharedPreferences.getString(UserInfo.groupId, "");
		POINTS = sharedPreferences.getString(UserInfo.points, "");
		PHOTO = sharedPreferences.getString(UserInfo.photo, "");
		MAIL = sharedPreferences.getString(UserInfo.mail, "");
		UNIT = sharedPreferences.getString(UserInfo.unit, "");
	}

}
