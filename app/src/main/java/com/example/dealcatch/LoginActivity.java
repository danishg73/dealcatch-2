package com.example.dealcatch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity
{
    EditText email,password;
    Button Login;
    String getemail,getpassword,Response,name,userid,usertype;
    ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
    SharedPreferences sp ;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        changeStatusBarColor();

        sp = this.getSharedPreferences("login", MODE_PRIVATE);

        if( sp.contains("usertype"))
        {
            String v=  sp.getString("usertype","").trim();
            if (v.equals("admin"))
            {

                Intent intent = new Intent(LoginActivity.this, Admindashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else if (v.equals("user"))
            {
                Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }




        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Login = findViewById(R.id.login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.Loading));
        progressDialog.setCancelable(false);
        Login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                getemail = email.getText().toString();
                getpassword = password.getText().toString();
                if (TextUtils.isEmpty(getemail) || TextUtils.isEmpty(getpassword))
                {
                    Toast.makeText(LoginActivity.this,R.string.All_Fields_are_required, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    login();
                }

            }
        });

    }
    public void login()
    {
        progressDialog.show();
        list.add(new BasicNameValuePair("email",getemail));
        list.add(new BasicNameValuePair("password",getpassword));
        uploadToServer readService= new  uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {
                list.clear();
                progressDialog.dismiss();
                if(output.contains("Wrong_Credentials"))
                {

                    Toast.makeText(LoginActivity.this, R.string.Wrong_Credentials, Toast.LENGTH_SHORT).show();
                }
                else if(output.contains("email not verified"))
                {

                    Toast.makeText(LoginActivity.this, R.string.email_not_verified, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try {
                        JSONArray jsonObject = new JSONArray(output);
                        for (int i=0; i<jsonObject.length(); i++)
                        {


                            JSONObject obj = jsonObject.getJSONObject(i);
                            userid = obj.getString("userid").trim();
                            name = obj.getString("fullname").trim();
                            usertype = obj.getString("usertype").trim();


                        }

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("email", getemail);
                    edit.putString("name",name);
                    edit.putString("userid",userid);
                    edit.putString("usertype",usertype);
                    edit.commit();
                    if(usertype.contains("admin"))
                    {
                        Intent intent = new Intent(LoginActivity.this,Admindashboard.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent(LoginActivity.this,Dashboard.class);
                        startActivity(intent);
                        finish();
                    }

                }


            }
        });readService.execute();




    }

    public void onLoginClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);

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

            String link=getResources().getString(R.string.link)+"login.php";


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

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
    }

}
