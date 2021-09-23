package com.example.dealcatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.MediaType;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Add_deal extends AppCompatActivity
{
    EditText dealname,description,link,actualprice,discountedprice;
    String getdeal,getdes,getlink,getaprice,getdprice,selecteddate="",selectedtime="",file_name,Response,ba1;
    String imgcheck="Select Picture";
    Button submit;
    ImageView backarrow;
    TextView attachfile;
    ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
     Uri filePath;
    SharedPreferences sp;
    String category ="";
    ProgressDialog progressDialog;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private int PICK_IMAGE_REQUEST = 1;
    ArrayList<String> arraycat = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Bitmap bitmap;
    Spinner spinner;
    TextView select_time,select_date;
    private SimpleDateFormat timeFormat;
    private Calendar calendar;
    TimePickerDialog timePickerDialog;
    String amPm;
    int currentHour;
    int currentMinute;
    int mYear, mMonth, mDay;
    private String URL = "https://fcm.googleapis.com/fcm/send";
    private RequestQueue mRequestQue;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deal);


        sp = this.getSharedPreferences("login", MODE_PRIVATE);
        if( sp.contains("usertype"))
        {
            String v=  sp.getString("usertype","").trim();
            if (v.equals("admin"))
            {

            }
            else if (v.equals("user"))
            {

                Intent intent = new Intent(Add_deal.this,Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        }
        dealname = findViewById(R.id.deal_name);
        description = findViewById(R.id.description);
        link = findViewById(R.id.link);
        actualprice = findViewById(R.id.actualprice);
        discountedprice = findViewById(R.id.discountedprice);
        submit = findViewById(R.id.submit);
        select_time =  findViewById(R.id.select_time);
        select_date =  findViewById(R.id.select_date);
        spinner =  findViewById(R.id.category);
        attachfile =  findViewById(R.id.attach_file);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.Loading));
        progressDialog.setCancelable(false);

        getcategory();
        file_name ="No file";
        mRequestQue = Volley.newRequestQueue(this);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(spinner.getSelectedItem() !=null)
                {
                    category=spinner.getSelectedItem().toString();
                }
                getdeal = dealname.getText().toString().trim();
                getdes = description.getText().toString().trim();
                getaprice = actualprice.getText().toString().trim();
                getdprice = discountedprice.getText().toString().trim();
                getlink = link.getText().toString().trim();

                imgcheck = attachfile.getText().toString();
                if (    TextUtils.isEmpty(getdeal) || TextUtils.isEmpty(getdes)||
                        TextUtils.isEmpty(getaprice) || TextUtils.isEmpty(getdprice)
                        || TextUtils.isEmpty(getlink)
                )
                {
                    Toast.makeText(Add_deal.this, R.string.All_Fields_are_required, Toast.LENGTH_SHORT).show();
                }
