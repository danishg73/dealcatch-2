package com.example.dealcatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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

public class favouritesdeals extends AppCompatActivity {


    List<deal_list> array_deal_list = new ArrayList<>();
    deal_adapter adapter_main;
    ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
    ListView listView;
    String Response,userid,name,email,loggedin="no" ;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sp;
    ImageView backarrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favouritesdeals);


        sp = this.getSharedPreferences("login", MODE_PRIVATE);
        if( sp.contains("usertype"))
        {
            String v=  sp.getString("usertype","").trim();
            if (v.equals("admin"))
            {
                Intent intent = new Intent(favouritesdeals.this,Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else if (v.equals("user"))
            {

                userid = sp.getString("userid", "").trim();
                name = sp.getString("name", "").trim();
                email = sp.getString("email", "").trim();
                loggedin="yes";

            }
        }
        else
        {
            Intent intent = new Intent(favouritesdeals.this,Dashboard.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }



        listView = findViewById(R.id.listview);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.Loading));
        progressDialog.setCancelable(false);

        backarrow=findViewById(R.id.backarrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                favouritesdeals.super.onBackPressed();
            }
        });
        if(isNetworkConnected())
        {
            progressDialog.show();
            getdata();
        }
        else
        {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(favouritesdeals.this, Deal.class);
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
                intent.putExtra("favourtie",  "yes");
                intent.putExtra("like","no");
                intent.putExtra("dislike","no");
                startActivity(intent);
            }
        });






    }



    public void getdata()
    {
        list.clear();
        list.add(new BasicNameValuePair("userid",userid));
        uploadToServer readService= new uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                if(output.contains("No Deals Founds"))
                {

                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(favouritesdeals.this, getResources().getString(R.string.No_deals_found), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    insertdata(output);
                }


            }
        });readService.execute("getfavdeal.php");

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
                array_deal_list.add(dealList);
                dealList = null;
            }
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
        adapter_main = new deal_adapter(array_deal_list, getBaseContext(), favouritesdeals.this);
        listView.setAdapter(adapter_main);
        adapter_main.notifyDataSetChanged();
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