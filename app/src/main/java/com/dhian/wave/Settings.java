package com.dhian.wave;

import android.preference.*;
import android.os.*;
import android.os.Process;
import com.dhian.colorpicker.*;
import android.content.*;
import android.view.*;
import android.app.*;
import android.widget.*;
import com.dhian.colorpicker.*;
import android.support.v7.widget.Toolbar;
import android.graphics.*;

public class Settings extends PreferenceActivity
{
    private WaveView wv;
    private ColorPickerPreference a;
    private Toolbar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);  
        setContentView(R.layout.view_settings);
        
        wv = (WaveView)findViewById(R.id.wv);
        a = (ColorPickerPreference)findPreference("wave_color");
        a.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener)new Preference.OnPreferenceChangeListener(){
            public boolean onPreferenceChange(Preference preference, Object object) {
                int w = object.hashCode();
                wv.setWaveColor(w,adjustAlpha(w,0.4f));
                Toast.makeText(getBaseContext(),String.valueOf(object),Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }
    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
    private void restartWithConfirm() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please restart to apply your settings!")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Restart();
                }
            })
            .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    finish();
                }
            }).create();
        builder.show();
    }   

    public static void Restart(){
        Process.killProcess(Process.myPid());
    }
    
    

    @Override
    public void onBackPressed() {
        restartWithConfirm();
    }
   
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.view_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restartWithConfirm();
                }
            });
    }
	
}
