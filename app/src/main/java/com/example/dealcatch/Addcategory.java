package com.example.dealcatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

public class Addcategory extends AppCompatActivity {
    Button add_cat;
    String category,Response;
    ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
    ProgressDialog progressDialog;
    ListView listView;
    category_list_adapter adapter;
    ImageView backarrow;
    SharedPreferences sp;
    List<category_list> array_cat_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcategory);


        sp = this.getSharedPreferences("login", MODE_PRIVATE);
        if( sp.contains("usertype"))
        {
            String v=  sp.getString("usertype","").trim();
            if (v.equals("admin"))
            {

            }
            else if (v.equals("user"))
            {

                Intent intent = new Intent(Addcategory.this,Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        }

        add_cat = findViewById(R.id.add_cat);
        listView = findViewById(R.id.listview);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.Loading));
        progressDialog.setCancelable(true);
        adapter = new category_list_adapter(array_cat_list,this,this);
        listView.setAdapter(adapter);
        getcatgoery();
        add_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                add_cat();
            }
        });

        backarrow=findViewById(R.id.backarrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Addcategory.super.onBackPressed();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Addcategory.this, position+"", Toast.LENGTH_SHORT).show();
                popup_cat(array_cat_list.get(position).category_id,array_cat_list.get(position).category,position);
                return false;
            }
        });



    }

    public void getcatgoery()
    {
        progressDialog.show();
        array_cat_list.clear();
        uploadToServer readService= new uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {
                progressDialog.dismiss();
                list.clear();
                if(output.contains("No details found"))
                {
                    Toast.makeText(Addcategory.this, R.string.No_details_found, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    insertcat(output);
                }


            }
        });readService.execute("get_category.php");
    }

    public void  insertcat(String output)
    {

        try {
            JSONArray jsonObject = new JSONArray(output);
            for (int i=0; i<jsonObject.length(); i++)
            {

                category_list categoryList = new category_list();
                JSONObject obj = jsonObject.getJSONObject(i);
                categoryList.category =obj.getString("category").trim();
                categoryList.category_id =obj.getString("id").trim();
                array_cat_list.add(categoryList);
                categoryList = null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        category_list_adapter adapter = new category_list_adapter(array_cat_list,this,this);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public void add_cat()
    {


        AlertDialog.Builder builder = new AlertDialog.Builder(Addcategory.this);
        LayoutInflater inflater = ((Addcategory) Addcategory.this).getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.custom_category,
                null);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.setView(dialogLayout, 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        WindowManager.LayoutParams wlmp = dialog.getWindow()
                .getAttributes();
        wlmp.gravity = Gravity.CENTER_VERTICAL;


        Button cancel =   dialogLayout.findViewById(R.id.cancel);
        Button submit =   dialogLayout.findViewById(R.id.submit);
        EditText cat =dialogLayout.findViewById(R.id.cat);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                category = cat.getText().toString().trim();
                if (TextUtils.isEmpty(category)  )
                {
                    Toast.makeText( Addcategory.this,R.string.All_Fields_are_required, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    list.clear();
                    list.add(new BasicNameValuePair("category",category));
                    progressDialog.show();
                    dialog.dismiss();
                    uploadToServer readService= new  uploadToServer(new ReadServiceResponse() {

                        @Override
                        public void processFinish(String output)
                        {

                            list.clear();

                            if(output.contains("Added Successfully"))
                            {
                                Toast.makeText(Addcategory.this, R.string.added_successfully, Toast.LENGTH_SHORT).show();
                                getcatgoery();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(Addcategory.this, output+"", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });readService.execute("add_category.php");

                }


            }
        });



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });


        builder.setView(dialogLayout);

        dialog.show();

    }


    public void popup_cat(String catid, String name,int pos)
    {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(Addcategory.this);
        builder1.setTitle(getResources().getString(R.string.Delete_Category));
        builder1.setMessage(getResources().getString(R.string.all_deal_will_be_deleted));
        builder1.setCancelable(true);

        builder1.setPositiveButton(getResources().getString(R.string.Delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        progressDialog.show();
                        list.clear();

                        list.add(new BasicNameValuePair("catgory_name",name));
                        list.add(new BasicNameValuePair("category_id",catid));
                        uploadToServer readService= new uploadToServer(new ReadServiceResponse() {

                            @Override
                            public void processFinish(String output)
                            {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                if(output.contains("Category Deleted"))
                                {
                                    Toast.makeText(Addcategory.this, getResources().getString(R.string.cat_deleted), Toast.LENGTH_SHORT).show();
                                    array_cat_list.remove(pos);
                                    listView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    Toast.makeText(Addcategory.this, output+"", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });readService.execute("delete_category.php");
                        // delete();
                    }
                });
        builder1.setNegativeButton(getResources().getString(R.string.Cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
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



}