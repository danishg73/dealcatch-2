package com.example.dealcatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.dealcatch.Dashboard.timediff;

public class Admindashboard extends AppCompatActivity  implements ActionBottomSheetDialog.ItemClickListener
{
    LinearLayout add_deal,logout,add_cat;
    ListView listView;
    deal_adapter adapter_main;
    String Response ;
    ImageView backarrow;
    ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
    boolean loading = false;
    SharedPreferences sp;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView category;
    LinearProgressIndicator linearProgressIndicator;
    int firstVisibleItem, visibleItemCount,totalItemCount;
    String lastid="";

    String cat ="All";
    List<deal_list> array_deal_list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admindashboard);



        sp = this.getSharedPreferences("login", MODE_PRIVATE);
        if( sp.contains("usertype"))
        {
            String v=  sp.getString("usertype","").trim();
            if (v.equals("admin"))
            {

            }
            else if (v.equals("user"))
            {

                Intent intent = new Intent(Admindashboard.this,Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        }



        add_deal = findViewById(R.id.add_deal);
        logout = findViewById(R.id.logout);
        add_cat = findViewById(R.id.add_cat);

        category =  findViewById(R.id.cat);
        listView = findViewById(R.id.listview);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        linearProgressIndicator = findViewById(R.id.linearProgressIndicator);
        linearProgressIndicator.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.Loading));
        progressDialog.setCancelable(false);


        sp = this.getSharedPreferences("login", MODE_PRIVATE);
        adapter_main = new deal_adapter(array_deal_list, getBaseContext(), Admindashboard.this);




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                if(isNetworkConnected())
                {

                    getdata();

                }
                else
                {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(Admindashboard.this,getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }


            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor e = sp.edit();
                e.clear();
                e.commit();
                Intent intent = new Intent(Admindashboard.this,Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


        category.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bottombar();
            }
        });


        add_deal.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Admindashboard.this, Add_deal.class);
                startActivity(intent);
            }
        });
        add_cat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Admindashboard.this, Addcategory.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(Admindashboard.this, Deal.class);
                intent.putExtra("title",  array_deal_list.get(position).title);
                intent.putExtra("description",  array_deal_list.get(position).description);
                intent.putExtra("id",  array_deal_list.get(position).id);
                intent.putExtra("timestamp",  array_deal_list.get(position).timestamp);
                intent.putExtra("category",  array_deal_list.get(position).category);
                intent.putExtra("link",  array_deal_list.get(position).link);
                intent.putExtra("total_likes",  array_deal_list.get(position).total_likes);
                intent.putExtra("total_dislikes",  array_deal_list.get(position).total_dislikes);
                intent.putExtra("total_comments",  array_deal_list.get(position).total_comments);
                intent.putExtra("picpath",  array_deal_list.get(position).picpath);
                intent.putExtra("actualprice",  array_deal_list.get(position).actualprice);
                intent.putExtra("discountedprice",  array_deal_list.get(position).discountedprice);
                intent.putExtra("like","no");
                intent.putExtra("dislike","no");
                intent.putExtra("favourtie",  "no");
                startActivity(intent);
            }
        });




        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;

                Log.d("TEST", "first : " + firstVisibleItem);
                Log.d("TEST", "visible : " + visibleItemCount);
                Log.d("TEST", "total : " + totalItemCount);


                if ((lastInScreen == totalItemCount) && !loading && (firstVisibleItem != 0)) {
                    loading = true;
                    linearProgressIndicator.setVisibility(View.VISIBLE);
                    loadmore();



                }
            }
        });



        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItemm, int visibleItemCountt,
                                 int totalItemCountt) {
                firstVisibleItem = firstVisibleItemm;
                visibleItemCount = visibleItemCountt;
                totalItemCount = totalItemCountt;
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE && loading == false) {
                    //expandapleInt++;
                    linearProgressIndicator.setVisibility(View.VISIBLE);
                    loading = true;
                    // Toast.makeText(Dashboard.this, "kl", Toast.LENGTH_SHORT).show();
                    loadmore();
                    //get next 10-20 items(your choice)items

                }
            }
        });







        backarrow=findViewById(R.id.backarrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Admindashboard.super.onBackPressed();
            }
        });
    }




    @Override
    public void onResume()
    {
        super.onResume();

        if(isNetworkConnected())
        {
            progressDialog.show();
            lastid=null;
            getdata();
        }
        else
        {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }

    }

    public void loadmore()
    {
        list.clear();
        list.add(new BasicNameValuePair("category",cat));
        list.add(new BasicNameValuePair("lastid",lastid));
      uploadToServer readService= new  uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                loading=false;
                linearProgressIndicator.setVisibility(View.GONE);
                if(output.contains("No Deals Founds"))
                {
                    lastid = "0";
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(Admindashboard.this, getResources().getString(R.string.No_deals_found), Toast.LENGTH_SHORT).show();
                }
                else
                {

                    insertdata(output);
                }


            }
        });readService.execute("getdeals.php");

    }

    public void getdata()
    {
        list.clear();
        list.add(new BasicNameValuePair("category",cat));
        array_deal_list.clear();
         uploadToServer readService= new  uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                if(output.contains("No Deals Founds"))
                {

                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(Admindashboard.this, getResources().getString(R.string.No_deals_found), Toast.LENGTH_SHORT).show();
                }
                else
                {

                    listView.setAdapter(adapter_main);
                    insertdata(output);
                }


            }
        });readService.execute("getdeals.php");

    }

    public void insertdata(String output)
    {
        try {
            JSONArray jsonObject = new JSONArray(output);
            for (int i=0; i<jsonObject.length(); i++)
            {

                deal_list dealList = new deal_list();
                JSONObject obj = jsonObject.getJSONObject(i);
                dealList.id = obj.getString("id").trim();
                dealList.title = obj.getString("title").trim();
                dealList.description =obj.getString("description").trim();
                dealList.category =obj.getString("category").trim();
                dealList.link =obj.getString("link").trim();
                dealList.expireytime =obj.getString("expireytime").trim();
                if(!obj.getString("expireytime").trim().equals(""))
                {
                    if(timediff(obj.getString("expireytime").trim()).contains("-"))
                    {
                        dealList.remainhours="expired";
                    }
                    else
                    {
                        dealList.remainhours = timediff(obj.getString("expireytime").trim());
                    }
                }
                else
                {
                    dealList.remainhours="";
                }
                 dealList.total_likes =obj.getString("likes").trim();
                dealList.total_dislikes = obj.getString("dislikes").trim();
                dealList.likeby = obj.getString("like_by").trim();
                dealList.dislikeby = obj.getString("dislike_by").trim();
                dealList.picpath = obj.getString("pic_path").trim();
                dealList.timestamp = obj.getString("creation_date").trim();
                dealList.total_comments = obj.getString("comments").trim();
                dealList.actualprice = obj.getString("actualprice").trim();
                dealList.discountedprice = obj.getString("discountedprice").trim();
                lastid = obj.getString("id").trim();
                array_deal_list.add(dealList);
                dealList = null;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        Parcelable state = listView.onSaveInstanceState();
        adapter_main.notifyDataSetChanged();
        listView.setAdapter(adapter_main);
        listView.onRestoreInstanceState(state);
    }
    public void bottombar()
    {

        ActionBottomSheetDialog addPhotoBottomDialogFragment =
                ActionBottomSheetDialog.newInstance();
        addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                ActionBottomSheetDialog.TAG);
    }
    @Override
    public void onItemClick(String item)
    {
        cat = item.trim();
        array_deal_list.clear();
        progressDialog.show();
        getdata();
        //   Toast.makeText(this, item+"", Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public class uploadToServer extends AsyncTask<String, Void, String> {

        ReadServiceResponse delegate =null;
        public uploadToServer(ReadServiceResponse res) {
            delegate = res;
        }
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... arg0) {

            String z = arg0[0];
            String link=getResources().getString(R.string.link)+z;



            try {
                HttpPost httppost = new HttpPost(link);
                HttpClient httpclient = new DefaultHttpClient();
                httppost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse response = httpclient.execute(httppost);
                Response = EntityUtils.toString(response.getEntity());
            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return Response;

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            delegate.processFinish(result);

        }
    }


}