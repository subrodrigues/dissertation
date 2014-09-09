package com.trekprobe.view;

import com.trekprobe.models.StreamSettings;
import com.trekprobe.variables.Variables;
import com.trekprobe.view.fragments.HomeFragment;
import com.trekprobe.view.fragments.ServerSettingsFragment;
import com.trekprobe.view.fragments.StreamSettingsFragment;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.support.v4.widget.DrawerLayout;
import android.widget.EditText;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {
	
	private StreamSettings streamSettings = null;
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		new LoadSettings().execute();
	}
	
	private void loadSettings() {
		SharedPreferences mPreferences = getSharedPreferences("Current User", MODE_PRIVATE);
		
		if (mPreferences != null) {	
			int maxResIndex = mPreferences.getInt("maxResIndex", 3);
			int minResIndex = mPreferences.getInt("minResIndex", 3);

			String maxFramerate = mPreferences.getString("maxFramerate", "0");
			String minFramerate = mPreferences.getString("minFramerate", "0");

			String maxAspectRatio = mPreferences.getString("maxAspectRatio", "0");
			String minAspectRatio = mPreferences.getString("minAspectRatio", "0");
			
			int streamAudio = mPreferences.getInt("streamAudio", 1);
			int streamLocalVideo = mPreferences.getInt("streamLocalVideo", 1);
			int showRemoteFullScreen = mPreferences.getInt("showRemoteFullScreen", 0);
			
			setStreamSettings(new StreamSettings(maxResIndex, minResIndex, maxFramerate, minFramerate,
					maxAspectRatio, minAspectRatio, streamAudio, streamLocalVideo, showRemoteFullScreen));
		}
	}
	
	private class LoadSettings extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			loadSettings();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO
		}
	}	
	
	private void clearSettingsPreferences() {
		SharedPreferences mPreferences = getSharedPreferences("Current User", MODE_PRIVATE);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.clear();
		editor.commit();
	}
	
	public void onClickApplyStream(View v){
		// TODO: Apply Stream Options		
	//	TextView maxResView = (TextView)((Spinner)findViewById(R.id.max_res_spinner)).getSelectedView();
	//	String maxRes = maxResView.getText().toString();
		
	//	String maxRes = ((Spinner)v.findViewById(R.id.max_res_spinner)).getSelectedItem().toString();
	//	TextView minResView = (TextView)((Spinner)findViewById(R.id.min_res_spinner)).getSelectedView();

	//	String minRes = minResView.getText().toString();
		int maxResInd = ((Spinner)findViewById(R.id.max_res_spinner)).getSelectedItemPosition();
		int minResInd = ((Spinner)findViewById(R.id.min_res_spinner)).getSelectedItemPosition();
		
		String maxFrameRate = ((EditText)findViewById(R.id.max_fr)).getText().toString();
		String minFrameRate = ((EditText)findViewById(R.id.min_fr)).getText().toString();
		
		String maxAspectRatio = ((EditText)findViewById(R.id.max_ar)).getText().toString();
		String minAspectRatio = ((EditText)findViewById(R.id.min_ar)).getText().toString();
		
		Switch audioSW = ((Switch)findViewById(R.id.switch_audio));
		int audio = 1;
		if(!audioSW.isChecked())
			audio = 0;
		
		Switch localStream = ((Switch)findViewById(R.id.switch_local_stream));
		int showLocalStream = 1;
		if(!localStream.isChecked())
			showLocalStream = 0;
		
		Switch remoteInFS = ((Switch)findViewById(R.id.switch_remote_fs));
		int showRemoteFS = 0;
		if(remoteInFS.isChecked())
			showRemoteFS = 1;
		
		// Insert into model
		streamSettings.setMaxResIndex(maxResInd);
		streamSettings.setMinResIndex(minResInd);
		
		getStreamSettings().setMaxFramerate(maxFrameRate);
		getStreamSettings().setMinFramerate(minFrameRate);
		
		getStreamSettings().setMaxAspectRatio(maxAspectRatio);
		getStreamSettings().setMinAspectRatio(minAspectRatio);
		
		getStreamSettings().setStreamAudio(audio);
		getStreamSettings().setStreamLocalVideo(showLocalStream);
		getStreamSettings().setShowRemoteFullScreen(showRemoteFS);
		
		saveSettings();
	}
	
	private void saveSettings() {
		SharedPreferences mPreferences = getSharedPreferences("Current User", MODE_PRIVATE);
		SharedPreferences.Editor editor = mPreferences.edit();
		
		editor.putInt("maxResIndex", getStreamSettings().getMaxResIndex());
		editor.putInt("minResIndex", getStreamSettings().getMinResIndex());
		
		editor.putString("maxFramerate", getStreamSettings().getMaxFramerate());
		editor.putString("minFramerate", getStreamSettings().getMinFramerate());
		
		editor.putString("maxAspectRatio", getStreamSettings().getMaxAspectRatio());
		editor.putString("minAspectRatio", getStreamSettings().getMinAspectRatio());
		
		editor.putInt("streamAudio", getStreamSettings().getStreamAudio());
		editor.putInt("streamLocalVideo", getStreamSettings().getStreamLocalVideo());
		editor.putInt("showRemoteFullScreen", getStreamSettings().getShowRemoteFullScreen());

		editor.commit();
	}
	
	public void onClickStart(View v){
		Intent startCamIntent = new Intent(this, StreamActivity.class);
		startActivityForResult(startCamIntent, 0);
	}
	
	public void onClickApplyServer(View v){
		// Apply Server Options
		Variables.ipAddress = ((EditText)findViewById(R.id.ip_address)).getText().toString();
		Variables.port = ((EditText)findViewById(R.id.port)).getText().toString();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_home);
		case 2:
			mTitle = getString(R.string.title_server_settings);
			break;
		case 3:
			mTitle = getString(R.string.title_stream_settings);
			break;
		case 4:
			mTitle = getString(R.string.title_robot_settings);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public StreamSettings getStreamSettings() {
		return streamSettings;
	}

	public void setStreamSettings(StreamSettings streamSettings) {
		this.streamSettings = streamSettings;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static Fragment newInstance(int sectionNumber) {
			
			switch(sectionNumber){
				case 1:
					HomeFragment homeFragment = new HomeFragment();
					Bundle args = new Bundle();
					args.putInt(ARG_SECTION_NUMBER, sectionNumber);
					homeFragment.setArguments(args);
					return homeFragment;
				case 2:
					ServerSettingsFragment serverFragment = new ServerSettingsFragment();
					Bundle argsServer = new Bundle();
					argsServer.putInt(ARG_SECTION_NUMBER, sectionNumber);
					serverFragment.setArguments(argsServer);
					return serverFragment;
				case 3:
					StreamSettingsFragment streamFragment = new StreamSettingsFragment();
					Bundle argsStream = new Bundle();
					argsStream.putInt(ARG_SECTION_NUMBER, sectionNumber);
					streamFragment.setArguments(argsStream);
					return streamFragment;
				default:
					return null;
			}

		}
		
/*		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
*/
	}

}
