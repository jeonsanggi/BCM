package com.example.lg.bcm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment3 extends Fragment {
    //
    private static String TAG = "phptest_MainActivity";


    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_COMPANY = "company";
    private static final String TAG_NAME = "name";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_TEL = "tel";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS ="address";

    String company;
    String android_num;
    String phone;
    String tel;
    String email;
    String address;

    //private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;
    //
    Context ct;

    public Fragment3( ){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =  (View) inflater.inflate(R.layout.fragment_fragment3, container, false);
        setHasOptionsMenu(true);
        mlistView = (ListView) view.findViewById(R.id.listView_main_list);
        mArrayList = new ArrayList<>();
        ct = inflater.getContext();
        Fragment3.GetData task = new Fragment3.GetData();

        task.execute("http://192.168.1.150/getjson.php");


        return view;
    }

    public void onResume(){
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id =  item.getItemId();

        if (id == R.id.mEdit){
                Intent intent = new Intent(getActivity(),add.class);
                intent.putExtra("company", company);
                intent.putExtra("android_num", android_num);
                intent.putExtra("phone", phone);
                intent.putExtra("tel", tel);
                intent.putExtra("email", email);
                intent.putExtra("address", address);
                startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }


    private class GetData extends AsyncTask<String, Void, String>{
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
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

              //  mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {
            Bundle extra = getArguments();
            android_num = extra.getString("android_num");
            String serverURL = params[0];
            String postParameters = "&android_num=" + android_num;

            Log.v("태그", android_num);
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
                company = item.getString(TAG_COMPANY);
                String name = item.getString(TAG_NAME);
                phone = item.getString(TAG_PHONE);
                tel = item.getString(TAG_TEL);
                email = item.getString(TAG_EMAIL);
                address = item.getString(TAG_ADDRESS);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_COMPANY, company);
                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_PHONE, phone);
                hashMap.put(TAG_TEL, tel);
                hashMap.put(TAG_EMAIL, email);
                hashMap.put(TAG_ADDRESS, address);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
                    ct, mArrayList, R.layout.item_list,
                    new String[]{TAG_ID, TAG_COMPANY, TAG_NAME, TAG_PHONE, TAG_TEL, TAG_EMAIL, TAG_ADDRESS},
                    new int[]{R.id.textView_list_id, R.id.textView_list_company, R.id.textView_list_name, R.id.textView_list_phone, R.id.textView_list_tel, R.id.textView_list_email, R.id.textView_list_address}
            );

            mlistView.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}