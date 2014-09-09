package com.trekprobe.view;

import java.util.List;

import org.json.JSONException;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoRenderer;

import com.trekprobe.connection.VideoStreamsView;
import com.trekprobe.connection.WebRtcClient;
import com.trekprobe.models.StreamSettings;
import com.trekprobe.variables.Variables;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class StreamActivity extends Activity implements WebRtcClient.RTCListener{
	@SuppressWarnings("unused")
	private final static int VIDEO_CALL_SENT = 666;
	private VideoStreamsView vsv;
	private WebRtcClient client;
	private String mSocketAddress;
	private String callerId;
	
	private AlertDialog backMsg = null;
	
	private StreamSettings streamSettings = null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				
		mSocketAddress = "http://" + Variables.ipAddress;
		mSocketAddress += (":"+Variables.port+"/");

		PeerConnectionFactory.initializeAndroidGlobals(this);

		// Camera display view
		Point displaySize = new Point();
		getWindowManager().getDefaultDisplay().getSize(displaySize);
		vsv = new VideoStreamsView(this, displaySize);
		client = new WebRtcClient(this, mSocketAddress, this.getApplicationContext());

		final Intent intent = getIntent();
		final String action = intent.getAction();

		if (Intent.ACTION_VIEW.equals(action)) {
			final List<String> segments = intent.getData().getPathSegments();
			callerId = segments.get(0);
		}
		
		backMsg = makeBackMsg("Do you really want to stop streaming?").create();
		
		new LoadSettingsAndStartCam().execute();

	}
	
	private class LoadSettingsAndStartCam extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Bundle extras = getIntent().getExtras();
		//	streamSettings = new StreamSettings();
			streamSettings = extras.getParcelable("Settings");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			startCam();
		}
	}	
	
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	public void onPause() {
		super.onPause();
		vsv.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		vsv.onResume();
	}

	@Override
	public void onCallReady(String callId) {
		if(callerId != null) {
			try {
				answer(callerId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			call(callId);
		}
	}

	public void answer(String callerId) throws JSONException {
		client.sendMessage(callerId, "init", Variables.droidName, null);
		startCam();
	}

	public void call(String callId) {
		Intent msg = new Intent(Intent.ACTION_SEND);
		msg.putExtra(Intent.EXTRA_TEXT, mSocketAddress + callId);
		msg.setType("text/plain");

		//	startActivityForResult(Intent.createChooser(msg, "Call :"), VIDEO_CALL_SENT);
	}

	public void startDroid(View v){
		startCam();
	}

/*	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == VIDEO_CALL_SENT) {
			startCam();
		}
	}
*/
	
	public void startCam() {
		setContentView(vsv);
		
		String[] resolutions = getResources().getStringArray(R.array.res_array);
		
		String maxRes = resolutions[streamSettings.getMaxResIndex()];
		String minRes = resolutions[streamSettings.getMinResIndex()];
		
		// Camera settings
	//	client.setCamera(Variables.CAMERA_FACING, "480", "640");
		client.setCameraConstraints(Variables.CAMERA_FACING, maxRes, minRes, streamSettings.getMaxFramerate(),
				streamSettings.getMinFramerate(), streamSettings.getMaxAspectRatio(), 
				streamSettings.getMinAspectRatio(), streamSettings.getStreamAudio()); 
		client.start(Variables.droidName, false); // SlaveTrekProbe SecondCameraTrekProbe
	}

	@Override
	public void onStatusChanged(final String newStatus) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), newStatus, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onLocalStream(MediaStream localStream) {
		localStream.videoTracks.get(0).addRenderer(new VideoRenderer(new VideoCallbacks(vsv, 0)));
	}

	@Override
	public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {
		remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(new VideoCallbacks(vsv, endPoint)));
		vsv.shouldDraw[endPoint] = true;
	}

	@Override
	public void onRemoveRemoteStream(MediaStream remoteStream, int endPoint) {
		remoteStream.videoTracks.get(0).dispose();
		vsv.shouldDraw[endPoint] = false;
	}

	// Implementation detail: bridge the VideoRenderer.Callbacks interface to the
	// VideoStreamsView implementation.
	private class VideoCallbacks implements VideoRenderer.Callbacks {
		private final VideoStreamsView view;
		private final int stream;

		public VideoCallbacks(VideoStreamsView view, int stream) {
			this.view = view;
			this.stream = stream;
		}

		@Override
		public void setSize(final int width, final int height) {
			view.queueEvent(new Runnable() {
				public void run() {
					view.setSize(stream, width, height);
				}
			});
		}

		@Override
		public void renderFrame(VideoRenderer.I420Frame frame) {
			view.queueFrame(stream, frame);
		}
	}
	
	@Override
    public void onBackPressed() {
          //  super.onBackPressed();
            backMsg.show();
    }
	
	private AlertDialog.Builder makeBackMsg(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setCancelable(true);
		builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                vsv.onPause();
                finish();
            }
        });
		builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
		return builder;
	}
}
