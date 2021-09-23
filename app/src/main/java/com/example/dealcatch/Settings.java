package com.example.dealcatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Locale;

public class Settings extends AppCompatActivity {
    LinearLayout change_password;
    String oldpass,newpass,confirmpass,Response;
    SharedPreferences sp,notisetting;
    String email;
    ProgressDialog progressDialog;
    ImageView backarrow;
    ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
    Switch aSwitch;
    TextView english,german;
    SharedPreferences ln;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        english=findViewById(R.id.english);
        german=findViewById(R.id.german);
        ln=getSharedPreferences("lang",MODE_PRIVATE);

        String locale = getResources().getConfiguration().locale.toString();

        if(locale.equals("de"))
        {
            german.setBackgroundResource(R.drawable.language_select_right);
            english.setBackgroundResource(R.drawable.langauage_design_left);
            german.setTextColor(getResources().getColor(R.color.white));
            english.setTextColor(getResources().getColor(R.color.black));
        }
        else
        {

        }

        german.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!locale.equals("de"))
                {
                    setLocale("de");
                }

            }
        });
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!locale.equals("en"))
                {
                setLocale("en");
                }

            }
        });


        sp = this.getSharedPreferences("login", MODE_PRIVATE);
        change_password =findViewById(R.id.ll_password);
        aSwitch =findViewById(R.id.aSwitch);
        if( sp.contains("usertype"))
        {
            String v=  sp.getString("usertype","").trim();
            if (v.equals("admin"))
            {
                Intent intent = new Intent(Settings.this,Dashboard.class);
                startActivity(intent);
                finish();
            }
            else if (v.equals("user"))
            {

                email = sp.getString("email", "").trim();
                change_password.setVisibility(View.VISIBLE);

            }
        }
        else {
            change_password.setVisibility(View.GONE);
        }


        notisetting = this.getSharedPreferences("notification", MODE_PRIVATE);
        if(notisetting.contains("notification"))
        {
            String s=  notisetting.getString("notification","").trim();
            if (s.equals("yes"))
            {
                 aSwitch.setChecked(true);
            }
            else if (s.equals("no"))
            {
                aSwitch.setChecked(false);
            }
        }
        else
        {
            aSwitch.setChecked(true);
        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked == true)
                {
                    SharedPreferences.Editor edit = notisetting.edit();
                    edit.putString("notification", "yes");
                    edit.commit();
                    FirebaseMessaging.getInstance().subscribeToTopic("deal");
                    Toast.makeText(Settings.this, R.string.Notifications_on, Toast.LENGTH_SHORT).show();
                }
                else if(isChecked == false)
                {
                    SharedPreferences.Editor edit = notisetting.edit();
                    edit.putString("notification", "no");
                    edit.commit();
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("deal");
                    Toast.makeText(Settings.this, R.string.Notifications_off, Toast.LENGTH_SHORT).show();
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.Loading));
        progressDialog.setCancelable(false);

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                exit();
            }
        });



        backarrow=findViewById(R.id.backarrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Settings.super.onBackPressed();
            }
        });

    }

    public void exit()
    {


        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        LayoutInflater inflater = ((Settings) Settings.this).getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.custom_changepassword,
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
        EditText oldpasword =dialogLayout.findViewById(R.id.old_password);
        EditText newpassword =dialogLayout.findViewById(R.id.new_password);
        EditText confirmpassword =dialogLayout.findViewById(R.id.confrim_password);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                oldpass = oldpasword.getText().toString().trim();
                newpass = newpassword.getText().toString().trim();
                confirmpass = confirmpassword.getText().toString().trim();
                if (TextUtils.isEmpty(oldpass) || TextUtils.isEmpty(newpass) || TextUtils.isEmpty(confirmpass))
                {
                    Toast.makeText( Settings.this,R.string.All_Fields_are_required, Toast.LENGTH_SHORT).show();
                }
                else if ( !newpass.equals(confirmpass))
                {
                    Toast.makeText(Settings.this, R.string.Passwords_are_not_same, Toast.LENGTH_SHORT).show();

                }
                else
                {
                    list.clear();
                    list.add(new BasicNameValuePair("email",email));
                    list.add(new BasicNameValuePair("oldpassword",oldpass));
                    list.add(new BasicNameValuePair("newpassword",newpass));
                    progressDialog.show();

                  uploadToServer readService= new  uploadToServer(new ReadServiceResponse() {

                        @Override
                        public void processFinish(String output)
                        {
                            progressDialog.dismiss();
                            list.clear();

                            if(output.contains("Password changed successfully"))
                            {
                                Toast.makeText(Settings.this, R.string.Password_changed_successfully, Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                Toast.makeText(Settings.this, output+"", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });readService.execute("change_password.php");




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
    public void setLocale(String lang)
    {
        Locale myLocale = new Locale("de");
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        Intent intent = new Intent( Settings.this, Dashboard.class);
        startActivity(intent);
        finish();



//
//        Locale locale = new Locale(lang);
//        Locale.setDefault(locale);
//        Resources resources = getApplicationContext().getResources();
//        Configuration config = resources.getConfiguration();
//        config.setLocale(locale);
//        resources.updateConfiguration(config, resources.getDisplayMetrics());

//        myLocale = new Locale(lang);
//        Resources res = getResources();
//        Configuration conf = res.getConfiguration();
//
//        if (Build.VERSION.SDK_INT >= 17) {
//            conf.setLocale(myLocale);
//        } else {
//            conf.locale = myLocale;
//        }
//        res.updateConfiguration(conf, res.getDisplayMetrics());

        SharedPreferences.Editor edit = ln.edit();
        edit.putString("language", lang);
        edit.commit();

        triggerRebirth(getBaseContext());
//
//        Intent refresh = new Intent(this, SplashActivity.class);
//        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        finish();
//        startActivity(refresh);
    }
    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
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