package com.dhian.wave;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.graphics.*;
import android.view.View.*;
import android.view.*;
import android.content.*;
import android.content.pm.*;
import java.util.*;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView;
import android.support.v4.widget.ListViewCompat;

import com.dhian.ListApplicationAdapter;
import android.widget.AdapterView.*;
import android.support.design.widget.*;
import android.support.v7.widget.*;
import android.graphics.drawable.*;
import com.dhian.*;

public class MainActivity extends AppCompatActivity
{
    private ListApplicationAdapter simpleRecyclerAdapter;
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        packageManager = getPackageManager();
        recyclerView = (RecyclerView)findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
		applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));

        if (simpleRecyclerAdapter == null) {
            recyclerView.setAdapter(new ListApplicationAdapter(MainActivity.this,applist,new ListApplicationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ApplicationInfo item,int position) {
                ApplicationInfo app = applist.get(position);
                try {
                    Intent intent = packageManager.getLaunchIntentForPackage(app.packageName);
                    if (null != intent) {
                        startActivity(intent);
                    }
               } catch (ActivityNotFoundException e) {

               } catch (Exception e) {

           }
       }
     }));
     fab = (FloatingActionButton)findViewById(R.id.fab);
     fab.setOnClickListener(new OnClickListener(){
         public void onClick(View v){
             Intent i = new Intent(MainActivity.this,Settings.class);
             startActivity(i);
         }
     });
   }
        
        
       
    }
    
    

    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.settings:
                Intent i = new Intent(this,Settings.class);
                startActivity(i);
                return true;
                
            

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
   

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return applist;
    }

    
}
