package com.example.lg.bcm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by LG on 2018-04-17.
 */

public class Login extends AppCompatActivity{
    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_COMPANY = "company";
    private static final String TAG_NAME = "name";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_TEL = "tel";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS ="address";

    private EditText User_ID;
    private EditText User_PW;
    CheckBox autoLogin;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
   ListView mlistView;
    String mJsonString;
    String user_id;
    String user_pw;
    String name;



    public Login(){
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        User_ID = (EditText)findViewById(R.id.editText_login_id);
        User_PW = (EditText)findViewById(R.id.editText_login_pw);
        autoLogin = (CheckBox)findViewById(R.id.checkBox);


        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        //mlistView = (ListView) findViewById(R.id.listView_main_list);
        mArrayList = new ArrayList<>();

        pref = getSharedPreferences("pref", 0);
        editor = pref.edit();

        if(pref.getBoolean("auto_Login_enabled",false)){
            User_ID.setText(pref.getString("ID",""));
            User_PW.setText(pref.getString("PW",""));
            autoLogin.setChecked(true);

            user_id = User_ID.getText().toString();
            user_pw = User_PW.getText().toString();

            Login.GetData task = new Login.GetData();
            task.execute(user_id, user_pw);
        }else{
            Button buttonInsert = (Button) findViewById(R.id.button_main_login);
            buttonInsert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user_id = User_ID.getText().toString();
                    user_pw = User_PW.getText().toString();

                    Login.GetData task = new Login.GetData();
                    task.execute(user_id, user_pw);


                    /*User_ID.setText("");
                    User_PW.setText("");*/

                }
            });
        }

        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    String ID = User_ID.getText().toString();
                    String PW = User_PW.getText().toString();

                    editor.putString("ID", ID);
                    editor.putString("PW", PW);
                    editor.putBoolean("auto_Login_enabled", true);
                    editor.commit();

                }else{
                    editor.remove("ID");
                    editor.remove("PW");
                    editor.remove("auto_Login_enabled");
                    editor.clear();
                    editor.commit();
                }
            }
        });
    }


    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show( Login.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
           // mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

               // mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String user_id = (String)params[0];
            String user_pw = (String)params[1];
            String serverURL = "http://192.168.1.150/Login_getbc.php";
            String postParameters = "user_id=" + user_id + "&user_pw=" + user_pw;

            Log.v("왜 안되는것일까", user_id);
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
                String company = item.getString(TAG_COMPANY);
                name = item.getString(TAG_NAME);
                String phone = item.getString(TAG_PHONE);
                String tel = item.getString(TAG_TEL);
                String email = item.getString(TAG_EMAIL);
                String address = item.getString(TAG_ADDRESS);

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

            Log.v("sucess를 하엿는가", TAG_NAME);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

        if(name.equals(user_id)){
            Intent intent = new Intent(Login.this,MainActivity.class);
            startActivity(intent);
        }

    }


}


