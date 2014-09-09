package com.trekprobe.view.fragments;

import java.util.Arrays;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.trekprobe.view.MainActivity;
import com.trekprobe.view.R;
import com.trekprobe.utilities.Utilities;

public class StreamSettingsFragment extends Fragment implements OnItemSelectedListener {
	
	public StreamSettingsFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_stream, container, false);
		
		Spinner maxResSpinner = (Spinner) rootView.findViewById(R.id.max_res_spinner);
		ArrayAdapter<String> adapter = new Utilities.MySpinnerAdapter(this.getActivity(), android.R.layout.simple_spinner_item, Arrays.asList(getResources().getStringArray(R.array.res_array)));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		maxResSpinner.setAdapter(adapter);
		maxResSpinner.setSelection(3);
		Spinner minResSpinner = (Spinner) rootView.findViewById(R.id.min_res_spinner);
		minResSpinner.setAdapter(adapter);
		minResSpinner.setSelection(3);
		
		TextView maxAspRatio = (TextView) rootView.findViewById(R.id.max_ar);
	//	ArrayAdapter<String> adapter2 = new Utilities.MySpinnerAdapter(this.getActivity(), android.R.layout.simple_spinner_item, Arrays.asList(getResources().getStringArray(R.array.aspect_ratio_array)));
	//	adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	//	maxAspRatioSpinner.setAdapter(adapter2);
	//	maxAspRatioSpinner.setSelection(0);
		TextView minAspRatio = (TextView) rootView.findViewById(R.id.min_ar);
	//	minAspRatioSpinner.setAdapter(adapter2);
	//	minAspRatioSpinner.setSelection(0);
		
		Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(),"fonts/automati.ttf");
		TextView resolution = (TextView)rootView.findViewById(R.id.res_title);
		TextView frameRate = (TextView)rootView.findViewById(R.id.fr_title);
		TextView aspRatio = (TextView)rootView.findViewById(R.id.aspect_ratio_title);
		
		TextView streamSetTitle = (TextView)rootView.findViewById(R.id.stream_settings);
		TextView maxRes = (TextView)rootView.findViewById(R.id.max_res_title);		
		TextView minRes = (TextView)rootView.findViewById(R.id.min_res_title);
		TextView maxAspRatioTitle = (TextView)rootView.findViewById(R.id.max_aspect_ratio_title);		
		TextView minAspRatioTitle = (TextView)rootView.findViewById(R.id.min_aspect_ratio_title);
		TextView maxFrRateTitle = (TextView)rootView.findViewById(R.id.max_fr_title);		
		TextView minFrRateTitle = (TextView)rootView.findViewById(R.id.min_fr_title);
		TextView maxFrRate = (TextView)rootView.findViewById(R.id.max_fr);		
		TextView minFrRate = (TextView)rootView.findViewById(R.id.min_fr);
		Switch switchAudio = (Switch)rootView.findViewById(R.id.switch_audio);
		Switch switchLocalStream = (Switch)rootView.findViewById(R.id.switch_local_stream);		
		Switch switchRemoteFS = (Switch)rootView.findViewById(R.id.switch_remote_fs);
		Button applyStream = (Button)rootView.findViewById(R.id.applyStream);
		
		resolution.setTypeface(typeFace);
		frameRate.setTypeface(typeFace);
		aspRatio.setTypeface(typeFace);
		streamSetTitle.setTypeface(typeFace);
		maxRes.setTypeface(typeFace);
		minRes.setTypeface(typeFace);
		maxAspRatio.setTypeface(typeFace);
		minAspRatio.setTypeface(typeFace);
		maxAspRatioTitle.setTypeface(typeFace);
		minAspRatioTitle.setTypeface(typeFace);
		applyStream.setTypeface(typeFace);
		maxFrRateTitle.setTypeface(typeFace);
		minFrRateTitle.setTypeface(typeFace);
		maxFrRate.setTypeface(typeFace);
		minFrRate.setTypeface(typeFace);
		switchAudio.setTypeface(typeFace);
		switchLocalStream.setTypeface(typeFace);
		switchRemoteFS.setTypeface(typeFace);
		
		maxResSpinner.setSelection(((MainActivity)getActivity()).getStreamSettings().getMaxResIndex());
		minResSpinner.setSelection(((MainActivity)getActivity()).getStreamSettings().getMinResIndex());
		
		maxFrRate.setText(((MainActivity)getActivity()).getStreamSettings().getMaxFramerate());
		minFrRate.setText(((MainActivity)getActivity()).getStreamSettings().getMinFramerate());
		
		maxAspRatio.setText(((MainActivity)getActivity()).getStreamSettings().getMaxAspectRatio());
		minAspRatio.setText(((MainActivity)getActivity()).getStreamSettings().getMinAspectRatio());
		
		switchAudio.setChecked(((MainActivity)getActivity()).getStreamSettings().isStreamingAudio());
		switchLocalStream.setChecked(((MainActivity)getActivity()).getStreamSettings().isStreamingLocalVideo());
		switchRemoteFS.setChecked(((MainActivity)getActivity()).getStreamSettings().isRemoteFullScreen());

		return rootView;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	} 
}