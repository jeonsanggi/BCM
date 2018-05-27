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
    private static final String TAG_JSON="logincheck";
    private static final String TAG_CHECK="check";


    private EditText User_ID;
    private EditText User_PW;
    CheckBox autoLogin;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ArrayList<HashMap<String, String>> mArrayList;

    String mJsonString;
    String user_id;
    String user_pw;



    public Login(){
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        User_ID = (EditText)findViewById(R.id.editText_login_id);
        User_PW = (EditText)findViewById(R.id.editText_login_pw);
        autoLogin = (CheckBox)findViewById(R.id.checkBox);

        mArrayList = new ArrayList<>();

        pref = getSharedPreferences("pref", 0);
        editor = pref.edit();


        Intent intent = getIntent();
        String Logout = intent.getStringExtra("Logout");

        Log.v("Logout========", Logout);

        if(Logout.equals("Logout")){
            editor.putBoolean("auto_Login_enabled", false);
            editor.commit();
        }

        //자동로그인을 위한 버튼이 check 되어있는지 확인
        if(pref.getBoolean("auto_Login_enabled",false)){
            User_ID.setText(pref.getString("ID",""));
            User_PW.setText(pref.getString("PW",""));
            autoLogin.setChecked(true);

            user_id = User_ID.getText().toString();
            user_pw = User_PW.getText().toString();

            Login.GetData task = new Login.GetData();
            task.execute(user_id, user_pw);
        }
        //button 클릭시 로그인 요청
        Button buttonInsert = (Button) findViewById(R.id.button_main_login);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = User_ID.getText().toString();
                user_pw = User_PW.getText().toString();

                Login.GetData task = new Login.GetData();
                task.execute(user_id, user_pw);
            }
        });
        //button 클릭시 회원가입으로 전환
        Button buttonsignup = (Button)findViewById(R.id.button_main_sign_up);
        buttonsignup.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(Login.this,SignUp.class);
                startActivity(intent);
                finish();
                }
        });
        //자동로그인을 위한 Listener
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

            }
            else{
                mJsonString =result;
                check_login();  //입력 id, pw와 DB 정보와 비교한 값을 확인
            }
        }

        @Override
        //http 통신으로 입력 id, password 전송
        protected String doInBackground(String... params) {
            String user_id = (String)params[0];
            String user_pw = (String)params[1];
            String serverURL = "http://192.168.1.102/bcm/login.php";
            String postParameters = "id=" + user_id + "&password=" + user_pw;

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
                Log.d(TAG,sb.toString().trim());
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }
    private void check_login(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            JSONObject item = jsonArray.getJSONObject(0);
            String check = item.getString(TAG_CHECK);

            switch (check){
                case "success":
                    Intent intent = new Intent(Login.this,MainActivity.class);
                    intent.putExtra("user_id",user_id);
                    startActivity(intent);
                    finish();
                    break;
                case "fsql":
                    Toast.makeText(getApplicationContext(),"서버에러",Toast.LENGTH_SHORT).show();
                    break;
                case "fpwd":
                case "fid":
                    Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호를 확인해주세요",Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }

}


