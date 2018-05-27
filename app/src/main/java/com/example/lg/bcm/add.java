package com.example.lg.bcm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lg.bcm.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ImageView imgview;
    String incompany ;
    String inname ;
    String inphone;
    String intel ;
    String inemail ;
    String inaddress ;
    String inimgurl ;
    String user_id;
    String from="";
    String check;
    Bitmap bitmap;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert);
        mEditTextCompany = (EditText) findViewById(R.id.editText_main_company);
        mEditTextName = (EditText) findViewById(R.id.editText_main_name);
        mEditTextPhone = (EditText) findViewById(R.id.editText_main_phone);
        mEditTextTel = (EditText) findViewById(R.id.editText_main_tel);
        mEditTextEmail = (EditText) findViewById(R.id.editText_main_email);
        mEditTextAddress = (EditText) findViewById(R.id.editText_main_address);
        imgview = (ImageView) findViewById(R.id.imgView);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        check = intent.getStringExtra("check");
        if(intent.hasExtra("from")) {
            from = intent.getStringExtra("from");
        }
        if(from.equals("ocr")){
            String ocr_result = intent.getStringExtra("ocr_result");
            String ocr_company = intent.getStringExtra("company");
            String ocr_name = intent.getStringExtra("name");
            Log.d("ocr_result",ocr_result);
            setOcr_result(ocr_result,ocr_company,ocr_name);

        }else if(from.equals("frag1")){

        }else{
            String company = intent.getStringExtra("company");
            String name = intent.getStringExtra("name");
            String phone = intent.getStringExtra("phone");
            String tel = intent.getStringExtra("tel");
            String email = intent.getStringExtra("email");
            String address = intent.getStringExtra("address");

            mEditTextCompany.setText(company);
            mEditTextName.setText(name);
            mEditTextPhone.setText(phone);
            mEditTextTel.setText(tel);
            mEditTextEmail.setText(email);
            mEditTextAddress.setText(address);
        }
        if (intent.hasExtra("img")) {
            bitmap = BitmapFactory.decodeByteArray(intent.getByteArrayExtra("img"), 0, intent.getByteArrayExtra("img").length);
            imgview.setVisibility(View.VISIBLE);
            imgview.setImageBitmap(bitmap);
        }
        Button buttonInsert = (Button)findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                incompany = mEditTextCompany.getText().toString();
                inname = mEditTextName.getText().toString();
                inphone = mEditTextPhone.getText().toString();
                intel = mEditTextTel.getText().toString();
                inemail = mEditTextEmail.getText().toString();
                inaddress = mEditTextAddress.getText().toString();
                inimgurl = mEditTextImgurl.getText().toString();
                InsertData task = new InsertData();
                task.execute(user_id,incompany,inname,inphone,intel,inemail,inaddress,inimgurl);

            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }
    private void setOcr_result(String ocr_result,String ocr_company, String ocr_name) {

        String result[] = ocr_result.split("/");
        for(int i=0;i<result.length;i++){
            Log.v("split",result[i]);
        }

         /* Pattern */
        String phonepattern = "(Moblie.?|M.?|Phone.?)?.(01[0-9]).(\\d{3,4}).(\\d{4})";
        String phonepattern2 = "(Moblie.?|M.?|Phone.?)?.([0-9]{2}).(\\d{2}).(\\d{3,4}).(\\d{4})";
        String telpattern = "(Tel|T|TEL)?.(0[0-9]{1,2}).(\\d{3,4}).(\\d{4})";
        String telpattern2 = "(Tel|T|TEL)?.([0-9]{2}).(\\d{1,2}).(\\d{3,4}).(\\d{4})";
        String emailpattern = "\\b(\\S+)+@(\\S.\\S.\\S?)+";
        int phoneindex = -1;
        int telindex = -1;
        int emailindex = -1;
        int faxindex = -1;
        Pattern pattern;
        Matcher matcher;
        String phonenum="";
        /*Get Company*/
        mEditTextCompany.setText(ocr_company);
        /*Get Name*/
        mEditTextName.setText(ocr_name);
        /*Get Email*/
        pattern = Pattern.compile(emailpattern);
        for (int i = 0; i < result.length; i++) {
            matcher = pattern.matcher(result[i]);
            if (matcher.find()) {
                emailindex = i;
                mEditTextEmail.setText(matcher.group(0));
            }
        }
        /*Get Phone*/
        for (int i = 0; i < result.length; i++) {
            pattern = Pattern.compile(phonepattern);
            matcher = pattern.matcher(result[i]);
            if (matcher.find()) {
                phoneindex = i;
                phonenum = matcher.group(0);
                mEditTextPhone.setText(phonenum);
            } else {
                pattern = Pattern.compile(phonepattern2);
                matcher = pattern.matcher(result[i]);
                if (matcher.find()) {
                    phoneindex = i;
                    phonenum = matcher.group(0);
                    mEditTextPhone.setText(phonenum);
                }
            }
        }
        /*Get Tel*/
        for (int i = 0; i < result.length; i++) {
            pattern = Pattern.compile(telpattern);
            matcher = pattern.matcher(result[i]);
            if(matcher.find()){
                int j = matcher.groupCount();

                if(j==2){
                    if(!matcher.group(0).equals(phonenum)){
                        mEditTextTel.setText(matcher.group(0));
                        telindex = i;
                    }else if(!matcher.group(1).equals(phonenum)){
                        mEditTextTel.setText(matcher.group(1));
                        telindex = i;
                    }
                }else if(j==3){
                    if(matcher.group(0).equals(phonenum)){
                        mEditTextTel.setText(matcher.group(1));
                        telindex = i;

                    }else if(matcher.group(1).equals(phonenum)){
                        mEditTextTel.setText(matcher.group(0));
                        telindex = i;

                    }else if(matcher.group(2).equals(phonenum)){
                        mEditTextTel.setText(matcher.group(0));
                        telindex = i;

                    }
                }
            }else{
                pattern = Pattern.compile(telpattern2);
                matcher = pattern.matcher(result[i]);
                int j = matcher.groupCount();
                if(j==2){
                    if(!matcher.group(0).equals(phonenum)){
                        telindex = i;
                        mEditTextTel.setText(matcher.group(0));
                    }else if(!matcher.group(1).equals(phonenum)){
                        telindex = i;
                        mEditTextTel.setText(matcher.group(1));
                    }
                }else if(j==3){
                    if(matcher.group(0).equals(phonenum)){
                        telindex = i;
                        mEditTextTel.setText(matcher.group(1));

                    }else if(matcher.group(1).equals(phonenum)){
                        telindex = i;
                        mEditTextTel.setText(matcher.group(0));

                    }else if(matcher.group(2).equals(phonenum)){
                        telindex = i;
                        mEditTextTel.setText(matcher.group(0));

                    }
                }
            }
        }
        String add_result="";
        for(int i=0;i<result.length;i++){
            if(i==emailindex|i==telindex|i==phoneindex)
                continue;
            else{
                add_result += result[i];
            }
        }
        mEditTextAddress.setText(add_result);
    }
    public boolean onCreateOptionsMenu(Menu write_menu){
        getMenuInflater().inflate(R.menu.write_menu, write_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        //저장 button 클릭시 수정한 정보들을 전송
        if(id == R.id.done){
            incompany = mEditTextCompany.getText().toString();
            inname = mEditTextName.getText().toString();
            inphone = mEditTextPhone.getText().toString();
            intel = mEditTextTel.getText().toString();
            inemail = mEditTextEmail.getText().toString();
            inaddress = mEditTextAddress.getText().toString();
            inimgurl = mEditTextImgurl.getText().toString();

            InsertData task = new InsertData();
            task.execute(user_id,incompany,inname,inphone,intel,inemail,inaddress,inimgurl);


        }
        //취소 button 클릭시 이전 화면으로 전환
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

            progressDialog.dismiss();
            Intent comintent = new Intent(add.this, MainActivity.class);
            comintent.putExtra("user_id",user_id);
            comintent.putExtra("from","add");
            startActivity(comintent);
            finish();

            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        //수정한 정보들을 서버로 전송
        protected String doInBackground(String... params) {
            String serverURL="";
            String id = (String)params[0];
            String company = (String)params[1];
            String name = (String)params[2];
            String phone = (String)params[3];
            String tel = (String)params[4];
            String email = (String)params[5];
            String address = (String)params[6];
            String imgurl = (String)params[7];

            Log.v("add 에서의 태그값은 :", name);
            if(check.equals("list")){
                serverURL = "http://192.168.1.102/bcm/insert.php";
            }else if(check.equals("mypage")){
                serverURL = "http://192.168.1.102/bcm/update.php";
            }

  
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
