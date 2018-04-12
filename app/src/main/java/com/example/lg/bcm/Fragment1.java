package com.example.lg.bcm;

/**
 * Created by byunseonuk on 2018-03-19.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.lg.bcm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Fragment1 extends Fragment {

    private final int INSERT_FRAG =1;
    //
    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS ="address";

    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;
    //
    Context ct;
    public Fragment1( ){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =  (View) inflater.inflate(R.layout.fragment_fragment1, container, false);
        mTextViewResult = (TextView) view.findViewById(R.id.textView_main_result);
        mlistView = (ListView) view.findViewById(R.id.listView_main_list);
        mArrayList = new ArrayList<>();
        ct = inflater.getContext();
        GetData task = new GetData();
        task.execute("http://192.168.58.132/getjson.php");

      //  Button bt = (Button) view.findViewById(R.id.bt_add);

     /*   bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                callFragment(INSERT_FRAG);
            }
        });*/

        return view;
    }
   /* private void callFragment(int frament_no) {

        // 프래그먼트 사용을 위해
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        switch (frament_no) {
            case 1:
                // '프래그먼트1' 호출
                Insert_frag insert_frag = new Insert_frag();
                transaction.replace(R.id.fragment_container, insert_frag);
                transaction.commit();
                break;
        }

    }*/
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
            mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {
            Bundle extra = getArguments();
            String android_num = extra.getString("android_num");
            String serverURL = params[0];
            String postParameters = "android_num=" + android_num;


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

                String id = item.getString(TAG_ID);
                String name = item.getString(TAG_NAME);
                String address = item.getString(TAG_ADDRESS);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_ADDRESS, address);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
                    ct, mArrayList, R.layout.item_list,
                    new String[]{TAG_ID, TAG_NAME, TAG_ADDRESS},
                    new int[]{R.id.textView_list_id, R.id.textView_list_name, R.id.textView_list_address}
            );

            mlistView.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}
