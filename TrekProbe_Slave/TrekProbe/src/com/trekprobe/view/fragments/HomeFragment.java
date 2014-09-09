package com.trekprobe.view.fragments;

import com.trekprobe.variables.Variables;
import com.trekprobe.view.R;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class HomeFragment extends Fragment {
	
	public HomeFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		
		Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(),"fonts/automati.ttf");
		TextView welcome = (TextView)rootView.findViewById(R.id.welcome);
		Switch backCamera = (Switch)rootView.findViewById(R.id.switch_back_camera);
		Button start = (Button)rootView.findViewById(R.id.start_cam);
		welcome.setTypeface(typeFace);
		start.setTypeface(typeFace);
		backCamera.setTypeface(typeFace);

		backCamera.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			    if(isChecked) {
			    	Variables.CAMERA_FACING = "back";
			    } else {
			    	Variables.CAMERA_FACING = "front";
			    }
		    }
		});
		
		return rootView;
	} 
}
