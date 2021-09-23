package com.example.dealcatch;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Dashboard extends AppCompatActivity implements ActionBottomSheetDialog.ItemClickListener {

    List<deal_list> array_deal_list = new ArrayList<>();
    deal_adapter adapter_main;
    ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
    ListView listView;
    String Response ;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView category;
    String cat ="All";
    SharedPreferences sp,notisetting;
    String lastid="";
    Menu nav_Menu;
    TextView headeremail,headername;
    String email,userid,usertype,name;
    NavigationView navigationView;
    View headerView;
    long back_pressed;
    boolean loading = false;
    LinearProgressIndicator linearProgressIndicator;
    int firstVisibleItem, visibleItemCount,totalItemCount;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        headerView = navigationView.getHeaderView(0);

        nav_Menu = navigationView.getMenu();
        headeremail = headerView.findViewById(R.id.headeremail);
        headername = headerView.findViewById(R.id.headername);
        category =  findViewById(R.id.cat);
        listView = findViewById(R.id.listview);
        linearProgressIndicator = findViewById(R.id.linearProgressIndicator);
        linearProgressIndicator.setVisibility(View.GONE);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.Loading));
        progressDialog.setCancelable(false);

        category.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bottombar();
            }
        });


        sp = this.getSharedPreferences("login", MODE_PRIVATE);
        checkuser();
        notisetting = this.getSharedPreferences("notification", MODE_PRIVATE);
        if(notisetting.contains("notification"))
        {
            String s=  notisetting.getString("notification","").trim();
            if (s.contains("yes"))
            {
                FirebaseMessaging.getInstance().subscribeToTopic("deal");
            }
            else if (s.contains("no"))
            {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("deal");
            }
        }
        else
        {
            FirebaseMessaging.getInstance().subscribeToTopic("deal");
        }


        ///----------------------------------------------------------



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                // Handle navigation view item clicks here.
                int id = menuItem.getItemId();
                if (id == R.id.nav_login)
                {
                    Intent intent = new Intent(Dashboard.this, LoginActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_imprint)
                {
                    Intent intent = new Intent(Dashboard.this, imprint.class);
                    startActivity(intent);
                 }
                else if (id == R.id.nav_share)
                {
                    Intent i=new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT,getResources().getString(R.string.app_name));
                    i.putExtra(Intent.EXTRA_TEXT," get app by https://play.google.com/store/apps/details?id=com.dealcatch.app");
                    startActivity(getIntent().createChooser(i, getResources().getString(R.string.share)));
                }
                else if (id == R.id.nav_data_protection)
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dealcatch.de/datenschutzerklaerung"));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(browserIntent);
                }
                else if (id == R.id.nav_condtion)
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dealcatch.de/agb"));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(browserIntent);
                }
                else if (id == R.id.nav_rate)
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.dealcatch.app"));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(browserIntent);
                }
                else if (id == R.id.nav_contact)
                {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"selcuk.camuz19@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Deal Adding Request");
                    intent.putExtra(Intent.EXTRA_TEXT, "Please add my deal along with following details:" +
                            "\nTitle:" +
                            "\nDescription:" +
                            "\nActual price:" +
                            "\nDiscounted Price:" +
                            "\nLink:" +
                            "\nCategory: " +
                            "\nExpirey Date:" +
                            "\nExpirey Time:" );
                    try
                    {
                        startActivity(Intent.createChooser(intent, getResources().getString(R.string.send_mail)));
                    }
                    catch (ActivityNotFoundException ex)
                    {
                        Toast.makeText(Dashboard.this, getResources().getString(R.string.no_email_app), Toast.LENGTH_SHORT).show();
                    }
                }
                else if (id == R.id.nav_setting)
                {
                    Intent intent = new Intent(Dashboard.this, Settings.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_admin)
                {
                      Intent intent = new Intent(Dashboard.this, Admindashboard.class);
                      startActivity(intent);
                }
                else if (id == R.id.nav_fav)
                {
                    Intent intent = new Intent(Dashboard.this, favouritesdeals.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_logout)
                {
                    nav_Menu.findItem(R.id.nav_login).setVisible(true);
                    nav_Menu.findItem(R.id.nav_logout).setVisible(false);
                    nav_Menu.findItem(R.id.nav_admin).setVisible(false);
                    SharedPreferences.Editor e = sp.edit();
                    e.clear();
                    e.commit();
                    drawer.closeDrawers();
                    Intent intent = new Intent(Dashboard.this,Splashscreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
        //-----------------------------------------------------------
        if(isNetworkConnected())
        {
            progressDialog.show();
            getdata();
        }
        else
        {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                if(isNetworkConnected())
                {
                    lastid=null;
                    array_deal_list.clear();
                    getdata();
                }
                else
                {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(Dashboard.this,getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(Dashboard.this, Deal.class);
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



//
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//
//                Log.d("TEST", "first : " + firstVisibleItem);
//                Log.d("TEST", "visible : " + visibleItemCount);
//                Log.d("TEST", "total : " + totalItemCount);
//
//
//                if ((lastInScreen == totalItemCount) && !loading && (firstVisibleItem != 0)) {
//                    loading = true;
//                    linearProgressIndicator.setVisibility(View.VISIBLE);
//                    loadmore();
//
//
//
//                }
//            }
//        });

//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//
//            }
//
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//
//                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
//                {
//                    if(loading == false)
//                    {
//                        linearProgressIndicator.setVisibility(View.VISIBLE);
//                        loading = true;
//                        loadmore();
//                    }
//                }
//            }
//        });

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
    }
    @Override
    public void onResume(){
        super.onResume();
      //  Toast.makeText(this, "Resume", Toast.LENGTH_SHORT).show();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        headerView = navigationView.getHeaderView(0);
        nav_Menu = navigationView.getMenu();
        checkuser();
    }
    @Override
    public void onBackPressed() {
        if (back_pressed + 1000 > System.currentTimeMillis()){

            super.onBackPressed();
        }
        else
            {
            Toast.makeText(getBaseContext(),
                    R.string.press_once_again, Toast.LENGTH_SHORT)
                    .show();
        }
        back_pressed = System.currentTimeMillis();
    }
    public void loadmore()
    {
        list.clear();
        list.add(new BasicNameValuePair("category",cat));
        list.add(new BasicNameValuePair("lastid",lastid));
        uploadToServer readService= new uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                linearProgressIndicator.setVisibility(View.GONE);
                loading=false;
                if(output.contains("No Deals Founds"))
                {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(Dashboard.this, getResources().getString(R.string.No_deals_found), Toast.LENGTH_SHORT).show();
                    lastid = "0";
                }
                else
                {
                    insertdata(output);
                }
            }
        });readService.execute("getdeals.php");
    }
    private void checkuser()
    {
        if( sp.contains("usertype"))
        {
            String v=  sp.getString("usertype","").trim();
            if (v.equals("admin"))
            {
//                Intent intent = new Intent(Login_Employee.this, Manager_Dashboard.class);
//                startActivity(intent);
                nav_Menu.findItem(R.id.nav_login).setVisible(false);
                nav_Menu.findItem(R.id.nav_admin).setVisible(true);
                nav_Menu.findItem(R.id.nav_logout).setVisible(true);
                nav_Menu.findItem(R.id.nav_fav).setVisible(false);
            }
            else if (v.equals("user"))
            {
                email = sp.getString("email", "").trim();
                name = sp.getString("name", "").trim();
                userid = sp.getString("userid", "").trim();
                usertype = sp.getString("usertype", "").trim();
                headername.setText(name);
                headeremail.setText(email);
                nav_Menu.findItem(R.id.nav_login).setVisible(false);
                nav_Menu.findItem(R.id.nav_logout).setVisible(true);
                nav_Menu.findItem(R.id.nav_admin).setVisible(false);
                nav_Menu.findItem(R.id.nav_fav).setVisible(true);
            }
        }
        else
        {
            nav_Menu.findItem(R.id.nav_admin).setVisible(false);
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_fav).setVisible(false);
        }
    }

    public void getdata()
    {
        list.clear();
        list.add(new BasicNameValuePair("category",cat));
        uploadToServer readService= new uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                if(output.contains("No Deals Founds"))
                {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(Dashboard.this, getResources().getString(R.string.No_deals_found), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    insertdata(output);
                }
            }
        });readService.execute("getdeals.php");

    }
    public void bottombar()
    {
        ActionBottomSheetDialog addPhotoBottomDialogFragment =
                ActionBottomSheetDialog.newInstance();
        addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                ActionBottomSheetDialog.TAG);
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
        listView.setAdapter(null);
        adapter_main = new deal_adapter(array_deal_list, getBaseContext(), Dashboard.this);
        listView.setAdapter(adapter_main);
        listView.invalidateViews();
        listView.onRestoreInstanceState(state);
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


    @Override
    public void onItemClick(String item)
    {
        cat = item.trim();
        array_deal_list.clear();
        progressDialog.show();
        getdata();
     //   Toast.makeText(this, item+"", Toast.LENGTH_SHORT).show();
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
    public static String timediff(String gettime)   {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Date date1=null,date2 = null;

        try
        {
           date1 = simpleDateFormat.parse(currentTime);
           date2 = simpleDateFormat.parse(gettime);
        } catch (ParseException e)
        {

            // Exception handling goes here
        }
       // DateFormat date = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");


        long difference = date2.getTime() - date1.getTime();
        int days = (int) (difference / (1000*60*60*24));
        int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        hours = (hours < 0 ? -hours : hours);
        if(days == 0)
        {
            if (hours == 0)
            {
                return min+"min";
            }
            else
                {
                    return hours+"h "+min+"m";
                }
        }
        return days+"d "+hours+"h "+min+"m";
    }



}