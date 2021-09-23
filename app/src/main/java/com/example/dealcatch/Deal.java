package com.example.dealcatch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Deal extends AppCompatActivity
{
    ImageView titleimg,share,delete;
    ImageView likeimg,dislikeimg,favour;
    ImageView backarrow;
    ListView listView;
    LinearLayout listll;
    LinearLayout linearlist;
    Button send_comment;
    List<comment_list> array_comment_list = new ArrayList<>();
    comment_adapter adapter_main;
    ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
    ProgressBar pb,pb2,pb3;
    String userid,like_status="";
    String favourtie = "no";
   // LayoutInflater layoutInfiater;
    ProgressDialog progressDialog;
    SharedPreferences sp;
    ViewGroup.LayoutParams params;
    int h =0;
    EditText comment_input;
    AlertDialog.Builder builder1;
    TextView aprice,dprice;
    RelativeLayout relativeLayout,relativelayout2,relativelayout3;
    TextView title,description,totallike,totaldislike,totalcomment,timestamp,gotodeal,categorytextview;
    String gettitle,getdescription,gettimestamp,getcategory, getlikes,getdislikes,getcomments ,link,picpath,actualprice,discountedprice;
    String Response,getcomment,loggedin="",name,email,totallikes,totaldislikes;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        totallike = findViewById(R.id.total_likes);
        titleimg = findViewById(R.id.title_img);
        totaldislike = findViewById(R.id.total_dislikes);
        totalcomment = findViewById(R.id.total_comments);
        timestamp = findViewById(R.id.posted_date);
        share = findViewById(R.id.share);
        timestamp = findViewById(R.id.posted_date);
        gotodeal = findViewById(R.id.gotodeal);
        listView = findViewById(R.id.listview);
        favour = findViewById(R.id.favour);
        relativeLayout = findViewById(R.id.relativelayout);
        relativelayout2 = findViewById(R.id.relativelayout2);
        relativelayout3 = findViewById(R.id.relativelayout3);
        send_comment = findViewById(R.id.send_comment);
        comment_input = findViewById(R.id.comment_input);
        likeimg = findViewById(R.id.like_img);
        delete = findViewById(R.id.delete);
        dislikeimg = findViewById(R.id.dislike_img);
        aprice = findViewById(R.id.actualprice);
        dprice = findViewById(R.id.discountedprice);
        totallike = findViewById(R.id.total_likes);
        categorytextview = findViewById(R.id.categorytextview);
        totaldislike = findViewById(R.id.total_dislikes);
        pb = findViewById(R.id.progressBar1);
        pb2 = findViewById(R.id.progressBar2);
        pb3 = findViewById(R.id.progressBar3);
        linearlist = findViewById(R.id.linearlist);
        listll = findViewById(R.id.listll);
      //  layoutInfiater = (LayoutInflater)this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);



        gettitle = getIntent().getStringExtra("title");
        getdescription = getIntent().getStringExtra("description");
        id = getIntent().getStringExtra("id");
        gettimestamp = getIntent().getStringExtra("timestamp");
        link = getIntent().getStringExtra("link");
        getcategory = getIntent().getStringExtra("category");
        getlikes = getIntent().getStringExtra("total_likes");
        getdislikes = getIntent().getStringExtra("total_dislikes");
        getcomments = getIntent().getStringExtra("total_comments");
        picpath = getIntent().getStringExtra("picpath");
        actualprice = getIntent().getStringExtra("actualprice");
        discountedprice = getIntent().getStringExtra("discountedprice");
        favourtie=getIntent().getStringExtra("favourtie").trim();

        title.setText(gettitle);
        categorytextview.setText(getcategory);
        description.setText(getdescription);
        totallike.setText(getlikes);
        totaldislike.setText(getdislikes);
        totalcomment.setText(getcomments);
        timestamp.setText(gettimestamp);
        aprice.setText(actualprice+"€");
        dprice.setText(discountedprice+"€");
        Glide.with(this).load( getResources().getString(R.string.link)+picpath).into(titleimg);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.Loading));
        progressDialog.setCancelable(true);
        sp = this.getSharedPreferences("login", MODE_PRIVATE);

        if( sp.contains("usertype"))
        {
            String v=  sp.getString("usertype","").trim();
            if (v.equals("admin"))
            {
                likeimg.setEnabled(false);
                dislikeimg.setEnabled(false);
                comment_input.setEnabled(false);
                delete.setVisibility(View.VISIBLE);
                favour.setVisibility(View.GONE);
                relativelayout3.setVisibility(View.GONE);
                pb3.setVisibility(View.GONE);
            }
            else if (v.equals("user"))
            {
                userid = sp.getString("userid", "").trim();
                name = sp.getString("name", "").trim();
                email = sp.getString("email", "").trim();
                loggedin="yes";
                likeimg.setEnabled(true);
                dislikeimg.setEnabled(true);
                comment_input.setEnabled(true);
                delete.setVisibility(View.GONE);
                favour.setVisibility(View.VISIBLE);
                favour.setEnabled(true);
            }
        }
        else
        {
            relativelayout3.setVisibility(View.GONE);
            pb3.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            favour.setEnabled(false);

        }
        array_comment_list.clear();
        adapter_main = new comment_adapter(array_comment_list, getBaseContext(), Deal.this);
        listView.setAdapter(adapter_main);
        params = listView.getLayoutParams();

        if(isNetworkConnected())
        {

            getlikes();
            if(favourtie.equals("yes"))
            {
                favour.setBackgroundResource(0);
                favour.setBackground(getResources().getDrawable(R.drawable.ic_fav_yes));
                relativelayout3.setVisibility(View.GONE);
                pb3.setVisibility(View.GONE);
            }
            else
            {
                getfav();

            }
        }
        else
        {
            relativelayout2.setVisibility(View.GONE);
            pb2.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            pb.setVisibility(View.GONE);
            relativelayout3.setVisibility(View.GONE);
            pb3.setVisibility(View.GONE);
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                builder1 = new AlertDialog.Builder(Deal.this);
                builder1.setMessage(getResources().getString(R.string.want_to_delete_this_Deal));
                builder1.setCancelable(true);

                builder1.setPositiveButton(getResources().getString(R.string.Yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                 delete();
                                 progressDialog.show();
                            }
                        });
                builder1.setNegativeButton(getResources().getString(R.string.No),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });
        favour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favourtie.equals("no"))
                {
                    favour.setBackgroundResource(0);
                    favour.setBackground(getResources().getDrawable(R.drawable.ic_fav_yes));
                    favourtie="yes";
                    managefav();
                }
                else if(favourtie.equals("yes"))
                {
                    favour.setBackgroundResource(0);
                    favour.setBackground(getResources().getDrawable(R.drawable.ic_fav_no));
                    favourtie="no";
                    managefav();
                }
            }
        });


        likeimg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(loggedin.contains("yes"))
                {
                int a =Integer.parseInt(getlikes);
                if(like_status.equals("1"))
                {
                    likeimg.setBackgroundResource(0);
                    likeimg.setBackground(getResources().getDrawable(R.drawable.ic_like));
                    getlikes = a-1+"";
                    totallike.setText(getlikes);
                    like_status="0";
                }
                else if(like_status.equals("0"))
                {
                    likeimg.setBackgroundResource(0);
                    likeimg.setBackground(getResources().getDrawable(R.drawable.ic_like_yes));
                    like_status="1";
                    getlikes = a+1+"";
                    totallike.setText(getlikes);
                }

                else if(like_status.equals("-1"))
                {
                    likeimg.setBackgroundResource(0);
                    likeimg.setBackground(getResources().getDrawable(R.drawable.ic_like_yes));
                    getlikes = a+1+"";
                    totallike.setText(getlikes);
                    like_status="1";

                    dislikeimg.setBackgroundResource(0);
                    dislikeimg.setBackground(getResources().getDrawable(R.drawable.ic_dislike));
                    int b = Integer.parseInt(getdislikes);
                    getdislikes = b-1+"";
                    totaldislike.setText(getdislikes);
                }
                managelikes();

                }
                else
                {
                    Toast.makeText(Deal.this, R.string.You_are_no_Logged_In, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dislikeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if(loggedin.contains("yes"))
                {
                int a =Integer.parseInt(getdislikes);
                if(like_status.equals("-1"))
                {
                    dislikeimg.setBackgroundResource(0);
                    dislikeimg.setBackground(getResources().getDrawable(R.drawable.ic_dislike));
                    getdislikes = a-1+"";
                    totaldislike.setText(getdislikes);
                    like_status="0";
                }
                else if(like_status.equals("0"))
                {
                    dislikeimg.setBackgroundResource(0);
                    dislikeimg.setBackground(getResources().getDrawable(R.drawable.ic_dislike_yes));
                    like_status="-1";
                    getdislikes = a+1+"";
                    totaldislike.setText(getdislikes);
                }
                else if(like_status.equals("1"))
                {
                    dislikeimg.setBackgroundResource(0);
                    dislikeimg.setBackground(getResources().getDrawable(R.drawable.ic_dislike_yes));
                    like_status="-1";
                    getdislikes = a+1+"";
                    totaldislike.setText(getdislikes);

                    likeimg.setBackgroundResource(0);
                    likeimg.setBackground(getResources().getDrawable(R.drawable.ic_like));
                    int b = Integer.parseInt(getlikes);
                    getlikes = b-1+"";
                    totallike.setText(getlikes);
                }


                managelikes();

                }
                else
                {
                    Toast.makeText(Deal.this, R.string.You_are_no_Logged_In, Toast.LENGTH_SHORT).show();
                }
            }
        });
        gotodeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
                startActivity(browserIntent);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT,getResources().getString(R.string.app_name));
                i.putExtra(Intent.EXTRA_TEXT,link);
                startActivity(getIntent().createChooser(i, getResources().getString(R.string.share)));
            }
        });
        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                if(loggedin.contains("yes"))
                {
                    getcomment = comment_input.getText().toString().trim();
                    if (TextUtils.isEmpty(getcomment))
                    {
                        Toast.makeText(Deal.this, R.string.cannot_be_empty, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        list.clear();
                        list.add(new BasicNameValuePair("dealid",id));
                        list.add(new BasicNameValuePair("userid",userid));
                        list.add(new BasicNameValuePair("email",email));
                        list.add(new BasicNameValuePair("name",name));
                        list.add(new BasicNameValuePair("comment",getcomment));
                        progressDialog.show();
                        uploadToServer readService= new uploadToServer(new ReadServiceResponse() {

                            @Override
                            public void processFinish(String output)
                            {
                                progressDialog.dismiss();
                                list.clear();

                                if(output.contains("Commmented"))
                                {
//                                    View vi = layoutInfiater.inflate(R.layout.custom_comments, null);
//                                    pname = vi.findViewById(R.id.title);
//                                    comment = vi.findViewById(R.id.comment);
//                                    ctimestamp = vi.findViewById(R.id.timestamp);
//
//                                    pname.setText(name);
//                                    comment.setText(getcomment);
//                                    ctimestamp.setText("");
//                                    linearlist.addView(vi);
                                            comment_list commentList = new comment_list();
                                            commentList.full_name =name;
                                            commentList.comment =getcomment;
                                            commentList.datentime ="";
                                            array_comment_list.add(commentList);
                                            commentList = null;
                                            listView.setAdapter(adapter_main);
                                            adapter_main.notifyDataSetChanged();
                                            getcomment = Integer.parseInt(getcomments)+1+"";
                                            totalcomment.setText(getcomment);
//                                            if(height<1)
//                                            {
//
//                                            }
//                                            height++;
//                                            params.height = height*120;
//                                            listView.setLayoutParams(params);
//                                            listView.requestLayout();
                                }
                                else
                                {
                                    Toast.makeText(Deal.this, output+"", Toast.LENGTH_SHORT).show();
                                }

                                comment_input.setText("");
                                getcomment="";


                            }
                        });readService.execute("create_comment.php");

                    }
                }
                else
                {
                    Toast.makeText(Deal.this, R.string.You_are_no_Logged_In, Toast.LENGTH_SHORT).show();
                }

            }
        });

        backarrow=findViewById(R.id.backarrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Deal.super.onBackPressed();
                finish();
            }
        });



    }

    public void managefav()
    {
        list.clear();
        list.add(new BasicNameValuePair("dealid",id));
        list.add(new BasicNameValuePair("userid",userid));
        list.add(new BasicNameValuePair("fav",favourtie));
        // Toast.makeText(this, "dealid: "+id, Toast.LENGTH_SHORT).show();
        uploadToServer readService= new uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {

                relativelayout3.setVisibility(View.GONE);
                pb3.setVisibility(View.GONE);
                if(output.contains("updated"))
                {
                }
                else
                {
                    Toast.makeText(Deal.this, "Error fav"+output, Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(Deal.this, output+"", Toast.LENGTH_SHORT).show();


            }
        });readService.execute("manage_fav.php");
    }
    public void getfav()
    {
        list.clear();
        list.add(new BasicNameValuePair("dealid",id));
        list.add(new BasicNameValuePair("userid",userid));
        // Toast.makeText(this, "dealid: "+id, Toast.LENGTH_SHORT).show();
        uploadToServer readService= new uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {

                relativelayout3.setVisibility(View.GONE);
                pb3.setVisibility(View.GONE);
                if(output.contains("No fav found"))
                {
                    favourtie ="no";
                    favour.setBackgroundResource(0);
                    favour.setBackground(getResources().getDrawable(R.drawable.ic_fav_no));
                }
                else
                {
                    favourtie="yes";
                    favour.setBackgroundResource(0);
                    favour.setBackground(getResources().getDrawable(R.drawable.ic_fav_yes));

                }


            }
        });readService.execute("getfav.php");

    }

    public void delete()
    {
        list.clear();
        list.add(new BasicNameValuePair("dealid",id));
        list.add(new BasicNameValuePair("imagePath",picpath));
        uploadToServer readService= new uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {
                progressDialog.dismiss();
                if(output.contains("Deleted"))
                {

                    Toast.makeText(Deal.this,  R.string.Deleted, Toast.LENGTH_SHORT).show();
                    Deal.super.onBackPressed();
                    finish();
                }
                else
                {
                    Toast.makeText(Deal.this, output+"", Toast.LENGTH_SHORT).show();

                }


            }
        });readService.execute("delete_deal.php");
    }
    public void managelikes()
    {
        list.clear();
        list.add(new BasicNameValuePair("dealid",id));
        list.add(new BasicNameValuePair("userid",userid));
        list.add(new BasicNameValuePair("likestatus",like_status));
        uploadToServer readService= new uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {
                list.clear();
                relativelayout2.setVisibility(View.GONE);
                pb2.setVisibility(View.GONE);
                if(output.contains("error"))
                {

                    Toast.makeText(Deal.this,  R.string.error_likes, Toast.LENGTH_SHORT).show();
                }
                else
                {

                }


            }
        });readService.execute("manage_like.php");

    }


    public void getlikes()
    {
        list.clear();
        list.add(new BasicNameValuePair("dealid",id));
        list.add(new BasicNameValuePair("userid",userid));

       // Toast.makeText(this, "dealid: "+id, Toast.LENGTH_SHORT).show();
        uploadToServer readService= new uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {

                getcomments();
                relativelayout2.setVisibility(View.GONE);
                pb2.setVisibility(View.GONE);
                if(output.contains("No"))
                {
                    like_status="0";
                }
                else
                {
                     if(output.contains("-1"))
                     {
                         dislikeimg.setBackgroundResource(0);
                         dislikeimg.setBackground(getResources().getDrawable(R.drawable.ic_dislike_yes));
                         likeimg.setBackgroundResource(0);
                         likeimg.setBackground(getResources().getDrawable(R.drawable.ic_like));
                         like_status="-1";
                     }
                     else if(output.contains("1"))
                     {
                         likeimg.setBackgroundResource(0);
                         likeimg.setBackground(getResources().getDrawable(R.drawable.ic_like_yes));
                         dislikeimg.setBackgroundResource(0);
                         dislikeimg.setBackground(getResources().getDrawable(R.drawable.ic_dislike));
                         like_status="1";
                     }
                     else if(output.contains("0"))
                     {
                         like_status="0";

                     }
                }


            }
        });readService.execute("getlikes.php");
    }


    public void getcomments()
    {
        list.clear();
        list.add(new BasicNameValuePair("dealid",id));
        uploadToServer readService= new uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {

                relativeLayout.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);
                if(!output.equals(null))
                {
                    if(output.contains("No Comments found"))
                    {
                        totalcomment.setText("0");
                        //     Toast.makeText(Deal.this, "No Comments found", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        insertdata(output);
                    }
                }



            }
        });readService.execute("getcomments.php");

    }
    public void insertdata(String output)
    {

        try {
            JSONArray jsonObject = new JSONArray(output);
            for (int i=0; i<jsonObject.length(); i++)
            {

                comment_list commentList = new comment_list();
                JSONObject obj = jsonObject.getJSONObject(i);
//                View vi = layoutInfiater.inflate(R.layout.custom_comments, null);
//                pname = vi.findViewById(R.id.title);
//                comment = vi.findViewById(R.id.comment);
//                ctimestamp = vi.findViewById(R.id.timestamp);

//                pname.setText(obj.getString("name").trim());
//                comment.setText(obj.getString("comment").trim());
//                ctimestamp.setText(obj.getString("timestamp").trim());
//                linearlist.addView(vi);
                commentList.id = obj.getString("id").trim();
                commentList.full_name = obj.getString("name").trim();
                commentList.comment =obj.getString("comment").trim();
                commentList.userid =obj.getString("userid").trim();
                commentList.datentime =obj.getString("timestamp").trim();
                array_comment_list.add(commentList);
                commentList = null;

                h++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



        adapter_main.notifyDataSetChanged();
//        int b =listll.getHeight();
//
//        ViewGroup.LayoutParams params = listll.getLayoutParams();
//       // LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listll.getLayoutParams();
//        if(b<150)
//        {
//            h = h + (100*h);
//            params.height = h;
//            listll.setLayoutParams(params);
//            listll.requestLayout();
//
//        }

// Changes the height and width to the specified *pixels*



        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = h*180;



        listView.setLayoutParams(params);
        listView.requestLayout();
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
                httppost.setEntity(new UrlEncodedFormEntity(list,"UTF-8"));
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


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }




}