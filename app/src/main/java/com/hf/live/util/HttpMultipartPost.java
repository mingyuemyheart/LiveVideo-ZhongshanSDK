package com.hf.live.util;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.hf.live.R;
import com.hf.live.common.CONST;
import com.hf.live.util.CustomMultipartEntity.ProgressListener;
import com.hf.live.view.UploadDialog;

public class HttpMultipartPost extends AsyncTask<String, Integer, String> {

	private Context context = null;
	private UploadDialog mDialog = null;
	private String serverResponse = null;
	private double maxLength = 150*1024*1024;//文件最大长度（150M）
	private AsynLoadCompleteListener completeListener;
	private long totalSize = 0;
	private String url = null;//接口地址
	private String token = null;
	private String workstype = null;
	private String location = null;
	private String title = null;
	private String content = null;
	private String workTime = null;
	private File videoFile = null;
	private File thumbnailFile = null;
	private String weather_flag = null;
	private String other_flags = null;
	private String latlon = null;
	
	public interface AsynLoadCompleteListener {
		public void loadComplete(String result);
	}

	public HttpMultipartPost(Context context, String url, AsynLoadCompleteListener completeListener) {
		this.context = context;
		this.url = url;
		this.completeListener = completeListener;
	}

	@Override
	protected void onPreExecute() {
		showDialog();
	}
	
	private void showDialog() {
		if (mDialog == null) {
			mDialog = new UploadDialog(context);
			mDialog.setCanceledOnTouchOutside(false);
		}
		mDialog.show();
	}
	
	private void cancelDialog() {
		if (mDialog != null) {
			mDialog.cancel();
		}
	}

	@Override
	protected String doInBackground(String... params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);

		try {
			CustomMultipartEntity multipartContent = new CustomMultipartEntity(new ProgressListener() {
						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});

			multipartContent.addPart("appid", new StringBody(CONST.APPID, Charset.forName("UTF-8")));
			multipartContent.addPart("token", new StringBody(getToken(), Charset.forName("UTF-8")));
			multipartContent.addPart("workstype", new StringBody(getWorkstype(), Charset.forName("UTF-8")));
			multipartContent.addPart("location", new StringBody(getLocation(), Charset.forName("UTF-8")));
			multipartContent.addPart("title", new StringBody(getTitle(),Charset.forName("UTF-8")));
			multipartContent.addPart("work_time", new StringBody(getWorkTime(), Charset.forName("UTF-8")));
			multipartContent.addPart("weather_flag", new StringBody(getWeather_flag(), Charset.forName("UTF-8")));
			if (!TextUtils.isEmpty(getOther_flags())) {
				multipartContent.addPart("other_flags", new StringBody(getOther_flags(), Charset.forName("UTF-8")));
			}
			if (!TextUtils.isEmpty(getContent())) {
				multipartContent.addPart("content", new StringBody(getContent(), Charset.forName("UTF-8")));
			}
			multipartContent.addPart("latlon", new StringBody(getLatlon(), Charset.forName("UTF-8")));
			multipartContent.addPart("video", new FileBody(getVideoFile()));
			multipartContent.addPart("thumbnail", new FileBody(getThumbnailFile()));
			
			totalSize = multipartContent.getContentLength();
			if (totalSize > maxLength) {
				cancelDialog();
				serverResponse = context.getResources().getString(R.string.file_too_big);
			}else {
				// 开始上传
				httpPost.setEntity(multipartContent);
				HttpResponse response = httpClient.execute(httpPost, httpContext);
				serverResponse = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return serverResponse;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (mDialog != null) {
			mDialog.setPercent(progress[0]);
		}
	}

	@Override
	protected void onPostExecute(String result) {
		if (completeListener != null) {
			completeListener.loadComplete(result);
        }
		cancelDialog();
	}

	@Override
	protected void onCancelled() {
		System.out.println("cancle");
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getWorkstype() {
		return workstype;
	}

	public void setWorkstype(String workstype) {
		this.workstype = workstype;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}

	public File getVideoFile() {
		return videoFile;
	}

	public void setVideoFile(File videoFile) {
		this.videoFile = videoFile;
	}

	public File getThumbnailFile() {
		return thumbnailFile;
	}

	public void setThumbnailFile(File thumbnailFile) {
		this.thumbnailFile = thumbnailFile;
	}

	public String getWeather_flag() {
		return weather_flag;
	}

	public void setWeather_flag(String weather_flag) {
		this.weather_flag = weather_flag;
	}

	public String getOther_flags() {
		return other_flags;
	}

	public void setOther_flags(String other_flags) {
		this.other_flags = other_flags;
	}

	public String getLatlon() {
		return latlon;
	}

	public void setLatlon(String latlon) {
		this.latlon = latlon;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
