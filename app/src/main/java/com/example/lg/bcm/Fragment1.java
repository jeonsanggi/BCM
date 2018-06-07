package com.example.lg.bcm;

/**
 * Created by byunseonuk on 2018-03-19.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lg.bcm.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class Fragment1 extends Fragment {

    private final int INSERT_FRAG =1;
    //
    private static String TAG = "phptest_MainActivity";
    public static  final int CAMERA_PERMISSION_REQUEST_CODE = 111;
    public static  final int READ_PERMISSION_REQUEST_CODE = 222;
    public static  final int WRITE_PERMISSION_REQUEST_CODE = 333;
    private  Uri mImageCaptureUri;
    private static final String TAG_JSON="webnautes";
    private static final String TAG_COMPANY = "company";
    private static final String TAG_NAME = "name";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_TEL = "tel";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS ="address";
    private static final String TAG_IMGURL = "imgurl";
    private String user_id;

    private Button insert_bc_btn;
    ArrayList<HashMap<String, String>> mArrayList;
    ArrayList<ListViewItem> listViewItems;
    ListView mlistView;
    ListViewAdapter adapter;

    String mJsonString;
    //
    Context ct;
    public Fragment1( ){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =  (View) inflater.inflate(R.layout.fragment_fragment1, container, false);
        mlistView = (ListView) view.findViewById(R.id.listView_main_list);

        listViewItems = new ArrayList<ListViewItem>();
        mlistView.setAdapter(adapter);
        insert_bc_btn = (Button)view.findViewById(R.id.insert_bc_btn);
        mArrayList = new ArrayList<>();
        ct = inflater.getContext();
        GetData task = new GetData();

        task.execute("http://192.168.1.102/bcm/getBCList.php");
        insert_bc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               // Log.v("IMGURL === ", mArrayList.get(position).get("imgurl"));
                ListViewItem item = (ListViewItem) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(),List_show.class);

                intent.putExtra("user_id",user_id);
                intent.putExtra("company", item.getCompany());
                intent.putExtra("name", item.getName());
                intent.putExtra("phone", item.getPhone());
                intent.putExtra("tel", item.getTel());
                intent.putExtra("email", item.getEmail());
                intent.putExtra("address", item.getAddress());
                intent.putExtra("imgurl", item.getImgurl());
                startActivity(intent);

            }
        });

        return view;
    }
    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("");
        builder.setMessage("사진촬영을 하시겠습니까");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        intent.putExtra("user_id",user_id);
                        intent.putExtra("from","frag1");
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(),add.class);
                        intent.putExtra("user_id",user_id);
                        intent.putExtra("from","frag1");
                        intent.putExtra("imgurl","url");
                        intent.putExtra("check","list");
                        startActivity(intent);
                    }
                });
        builder.show();
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show( ct,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);

            if (result == null){

            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {
            Bundle extra = getArguments();
            user_id = extra.getString("user_id");
            String serverURL = params[0];
            String postParameters = "id=" + user_id;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);


            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String company = item.getString(TAG_COMPANY);
                String name = item.getString(TAG_NAME);
                String phone = item.getString(TAG_PHONE);
                String tel = item.getString(TAG_TEL);
                String email = item.getString(TAG_EMAIL);
                String address = item.getString(TAG_ADDRESS);
                String imgurl = item.getString(TAG_IMGURL);
                listViewItems.add(new ListViewItem(company,name,phone,tel,email,address,imgurl,user_id));

            }
            adapter = new ListViewAdapter(getContext(),user_id,listViewItems);
            mlistView.setAdapter(adapter);

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

}
