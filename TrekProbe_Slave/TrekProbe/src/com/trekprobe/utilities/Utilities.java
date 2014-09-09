package com.trekprobe.utilities;

import java.util.List;

import com.trekprobe.view.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Utilities {
	public static class MySpinnerAdapter extends ArrayAdapter<String> {
	    Typeface font = Typeface.createFromAsset(getContext().getAssets(),
	                        "fonts/automati.ttf");
	    Context context;

	    public MySpinnerAdapter(Context context, int resource, List<String> items) {
	        super(context, resource, items);
	        this.context = context;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        TextView view = (TextView) super.getView(position, convertView, parent);
	        view.setTypeface(font);
	        view.setTextColor(context.getResources().getColor(R.color.orange_select));
	        return view;
	    }

	    @Override
	    public View getDropDownView(int position, View convertView, ViewGroup parent) {
	        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
	        view.setTypeface(font);
	        view.setTextColor(context.getResources().getColor(R.color.orange_title));
	        return view;
	    }
	}
}
