package com.example.dealcatch;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActionBottomSheetDialog extends BottomSheetDialogFragment
        implements View.OnClickListener {
    public static final String TAG = "ActionBottomDialog";
    List<String> array_cat_list = new ArrayList<>();
    ArrayAdapter adapter;
    RelativeLayout relativeLayout2;
    ProgressBar progressBar2;
    ListView listView;
    TextView dismiss;
    private ItemClickListener mListener;
    public String Response;
    public static ActionBottomSheetDialog newInstance() {
        return new ActionBottomSheetDialog();
    }
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.categories_dropdown, container, false);

    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);


        listView = view.findViewById(R.id.list_bottom);
        dismiss = view.findViewById(R.id.dismiss);
        relativeLayout2 = view.findViewById(R.id.relativelayout2);
        progressBar2 = view.findViewById(R.id.progressBar2);



        getcategories();


        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                String itemString= array_cat_list.get(position).toString() ;
                mListener.onItemClick(itemString);
                dismiss();
            }
        });

//        view.findViewById(R.id.title).setOnClickListener(this);
//        view.findViewById(R.id.total_comments).setOnClickListener(this);
//        view.findViewById(R.id.total_dislikes).setOnClickListener(this);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener) {
            mListener = (ItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override public void onClick(View view) {
        TextView tvSelected = (TextView) view;
        mListener.onItemClick(tvSelected.getText().toString());
        dismiss();
    }
    public interface ItemClickListener {
        void onItemClick(String item);
    }

    public void getcategories()
    {
         uploadToServer readService= new  uploadToServer(new ReadServiceResponse() {

            @Override
            public void processFinish(String output)
            {
                progressBar2.setVisibility(View.GONE);
               // relativeLayout2.setVisibility(View.GONE);
                if(output.contains("No details found"))
                {
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
        array_cat_list.add("All");

        try {
            JSONArray jsonObject = new JSONArray(output);
            for (int i=0; i<jsonObject.length(); i++)
            {
                JSONObject obj = jsonObject.getJSONObject(i);
                array_cat_list.add(obj.getString("category").trim());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new ArrayAdapter<String>(getContext(), R.layout.listview, array_cat_list);
        listView.setAdapter(adapter);
//        category_list_adapter adapter = new category_list_adapter(array_cat_list,this,this);
//        listView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
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
//                httppost.setEntity(new UrlEncodedFormEntity(list));
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