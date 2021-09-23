package com.example.dealcatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.util.Locale;

public class Splashscreen extends AppCompatActivity {

    private  static final int code=2000;
    SharedPreferences  ln;
    Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        ln=getSharedPreferences("lang",MODE_PRIVATE);
        changeStatusBarColor();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                String lan  = ln.getString("language", "");
                if(lan.equals("en") )
                {
                     myLocale = new Locale("en");
                }
                else if(lan.equals("de"))
                {
                     myLocale = new Locale("de");
                }
                else
                {
                    myLocale = new Locale("de");
                }

                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
                Intent intent = new Intent( Splashscreen.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
        },code);
    }


    public void setLocale(String lang) {


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