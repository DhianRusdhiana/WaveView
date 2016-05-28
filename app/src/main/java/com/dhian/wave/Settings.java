package com.dhian.wave;

import android.preference.*;
import android.os.*;
import android.os.Process;
import com.dhian.colorpicker.*;
import android.content.*;
import android.view.*;
import android.app.*;

public class Settings extends PreferenceActivity
{
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);  
        
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
   
	
}
