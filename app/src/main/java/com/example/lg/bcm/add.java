package com.example.lg.bcm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
    private TextView mEditTextImgurl;
    String user_id;
    String company;
    String name;
    String phone;
    String tel;
    String email;
    String address;
    String imgurl;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert);


        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        company = intent.getStringExtra("company");
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        tel = intent.getStringExtra("tel");
        email = intent.getStringExtra("email");
        address = intent.getStringExtra("address");
        imgurl = intent.getStringExtra("imgurl");

        mEditTextCompany = (EditText)findViewById(R.id.editText_main_company);
        mEditTextName = (EditText)findViewById(R.id.editText_main_name);
        mEditTextPhone = (EditText)findViewById(R.id.editText_main_phone);
        mEditTextTel = (EditText)findViewById(R.id.editText_main_tel);
        mEditTextEmail = (EditText)findViewById(R.id.editText_main_email);
        mEditTextAddress = (EditText)findViewById(R.id.editText_main_address);
        mEditTextImgurl = (TextView)findViewById(R.id.textView_main_imgurl);

        mEditTextCompany.setText(company);
        mEditTextName.setText(name);
        mEditTextPhone.setText(phone);
        mEditTextTel.setText(tel);
        mEditTextEmail.setText(email);
        mEditTextAddress.setText(address);
        mEditTextImgurl.setText(imgurl);

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
                String imgurl = mEditTextImgurl.getText().toString();
                InsertData task = new InsertData();
                task.execute(user_id,company,name,phone,tel,email,address,imgurl);

                mEditTextCompany.setText("");
                mEditTextName.setText("");
                mEditTextPhone.setText("");
                mEditTextTel.setText("");
                mEditTextEmail.setText("");
                mEditTextAddress.setText("");
                mEditTextImgurl.setText("");

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
            String company = mEditTextCompany.getText().toString();
            String name = mEditTextName.getText().toString();
            String phone = mEditTextPhone.getText().toString();
            String tel = mEditTextTel.getText().toString();
            String email = mEditTextEmail.getText().toString();
            String address = mEditTextAddress.getText().toString();
            String imgurl = mEditTextImgurl.getText().toString();
            InsertData task = new InsertData();
            task.execute(user_id,company,name,phone,tel,email,address,imgurl);

            mEditTextCompany.setText("");
            mEditTextName.setText("");
            mEditTextPhone.setText("");
            mEditTextTel.setText("");
            mEditTextEmail.setText("");
            mEditTextAddress.setText("");
            mEditTextImgurl.setText("");


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

            Toast.makeText(getApplicationContext(), "저장 완료", Toast.LENGTH_SHORT).show();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("user_id", user_id);;
            Fragment3 fragment3 = new Fragment3();
            fragment3.setArguments(bundle);

           /* transaction.replace(a, fragment3);*/
            transaction.commit();
            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {
            String id = (String)params[0];
            String company = (String)params[1];
            String name = (String)params[2];
            String phone = (String)params[3];
            String tel = (String)params[4];
            String email = (String)params[5];
            String address = (String)params[6];
            String imgurl = (String)params[7];
            Log.v("add 에서의 태그값은 :", name);

            String serverURL = "http://192.168.1.102/bcm/insert.php";
  
            String postParameters = "id="+id+"&company=" + company + "&name=" + name + "&phone=" + phone + "&tel=" + tel +"&email=" + email + "&address=" + address+"&imgurl=" + imgurl;


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
