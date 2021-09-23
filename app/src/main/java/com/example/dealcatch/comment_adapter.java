package com.example.dealcatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class comment_adapter extends BaseAdapter {
    Context context;
    List<comment_list> valueList;
    Deal mainActivity;
    ViewEM2 finalViewItem1;





    public comment_adapter(List<comment_list> listValue, Context context, Deal mainActivity) {
        this.context = context;
        this.valueList = listValue;
        this.mainActivity = mainActivity;
    }





    @Override
    public int getCount() {
        return this.valueList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.valueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getViewTypeCount() {

        if (getCount() > 0) {
            return getCount();
        } else {
            return super.getViewTypeCount();
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewEM2 viewItem = null;
        deal_adapter.position=position;

        if(convertView == null)
        {
            viewItem = new ViewEM2();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.custom_comments, null);
            viewItem.title = convertView.findViewById(R.id.title);
            viewItem.comment = convertView.findViewById(R.id.comment);
            viewItem.timestamp = convertView.findViewById(R.id.timestamp);
            convertView.setTag(viewItem);

            viewItem.title.setText(valueList.get(position).full_name);
            viewItem.comment.setText(valueList.get(position).comment);
            viewItem.timestamp.setText(valueList.get(position).datentime);
            finalViewItem1 = viewItem;

            ViewEM2 finalViewItem = viewItem;
        }
        else
        {
            viewItem = (ViewEM2) convertView.getTag();
        }

        return convertView;
    }








}

class ViewEM2 {
    TextView title;
    TextView comment;
    TextView timestamp;
}

