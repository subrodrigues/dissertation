package com.trekprobe.models;

import android.os.Parcel;
import android.os.Parcelable;

public class StreamSettings implements Parcelable {
	private int maxResIndex = -1;
	private int minResIndex = -1;

	private String maxFramerate = null; // "0" = not defined
	private String minFramerate = null; // "0" = not defined

	private String maxAspectRatio = null; // "0" = not defined
	private String minAspectRatio = null; // "0" = not defined

	private int streamAudio = -1;
	private int streamLocalVideo = -1;
	private int showRemoteFullScreen = -1;

	public StreamSettings(int maxInd, int minInd, String maxFR, String minFR, String maxAR, String minAR,
			int streamA, int streamLV, int showRFS){
		this.setMaxResIndex(maxInd);
		this.setMinResIndex(minInd);

		this.setMaxFramerate(maxFR);
		this.setMinFramerate(minFR);

		this.setMaxAspectRatio(maxAR);
		this.setMinAspectRatio(minAR);

		this.setStreamAudio(streamA);
		this.setStreamLocalVideo(streamLV);
		this.setShowRemoteFullScreen(showRFS);
	}

	public StreamSettings(Parcel in){
		this.maxResIndex = in.readInt();
		this.minResIndex = in.readInt();
		this.maxFramerate = in.readString();
		this.minFramerate = in.readString();
		this.maxAspectRatio = in.readString();
		this.minAspectRatio = in.readString();
		this.streamAudio = in.readInt();
		this.streamLocalVideo = in.readInt();
		this.showRemoteFullScreen = in.readInt();
	}
	
	public StreamSettings() {
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.maxResIndex);
		dest.writeInt( this.minResIndex);
		dest.writeString(this.maxFramerate);
		dest.writeString(this.minFramerate);
		dest.writeString(this.maxAspectRatio);
		dest.writeString(this.minAspectRatio);
		dest.writeInt(this.streamAudio);
		dest.writeInt(this.streamLocalVideo);
		dest.writeInt(this.showRemoteFullScreen);
	}


	public String getMaxFramerate() {
		return maxFramerate;
	}

	public void setMaxFramerate(String maxFrameRate2) {
		this.maxFramerate = maxFrameRate2;
	}

	public String getMinFramerate() {
		return minFramerate;
	}

	public void setMinFramerate(String minFramerate) {
		this.minFramerate = minFramerate;
	}

	public String getMaxAspectRatio() {
		return maxAspectRatio;
	}

	public void setMaxAspectRatio(String maxAspectRatio) {
		this.maxAspectRatio = maxAspectRatio;
	}

	public String getMinAspectRatio() {
		return minAspectRatio;
	}

	public void setMinAspectRatio(String minAspectRatio) {
		this.minAspectRatio = minAspectRatio;
	}

	public int getStreamAudio() {
		return streamAudio;
	}

	public boolean isStreamingAudio() {
		if(streamAudio == 0)
			return false;

		return true;
	}

	public void setStreamAudio(int streamAudio) {
		this.streamAudio = streamAudio;
	}

	public int getStreamLocalVideo() {
		return streamLocalVideo;
	}

	public boolean isStreamingLocalVideo() {
		if(streamLocalVideo == 0)
			return false;

		return true;
	}

	public void setStreamLocalVideo(int streamLocalVideo) {
		this.streamLocalVideo = streamLocalVideo;
	}

	public int getShowRemoteFullScreen() {
		return showRemoteFullScreen;
	}

	public boolean isRemoteFullScreen() {
		if(showRemoteFullScreen == 0)
			return false;

		return true;
	}

	public void setShowRemoteFullScreen(int showRemoteFullScreen) {
		this.showRemoteFullScreen = showRemoteFullScreen;
	}



	public int getMaxResIndex() {
		return maxResIndex;
	}



	public void setMaxResIndex(int maxResIndex) {
		this.maxResIndex = maxResIndex;
	}



	public int getMinResIndex() {
		return minResIndex;
	}



	public void setMinResIndex(int minResIndex) {
		this.minResIndex = minResIndex;
	}



	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<StreamSettings> CREATOR = new Creator<StreamSettings>() {
		public StreamSettings createFromParcel(Parcel source) {
	            return new StreamSettings(source);
	      }
	      public StreamSettings[] newArray(int size) {
	            return new StreamSettings[size];
	      }
	};
}
