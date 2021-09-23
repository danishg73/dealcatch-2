package com.example.dealcatch;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class category_list_adapter extends BaseAdapter {
    Context context;
    List<category_list> valueList;
    static int position = 0;
    Addcategory mainActivity;

    String classtype="" ;
    Calendar c = Calendar.getInstance();
    ViewEM22 finalViewItem1;







    public category_list_adapter(List<category_list> listValue, Context context,  Addcategory mainActivity ) {
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
        ViewEM22 viewItem = null;
        category_list_adapter.position=position;

        if(convertView == null)
        {
            viewItem = new ViewEM22();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                convertView = layoutInfiater.inflate(R.layout.custom_category_list, null);
                viewItem.name = convertView.findViewById(R.id.category_name);
                convertView.setTag(viewItem);
                viewItem.name.setText(valueList.get(position).category);




            finalViewItem1 = viewItem;


            ViewEM22 finalViewItem = viewItem;





        }
        else
        {
            viewItem = (ViewEM22) convertView.getTag();
        }

        return convertView;
    }






}

class ViewEM22 {
    TextView name;
}

