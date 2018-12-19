package com.hf.live.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhotoDto implements Parcelable {

	public String uid;//用户id
	public String mail;//邮箱
	public String unit;//单位
	public boolean state = false;// false为没有拍照，true为拍照完成
	public String imgUrl;// 图片或者缩略图路径
	public String videoUrl;// 视频路径
	public String createTime;// 上传视频或图片的时间
	public int section;
	public String location;// 地址信息
	public String workstype;// 区分imgs或者video
	public List<String> urlList = new ArrayList<>();
	public String userName;// 用户名
	public String praiseCount;// 点赞次数
	public String commentCount;// 评论次数
	public String videoId;// 视频id
	public String title;// 标题
	public String content;//上传内容
	public String comment;// 评论内容
	public String msgContent;// 消息内容
	public String workTime;// 录制或者拍照时间
	public String score;// 积分
	public String workId;// 作品id
	public String scoreWhy;// 改变积分原因
	public String portraitUrl;// 头像url
	public int workCount;// 作品数量
	public String status = null;//审核状态，1为未审核，2为通过，3为拒绝
	public String nickName;//昵称
	public String phoneNumber;//手机号
	public String lat;
	public String lng;
	public String pro,city,dis,street;
	public int selectSequnce = 0;//选择视频的顺序，数越大，优先级越高
	public String weatherFlag;//天气标签
	public String otherFlag;//其它标签
	
	//获取本地所有视频文件
	public String fileName;//文件名称
	public String filePath;//文件路径

	//获取本地相册用到
	public String albumName;//相册名字
	public String albumCover;//相册第一张图片url

	//获取本地相册里图片
	public String imageName;//图片名称
	public boolean isSelected = false;
	
	public List<String> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}

	public int getWorkCount() {
		return workCount;
	}

	public void setWorkCount(int workCount) {
		this.workCount = workCount;
	}

	public String getPortraitUrl() {
		return portraitUrl;
	}

	public void setPortraitUrl(String portraitUrl) {
		this.portraitUrl = portraitUrl;
	}

	public String getScoreWhy() {
		return scoreWhy;
	}

	public void setScoreWhy(String scoreWhy) {
		this.scoreWhy = scoreWhy;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPraiseCount() {
		return praiseCount;
	}

	public void setPraiseCount(String praiseCount) {
		this.praiseCount = praiseCount;
	}

	public String getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getSection() {
		return section;
	}

	public void setSection(int section) {
		this.section = section;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public PhotoDto() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.uid);
		dest.writeString(this.mail);
		dest.writeString(this.unit);
		dest.writeByte(this.state ? (byte) 1 : (byte) 0);
		dest.writeString(this.imgUrl);
		dest.writeString(this.videoUrl);
		dest.writeString(this.createTime);
		dest.writeInt(this.section);
		dest.writeString(this.location);
		dest.writeString(this.workstype);
		dest.writeStringList(this.urlList);
		dest.writeString(this.userName);
		dest.writeString(this.praiseCount);
		dest.writeString(this.commentCount);
		dest.writeString(this.videoId);
		dest.writeString(this.title);
		dest.writeString(this.content);
		dest.writeString(this.comment);
		dest.writeString(this.msgContent);
		dest.writeString(this.workTime);
		dest.writeString(this.score);
		dest.writeString(this.workId);
		dest.writeString(this.scoreWhy);
		dest.writeString(this.portraitUrl);
		dest.writeInt(this.workCount);
		dest.writeString(this.status);
		dest.writeString(this.nickName);
		dest.writeString(this.phoneNumber);
		dest.writeString(this.lat);
		dest.writeString(this.lng);
		dest.writeString(this.pro);
		dest.writeString(this.city);
		dest.writeString(this.dis);
		dest.writeString(this.street);
		dest.writeInt(this.selectSequnce);
		dest.writeString(this.weatherFlag);
		dest.writeString(this.otherFlag);
		dest.writeString(this.fileName);
		dest.writeString(this.filePath);
		dest.writeString(this.albumName);
		dest.writeString(this.albumCover);
		dest.writeString(this.imageName);
		dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
	}

	protected PhotoDto(Parcel in) {
		this.uid = in.readString();
		this.mail = in.readString();
		this.unit = in.readString();
		this.state = in.readByte() != 0;
		this.imgUrl = in.readString();
		this.videoUrl = in.readString();
		this.createTime = in.readString();
		this.section = in.readInt();
		this.location = in.readString();
		this.workstype = in.readString();
		this.urlList = in.createStringArrayList();
		this.userName = in.readString();
		this.praiseCount = in.readString();
		this.commentCount = in.readString();
		this.videoId = in.readString();
		this.title = in.readString();
		this.content = in.readString();
		this.comment = in.readString();
		this.msgContent = in.readString();
		this.workTime = in.readString();
		this.score = in.readString();
		this.workId = in.readString();
		this.scoreWhy = in.readString();
		this.portraitUrl = in.readString();
		this.workCount = in.readInt();
		this.status = in.readString();
		this.nickName = in.readString();
		this.phoneNumber = in.readString();
		this.lat = in.readString();
		this.lng = in.readString();
		this.pro = in.readString();
		this.city = in.readString();
		this.dis = in.readString();
		this.street = in.readString();
		this.selectSequnce = in.readInt();
		this.weatherFlag = in.readString();
		this.otherFlag = in.readString();
		this.fileName = in.readString();
		this.filePath = in.readString();
		this.albumName = in.readString();
		this.albumCover = in.readString();
		this.imageName = in.readString();
		this.isSelected = in.readByte() != 0;
	}

	public static final Creator<PhotoDto> CREATOR = new Creator<PhotoDto>() {
		@Override
		public PhotoDto createFromParcel(Parcel source) {
			return new PhotoDto(source);
		}

		@Override
		public PhotoDto[] newArray(int size) {
			return new PhotoDto[size];
		}
	};
}
