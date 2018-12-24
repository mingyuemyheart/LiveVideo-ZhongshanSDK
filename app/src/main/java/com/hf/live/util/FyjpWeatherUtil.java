package com.hf.live.util;

import android.text.TextUtils;

import com.hf.live.R;

public class FyjpWeatherUtil {
	
	private static final String SEPARATER = "\\|";
	private static final String NONE = "\\?";
	
	/**
	 * 获取最后一个值
	 * @param values
	 * @return
	 */
	public static final String lastValue(String values) {
		String temp = values.replaceAll(NONE, "");
		String[] vs = TextUtils.isEmpty(temp) ? null : temp.split(SEPARATER);
		String value = (vs == null || vs.length <= 0) ? null : vs[vs.length - 1];
		return value;
	}
	
	/**
	 * 根据天气现象code获取天气现象字符串
	 * @param code
	 * @return
	 */
	public static final int getWeatherId(int code) {
		int id = 0;
		switch (code) {
		case 0:
			id = R.string.weather0;
			break;
		case 1:
			id = R.string.weather1;
			break;
		case 2:
			id = R.string.weather2;
			break;
		case 3:
			id = R.string.weather3;
			break;
		case 4:
			id = R.string.weather4;
			break;
		case 5:
			id = R.string.weather5;
			break;
		case 6:
			id = R.string.weather6;
			break;
		case 7:
			id = R.string.weather7;
			break;
		case 8:
			id = R.string.weather8;
			break;
		case 9:
			id = R.string.weather9;
			break;
		case 10:
			id = R.string.weather10;
			break;
		case 11:
			id = R.string.weather11;
			break;
		case 12:
			id = R.string.weather12;
			break;
		case 13:
			id = R.string.weather13;
			break;
		case 14:
			id = R.string.weather14;
			break;
		case 15:
			id = R.string.weather15;
			break;
		case 16:
			id = R.string.weather16;
			break;
		case 17:
			id = R.string.weather17;
			break;
		case 18:
			id = R.string.weather18;
			break;
		case 19:
			id = R.string.weather19;
			break;
		case 20:
			id = R.string.weather20;
			break;
		case 21:
			id = R.string.weather21;
			break;
		case 22:
			id = R.string.weather22;
			break;
		case 23:
			id = R.string.weather23;
			break;
		case 24:
			id = R.string.weather24;
			break;
		case 25:
			id = R.string.weather25;
			break;
		case 26:
			id = R.string.weather26;
			break;
		case 27:
			id = R.string.weather27;
			break;
		case 28:
			id = R.string.weather28;
			break;
		case 29:
			id = R.string.weather29;
			break;
		case 30:
			id = R.string.weather30;
			break;
		case 31:
			id = R.string.weather31;
			break;
		case 32:
			id = R.string.weather32;
			break;
		case 33:
			id = R.string.weather33;
			break;
		case 49:
			id = R.string.weather49;
			break;
		case 53:
			id = R.string.weather53;
			break;
		case 54:
			id = R.string.weather54;
			break;
		case 55:
			id = R.string.weather55;
			break;
		case 56:
			id = R.string.weather56;
			break;
		case 57:
			id = R.string.weather57;
			break;
		case 58:
			id = R.string.weather58;
			break;
		default:
			id = R.string.weather0;
			break;
		}
		return id;
	}
	
	/**
	 * 根据风向编号获取风向字符串
	 * @param code
	 * @return
	 */
	public static final int getWindDirection(int code) {
		int id = 0;
		switch (code) {
		case 0:
			id = R.string.wind_dir0;
			break;
		case 1:
			id = R.string.wind_dir1;
			break;
		case 2:
			id = R.string.wind_dir2;
			break;
		case 3:
			id = R.string.wind_dir3;
			break;
		case 4:
			id = R.string.wind_dir4;
			break;
		case 5:
			id = R.string.wind_dir5;
			break;
		case 6:
			id = R.string.wind_dir6;
			break;
		case 7:
			id = R.string.wind_dir7;
			break;
		case 8:
			id = R.string.wind_dir8;
			break;
		case 9:
			id = R.string.wind_dir9;
			break;
		case 10:
			id = R.string.wind_force10;
			break;
		case 11:
			id = R.string.wind_force11;
			break;
		case 12:
			id = R.string.wind_force12;
			break;
		default:
			id = R.string.wind_dir0;
			break;
		}
		return id;
	}

	/**
	 * 根据实况风力编号获取风力等级
	 * @param code
	 * @return
	 */
	public static final String getFactWindForce(int code) {
		String force = "微风";
		switch (code) {
			case 0:
				force = "微风";
				break;
			case 1:
				force = "1级";
				break;
			case 2:
				force = "2级";
				break;
			case 3:
				force = "3级";
				break;
			case 4:
				force = "4级";
				break;
			case 5:
				force = "5级";
				break;
			case 6:
				force = "6级";
				break;
			case 7:
				force = "7级";
				break;
			case 8:
				force = "8级";
				break;
			case 9:
				force = "9级";
				break;
			case 10:
				force = "10级";
				break;
			case 11:
				force = "11级";
				break;
			case 12:
				force = "12级";
				break;

			default:
				break;
		}
		return force;
	}

}
