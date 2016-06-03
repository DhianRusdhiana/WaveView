package com.dhian;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.widget.*;
import android.content.pm.*;
import android.content.*;
import com.dhian.wave.*;


public class ListApplicationAdapter extends RecyclerView.Adapter<ListApplicationAdapter.ApplicationViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(ApplicationInfo item,int position);
    }
    private List<ApplicationInfo> appsList = null;
    private Context context;
    private PackageManager packageManager;
    private OnItemClickListener listener;

    public ListApplicationAdapter(Context context) {

        this.context = context;
        packageManager = context.getPackageManager();

    }


    public ApplicationInfo getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }


    public ListApplicationAdapter(Context context,List<ApplicationInfo> appsList,OnItemClickListener listener) {
        this.context = context;
        packageManager = context.getPackageManager();
        this.appsList = appsList;
        this.listener = listener;
    }

    @Override
    public ApplicationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item, viewGroup, false);
        ApplicationViewHolder viewHolder = new ApplicationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ApplicationViewHolder versionViewHolder, int i) {
        versionViewHolder.bind(appsList.get(i), listener,i);
        ApplicationInfo data = appsList.get(i);
        if (null != data) {
            versionViewHolder.title.setText(data.loadLabel(packageManager));
            versionViewHolder.subTitle.setText(data.packageName);
            versionViewHolder.iconApp.setImageDrawable(data.loadIcon(packageManager));
        }
    }

    @Override
    public int getItemCount() {

        return appsList == null ? 0 : appsList.size();
    }


    class ApplicationViewHolder extends RecyclerView.ViewHolder {
        CardView cardItemLayout;
        TextView title;
        TextView subTitle;
        ImageView iconApp;

        public ApplicationViewHolder(View itemView) {
            super(itemView);

            cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
            title = (TextView) itemView.findViewById(R.id.app_name);
            subTitle = (TextView) itemView.findViewById(R.id.app_paackage);
            iconApp = (ImageView) itemView.findViewById(R.id.app_icon);




        }
        public void bind(final ApplicationInfo item, final OnItemClickListener listener,final int position) {

            itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        listener.onItemClick(item,position);
                    }
                });
        }

    }

}
