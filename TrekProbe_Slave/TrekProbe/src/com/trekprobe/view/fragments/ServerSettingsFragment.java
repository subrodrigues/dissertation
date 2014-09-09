package com.trekprobe.view.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.trekprobe.utilities.IPAddressKeyListener;
import com.trekprobe.variables.Variables;
import com.trekprobe.view.R;

public class ServerSettingsFragment extends Fragment {
	
	public ServerSettingsFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_server, container, false);
		
		Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(),"fonts/automati.ttf");
		EditText ipAddress = (EditText)rootView.findViewById(R.id.ip_address);
		ipAddress.setKeyListener(IPAddressKeyListener.getInstance());
		EditText port = (EditText)rootView.findViewById(R.id.port);
		Button start = (Button)rootView.findViewById(R.id.apply);
		
		TextView serverSettings = (TextView)rootView.findViewById(R.id.server_settings);
		TextView ipAddressTitle = (TextView)rootView.findViewById(R.id.ip_address_title);
		TextView portTitle = (TextView)rootView.findViewById(R.id.port_title);
		
		serverSettings.setTypeface(typeFace);
		ipAddressTitle.setTypeface(typeFace);
		portTitle.setTypeface(typeFace);
		ipAddress.setTypeface(typeFace);
		port.setTypeface(typeFace);
		start.setTypeface(typeFace);
		
		ipAddress.setText(Variables.ipAddress);
		port.setText(Variables.port);
		
		return rootView;
	} 
}
