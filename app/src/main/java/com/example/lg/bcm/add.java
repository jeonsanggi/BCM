package com.example.lg.bcm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lg.bcm.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by LG on 2018-04-03.
 */

public class add extends AppCompatActivity {

    private static String TAG = "phptest_MainActivity";

    private EditText mEditTextCompany;
    private EditText mEditTextName;
    private EditText mEditTextPhone;
    private EditText mEditTextTel;
    private EditText mEditTextEmail;
    private EditText mEditTextAddress;
    private TextView mTextViewResult;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert);

        mEditTextCompany = (EditText)findViewById(R.id.editText_main_company);
        mEditTextName = (EditText)findViewById(R.id.editText_main_name);
        mEditTextPhone = (EditText)findViewById(R.id.editText_main_phone);
        mEditTextTel = (EditText)findViewById(R.id.editText_main_tel);
        mEditTextEmail = (EditText)findViewById(R.id.editText_main_email);
        mEditTextAddress = (EditText)findViewById(R.id.editText_main_address);
        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);


        Button buttonInsert = (Button)findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String company = mEditTextCompany.getText().toString();
                String name = mEditTextName.getText().toString();
                String phone = mEditTextPhone.getText().toString();
                String tel = mEditTextTel.getText().toString();
                String email = mEditTextEmail.getText().toString();
                String address = mEditTextAddress.getText().toString();

                InsertData task = new InsertData();
                task.execute(name,address);

                mEditTextCompany.setText("");
                mEditTextName.setText("");
                mEditTextPhone.setText("");
                mEditTextTel.setText("");
                mEditTextEmail.setText("");
                mEditTextAddress.setText("");

            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    public boolean onCreateOptionsMenu(Menu write_menu){
        getMenuInflater().inflate(R.menu.write_menu, write_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.done){
            Toast.makeText(getApplicationContext(), "저장 완료", Toast.LENGTH_SHORT).show();
            Intent intent = getIntent();
            setResult(1, intent);
            finish();
        }
        else if( id == R.id.cancel) {
            Intent intent = getIntent();
            setResult(0, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(add.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String company = (String)params[0];
            String name = (String)params[1];
            String phone = (String)params[2];
            String tel = (String)params[3];
            String email = (String)params[4];
            String address = (String)params[5];

            String serverURL = "http://192.168.1.34/insert.php";
              String postParameters = "&company=" + company + "name=" + name + "phone=" + phone + "&tel=" + tel +"email=" + email + "&address=" + address;


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


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
