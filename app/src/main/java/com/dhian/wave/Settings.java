package com.dhian.wave;

import android.preference.*;
import android.os.*;

import com.dhian.colorpicker.*;
import android.content.*;
import android.view.*;

public class Settings extends PreferenceActivity
{
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);  
        
    }
   
	
}
