package com.dhian.colorpicker;

import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.*;
import android.content.*;

public class ColorPickerDialog 
	extends 
		Dialog 
	implements
		ColorPickerView.OnColorChangedListener,
		View.OnClickListener {

	private ColorPickerView mColorPicker;
	

	private ColorPickerPanelView mOldColor;
	private ColorPickerPanelView mNewColor;
	
	private EditText mHexVal;
	private boolean mHexValueEnabled = false;
	private ColorStateList mHexDefaultTextColor;

	private OnColorChangedListener mListener;

	private AlertDialog alert;

	public interface OnColorChangedListener {
		public void onColorChanged(int color);
	}
	
	public ColorPickerDialog(Context context, int initialColor) {
		super(context);

		init(initialColor);
		this.setTitle("Choose Color");
	}

	private void init(int color) {
		// To fight color banding.
		getWindow().setFormat(PixelFormat.RGBA_8888);

		setUp(color);

	}

	private void setUp(int color) {
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(getID("dialog_color_picker","layout"), null);

		setContentView(layout);
		setCancelable(true);
	    setTitle("Pick Color Background");
		
		mColorPicker = (ColorPickerView) layout.findViewById(getID("color_picker_view","id"));
		mOldColor = (ColorPickerPanelView) layout.findViewById(getID("old_color_panel","id"));
		mNewColor = (ColorPickerPanelView) layout.findViewById(getID("new_color_panel","id"));
		
		mHexVal = (EditText) layout.findViewById(getID("hex_val","id"));
		mHexVal.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		mHexDefaultTextColor = mHexVal.getTextColors();
		
		mHexVal.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					String s = mHexVal.getText().toString();
					if (s.length() > 5 || s.length() < 10) {
						try {
							int c = ColorPickerPreference.convertToColorInt(s.toString());
							mColorPicker.setColor(c, true);
							mHexVal.setTextColor(mHexDefaultTextColor);
						} catch (IllegalArgumentException e) {
							mHexVal.setTextColor(Color.RED);
						}
					} else {
						mHexVal.setTextColor(Color.RED);
					}
					return true;
				}
				return false;
			}
		});
		
		((LinearLayout) mOldColor.getParent()).setPadding(
			Math.round(mColorPicker.getDrawingOffset()), 
			0, 
			Math.round(mColorPicker.getDrawingOffset()), 
			0
		);	
		
		mOldColor.setOnClickListener(this);
		mNewColor.setOnClickListener(this);
		mColorPicker.setOnColorChangedListener(this);
		mOldColor.setColor(color);
		mColorPicker.setColor(color, true);
		

	}

	@Override
	public void onColorChanged(int color) {

		mNewColor.setColor(color);
		
		if (mHexValueEnabled)
			updateHexValue(color);

		/*
		if (mListener != null) {
			mListener.onColorChanged(color);
		}
		*/

	}
	
	public void setHexValueEnabled(boolean enable) {
		mHexValueEnabled = enable;
		if (enable) {
			mHexVal.setVisibility(View.VISIBLE);
			updateHexLengthFilter();
			updateHexValue(getColor());
		}
		else
			mHexVal.setVisibility(View.GONE);
	}
	
	public boolean getHexValueEnabled() {
		return mHexValueEnabled;
	}
	
	private void updateHexLengthFilter() {
		if (getAlphaSliderVisible())
			mHexVal.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)});
		else
			mHexVal.setFilters(new InputFilter[] {new InputFilter.LengthFilter(7)});
	}

	private void updateHexValue(int color) {
		if (getAlphaSliderVisible()) {
			mHexVal.setText(ColorPickerPreference.convertToARGB(color).toUpperCase(Locale.getDefault()));
		} else {
			mHexVal.setText(ColorPickerPreference.convertToRGB(color).toUpperCase(Locale.getDefault()));
		}
		mHexVal.setTextColor(mHexDefaultTextColor);
	}

	public void setAlphaSliderVisible(boolean visible) {
		mColorPicker.setAlphaSliderVisible(visible);
		if (mHexValueEnabled) {
			updateHexLengthFilter();
			updateHexValue(getColor());
		}
	}
	
	public boolean getAlphaSliderVisible() {
		return mColorPicker.getAlphaSliderVisible();
	}
	
	/**
	 * Set a OnColorChangedListener to get notified when the color
	 * selected by the user has changed.
	 * @param listener
	 */
	public void setOnColorChangedListener(OnColorChangedListener listener){
		mListener = listener;
	}

	public int getColor() {
		return mColorPicker.getColor();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == getID("new_color_panel","id")) {
			if (mListener != null) {
				mListener.onColorChanged(mNewColor.getColor());
			}
		}
		dismiss();
	}
	
	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt("old_color", mOldColor.getColor());
		state.putInt("new_color", mNewColor.getColor());
		return state;
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mOldColor.setColor(savedInstanceState.getInt("old_color"));
		mColorPicker.setColor(savedInstanceState.getInt("new_color"), true);
	}
	public int getID(String name, String Type) {
		return getContext().getResources().getIdentifier(name, Type, getContext().getPackageName());
	}
}