//                else if( TextUtils.isEmpty(selecteddate) || TextUtils.isEmpty(selectedtime))
//                {
//                    Toast.makeText(Add_deal.this, "Select Date and Time", Toast.LENGTH_SHORT).show();
//                }
                else  if(imgcheck.contains("Select Picture") || bitmap==null)
                {
                    Toast.makeText(Add_deal.this,R.string.Select_picture, Toast.LENGTH_SHORT).show();
                }
                else if (category.equals("Select Category") || category.equals(null))
                {
                    Toast.makeText(Add_deal.this, R.string.Select_category, Toast.LENGTH_SHORT).show();
                }
                else if (Double.parseDouble(getdprice)> Double.parseDouble(getaprice))
                {
                    Toast.makeText(Add_deal.this, R.string.discount_price, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!TextUtils.isEmpty(selecteddate) && TextUtils.isEmpty(selectedtime) )
                    {
                        selectedtime = selecteddate+" 23:59:59";
                    }
                    else if(TextUtils.isEmpty(selecteddate) && !TextUtils.isEmpty(selectedtime))
                    {
                         String currentdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            selectedtime = currentdate+" "+selectedtime;

                    }
                    else
                    {
                        selectedtime = selecteddate+" "+selectedtime;
                    }

                    if(!getlink.contains("https://"))
                    {
                        getlink = "https://"+getlink;
                    }
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    bitmap = getResizedBitmap(bitmap,400);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                    byte[] ba = bao.toByteArray();
                    ba1 = Base64.encodeToString(ba,0);
                    list.clear();
                    list.add(new BasicNameValuePair("title",getdeal));
                    list.add(new BasicNameValuePair("description",getdes));
                    list.add(new BasicNameValuePair("actualprice",getaprice));
                    list.add(new BasicNameValuePair("discountedprice",getdprice));
                    list.add(new BasicNameValuePair("link",getlink));
                    list.add(new BasicNameValuePair("category",category));

                    list.add(new BasicNameValuePair("selectedtime",selectedtime));

                    list.add(new BasicNameValuePair("ImagePath",ba1));
                    list.add(new BasicNameValuePair("ImageName",imgcheck));
                    progressDialog.setMessage(getResources().getString(R.string.Loading));
                    progressDialog.show();


                    uploadToServer readService= new  uploadToServer(new ReadServiceResponse() {

                        @Override
                        public void processFinish(String output)
                        {
                            dealname.setText("");
                            description.setText("");
                            actualprice.setText("");
                            discountedprice.setText("");
                            selectedtime="";
                            selecteddate="";
                            select_time.setText(getResources().getString(R.string.Select_Time));
                            select_date.setText(getResources().getString(R.string.Select_Date));

                            link.setText("");


                            if(output.contains("Deal uploaded"))
                            {
                                Toast.makeText(Add_deal.this, getResources().getString(R.string.deal_uploaded), Toast.LENGTH_SHORT).show();
                                progressDialog.setMessage(getResources().getString(R.string.sending_notification));
                                sendNotification();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(Add_deal.this, output+"", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });readService.execute("add_deal.php");
                }

            }
        });

        select_time.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(Add_deal.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {

                        selectedtime = hourOfDay+":"+minutes+":00";
                        if (hourOfDay >= 12) {
                            if(hourOfDay>12)
                            {
                                hourOfDay = hourOfDay-12;
                            }
                            amPm = " pm";
                        } else {
                            amPm = " am";
                        }
                        select_time.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
                    }
                }, currentHour, currentMinute, false);
                timePickerDialog.show();


            }
        });

        select_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(Add_deal.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String m,d;
                                monthOfYear = monthOfYear+1;
                                if(monthOfYear < 10)
                                {
                                    m =  "0" + monthOfYear;
                                }
                                else
                                {
                                    m=""+monthOfYear;
                                }

                                if(dayOfMonth < 10)
                                {

                                    d  = "0"+dayOfMonth;
                                }
                                else
                                {
                                    d=""+dayOfMonth;
                                }

                                selecteddate=year+"-"+m+"-"+d;
                                select_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();


            }
        });

        attachfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                requestStoragePermission();
            }
        });



        backarrow=findViewById(R.id.backarrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Add_deal.super.onBackPressed();
            }
        });

    }

    public void getcategory()
    {
        progressDialog.show();

        uploadToServer readService= new  uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {
                progressDialog.dismiss();

                if(output.contains("No details found"))
                {
                    Toast.makeText(Add_deal.this, getResources().getString(R.string.sub_cat_not), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    insertdata(output);

                }


            }
        });readService.execute("get_category.php");

    }

    public void insertdata(String output)
    {
        arraycat.add("Select Category");
        try {
            JSONArray jsonObject = new JSONArray(output);
            for (int i=0; i<jsonObject.length(); i++)
            {

                JSONObject obj = jsonObject.getJSONObject(i);
                arraycat.add(obj.getString("category").trim());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, arraycat);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);


    }



    //-------------------------------Picture Selection and uploading---------------------------------------------------//

    //method to show file choosers
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent,getResources().getString(R.string.Select_Picture)), PICK_IMAGE_REQUEST);

    }
    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();


            attachfile.setText(getFileName(filePath));

            try {
                bitmap = MediaStore.Images.Media
                        .getBitmap(this.getContentResolver(), filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            try
//            {
//
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
    //method to get the file path from uri
    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showFileChooser();
            return;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                //Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
                showFileChooser();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this,  getResources().getString(R.string.Permission_Denied), Toast.LENGTH_LONG).show();
            }
        }
    }
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
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


            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("base64", ba1));


            try {
                HttpPost httppost = new HttpPost(link);
                HttpClient httpclient = new DefaultHttpClient();
                httppost.setEntity(new UrlEncodedFormEntity(list,"UTF-8"));
                HttpResponse response = httpclient.execute(httppost);
                Response = EntityUtils.toString(response.getEntity());
            } catch (Exception e) { Log.v("log_tag", "Error in http connection " + e.toString()); }
            return Response;
        }

        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            delegate.processFinish(result);
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    //----------------------------------------------------------------


    private void sendNotification() {
        String t ="/topics/deal";

        JSONObject json = new JSONObject();
        try {
            json.put("to", t);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",getdeal);
            notificationObj.put("body",getdes);
            JSONObject extraData = new JSONObject();

            json.put("notification",notificationObj);
            json.put("data",extraData);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response)
                        {

                            progressDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MUR", "onError: "+error.networkResponse);
                    progressDialog.dismiss();
                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAOJ4K7cc:APA91bGTZ2-cqtlTFT5CuX8DE1UR24dcL6t2b1AGTFH7hXNvr9_hZY-GZKM56GC2eWE_EcPNqBFcdXIzWaGso7G4KE4-WYsX9nuTbHF5Xx0e6pq7-Uo8d3c1XeYo34kUl0UCBktlDnrp");
                    return header;
                }
            };
            mRequestQue.add(request);
        }
        catch (JSONException e)

        {
            progressDialog.dismiss();
            e.printStackTrace();
        }


    }




}