package com.example.lg.bcm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
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
import java.util.HashMap;

/**
 * Created by byunseonuk on 2018-04-20.
 */

public class SignUp extends AppCompatActivity {
    private static String TAG = "phptest_MainActivity";

    private EditText mEdittextId;
    private EditText mEditPassword;
    private EditText mEditTextCompany;
    private EditText mEditTextName;
    private EditText mEditTextPhone;
    private EditText mEditTextTel;
    private EditText mEditTextEmail;
    private EditText mEditTextAddress;

    private static final String TAG_JSON="signupcheck";
    private static final String TAG_CHECK="check";


    String mJsonString;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        mEdittextId = (EditText)findViewById(R.id.signup_id);
        mEditPassword = (EditText)findViewById(R.id.signup_password);
        mEditTextCompany = (EditText)findViewById(R.id.signup_company);
        mEditTextName = (EditText)findViewById(R.id.signup_name);
        mEditTextPhone = (EditText)findViewById(R.id.signup_phone);
        mEditTextTel = (EditText)findViewById(R.id.signup_tel);
        mEditTextEmail = (EditText)findViewById(R.id.signup_email);
        mEditTextAddress = (EditText)findViewById(R.id.signup_address);


        Button buttonInsert = (Button)findViewById(R.id.signup_bt);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mEdittextId.getText().toString();
                String password = mEditPassword.getText().toString();
                String company = mEditTextCompany.getText().toString();
                String name = mEditTextName.getText().toString();
                String phone = mEditTextPhone.getText().toString();
                String tel = mEditTextTel.getText().toString();
                String email = mEditTextEmail.getText().toString();
                String address = mEditTextAddress.getText().toString();

                Insert task = new Insert();
                task.execute(id,password,company,name,phone,tel,email,address);
            }
        });
    }
    class Insert extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SignUp.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
            if(result==null){

            }else{
                mJsonString = result;
                check_signup();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String id = (String)params[0];
            String password = (String)params[1];
            String company = (String)params[2];
            String name = (String)params[3];
            String phone = (String)params[4];
            String tel = (String)params[5];
            String email = (String)params[6];
            String address = (String)params[7];
            String imgurl = "url";


            String serverURL = "http://192.168.1.150/signup.php";
            String postParameters = "id=" + id +"&password=" + password +"&company=" + company + "&name=" + name + "&phone=" + phone + "&tel=" + tel +"&email=" + email + "&address=" + address+ "&imgurl=" + imgurl;

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
                Log.d(TAG, "POST response code - " + responseStatusCode);

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
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
    public void check_signup(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            JSONObject item = jsonArray.getJSONObject(0);

            String check = item.getString(TAG_CHECK);
            switch (check){
                case "success":
                    Intent intent = new Intent(SignUp.this,Login.class);
                    startActivity(intent);
                    finish();
                    break;
                case "create_error":
                case "insert_error":
                    Toast.makeText(getApplicationContext(),"서버에러",Toast.LENGTH_SHORT).show();
                    break;
                case "already":
                    Toast.makeText(getApplicationContext(),"이미 있는 아이디입니다.",Toast.LENGTH_SHORT).show();
                    break;
                case "fid":
                    Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호를 확인해주세요",Toast.LENGTH_SHORT).show();
                    break;
                case "inputdata_error":
                    Toast.makeText(getApplicationContext(),"에러",Toast.LENGTH_SHORT).show();
                    break;
            }
          if(check.equals("success")){
              Intent intent = new Intent(SignUp.this,Login.class);
              startActivity(intent);
              finish();
          }
        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }
}
