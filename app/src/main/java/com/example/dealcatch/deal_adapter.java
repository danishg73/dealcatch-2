package com.example.dealcatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class deal_adapter extends BaseAdapter {
    Context context;
    List<deal_list> valueList;
    static int position = 0;
    Dashboard mainActivity2;
    Admindashboard mainActivity3;
    favouritesdeals mainActivity4;
    ViewEM finalViewItem1;








    public deal_adapter(List<deal_list> listValue, Context context, Dashboard mainActivity2) {
        this.context = context;
        this.valueList = listValue;
        this.mainActivity2 = mainActivity2;
    }

    public deal_adapter(List<deal_list> listValue, Context context, Admindashboard mainActivity3) {
        this.context = context;
        this.valueList = listValue;
        this.mainActivity3 = mainActivity3;
    }
    public deal_adapter(List<deal_list> listValue, Context context, favouritesdeals mainActivity4) {
        this.context = context;
        this.valueList = listValue;
        this.mainActivity4 = mainActivity4;
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
        ViewEM viewItem = null;
        deal_adapter.position=position;

        if(convertView == null)
        {
            viewItem = new ViewEM();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.custom_deal_list, null);
            viewItem.title = convertView.findViewById(R.id.title);
            viewItem.expires = convertView.findViewById(R.id.expire);
            viewItem.description = convertView.findViewById(R.id.description);
            viewItem.total_likes = convertView.findViewById(R.id.total_likes);
            viewItem.total_dislikes = convertView.findViewById(R.id.total_dislikes);
            viewItem.total_comments = convertView.findViewById(R.id.total_comments);
            viewItem.gotodeal = convertView.findViewById(R.id.gotodeal);
            viewItem.actualprice = convertView.findViewById(R.id.actualprice);
            viewItem.discountedprice = convertView.findViewById(R.id.discountedprice);
            viewItem.deal_image = convertView.findViewById(R.id.deal_image);
            viewItem.custom_linearlayout = convertView.findViewById(R.id.custom_linearlayout);

            convertView.setTag(viewItem);

            viewItem.title.setText(valueList.get(position).title);
            viewItem.description.setText(valueList.get(position).description);
            viewItem.total_likes.setText(valueList.get(position).total_likes);
            viewItem.total_dislikes.setText(valueList.get(position).total_dislikes);
            viewItem.total_comments.setText(valueList.get(position).total_comments);
            if(valueList.get(position).remainhours.equals(""))
            {
                viewItem.expires.setVisibility(View.GONE);
            }
            else if(valueList.get(position).remainhours.equals("expired"))
            {
                viewItem.expires.setText(context.getResources().getString(R.string.expired));
                viewItem.expires.setBackground(context.getResources().getDrawable(R.drawable.buttongrey));
                viewItem.gotodeal.setBackground(context.getResources().getDrawable(R.drawable.buttongrey));
                viewItem.custom_linearlayout.setBackground(context.getResources().getDrawable(R.drawable.round_corner_grey));

            }
            else
            {
                Animation animationListView = AnimationUtils.loadAnimation(context, R.anim.flash_leave_now);
                viewItem.expires.startAnimation(animationListView);

              //  viewItem.expires.setAnimation(AnimationUtils.loadAnimation(context, R.anim.flash_leave_now));
                viewItem.expires.setText(context.getResources().getString(R.string.Expires_in)+""+valueList.get(position).remainhours);
            }
            viewItem.actualprice.setText(valueList.get(position).actualprice+"€");
            viewItem.discountedprice.setText(valueList.get(position).discountedprice+"€");
            Glide.with(context).load(context.getResources().getString(R.string.link)+valueList.get(position).picpath)
                    .into(viewItem.deal_image);




            finalViewItem1 = viewItem;


            ViewEM finalViewItem = viewItem;
            viewItem.gotodeal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(valueList.get(position).link));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                }
            });





        }
        else
        {
            viewItem = (ViewEM) convertView.getTag();
        }

        return convertView;
    }








}

class ViewEM {
    TextView title;
    TextView description;
    TextView gotodeal;
    TextView total_likes;
    TextView total_dislikes;
    TextView total_comments;
    TextView actualprice;
    TextView discountedprice;
    TextView expires;
    ImageView deal_image;
    LinearLayout custom_linearlayout;
    ImageView like_img;
    ImageView dislike_img;
}

