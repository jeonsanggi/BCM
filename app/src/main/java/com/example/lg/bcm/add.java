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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LG on 2018-04-03.
 */

public class add extends AppCompatActivity {
    private static final String TAG_JSON="webnautes";
    private static final String TAG_CHECK="check";
    private static String TAG = "phptest_MainActivity";

    private EditText mEditTextCompany;
    private EditText mEditTextName;
    private EditText mEditTextPhone;
    private EditText mEditTextTel;
    private EditText mEditTextEmail;
    private EditText mEditTextAddress;
    private TextView mEditTextImgurl;
    private ImageView imgview;
    private String inimgurl="url";
    private String old_phone="";
    private String mJsonString;
    private String incompany ;
    private String inname ;
    private String inphone;
    private String intel ;
    private String inemail ;
    private String inaddress ;
    private String user_id;
    private String from="";
    private String check="";
    private Bitmap bitmap;
    byte[] bytes;
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
        inimgurl = intent.getStringExtra("imgurl");
        if(intent.hasExtra("from")) {
            from = intent.getStringExtra("from");
        }

        if(from.equals("ocr")){
            String ocr_result = intent.getStringExtra("ocr_result");
            String ocr_company = intent.getStringExtra("company");
            String ocr_name = intent.getStringExtra("name");
            Log.d("ocr_result",ocr_result);
            setOcr_result(ocr_result,ocr_company,ocr_name);

            bytes = intent.getByteArrayExtra("img");
            bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            imgview.setImageBitmap(bitmap);
        }else if(from.equals("frag1")){
            if(!inimgurl.equals("url")) {
                draw_ing();
            }
        }else{
            String company = intent.getStringExtra("company");
            String name = intent.getStringExtra("name");
            String phone = intent.getStringExtra("phone");
            String tel = intent.getStringExtra("tel");
            String email = intent.getStringExtra("email");
            String address = intent.getStringExtra("address");
            old_phone = phone;
            mEditTextCompany.setText(company);
            mEditTextName.setText(name);
            mEditTextPhone.setText(phone);
            mEditTextTel.setText(tel);
            mEditTextEmail.setText(email);
            mEditTextAddress.setText(address);
            if(!inimgurl.equals("url")) {
                draw_ing();
            }
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
                InsertData task = new InsertData();
                task.execute(user_id,incompany,inname,inphone,intel,inemail,inaddress,inimgurl);
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }
    public void draw_ing(){
            Thread mThread = new Thread(){
                @Override
                public void run(){
                    try{
                        URL url = new URL(inimgurl);

                        HttpURLConnection conn= (HttpURLConnection)url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                        bytes = byteArrayOutputStream.toByteArray();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            mThread.start();
            try {
                mThread.join();
                imgview.setImageBitmap(bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
                phonenum = result[i].replace(" ", "");
                Log.v("matcher.group(0) ==", result[i].toString());
                mEditTextPhone.setText(phonenum.trim());
            } else {
                pattern = Pattern.compile(phonepattern2);
                matcher = pattern.matcher(result[i]);
                if (matcher.find()) {
                    phoneindex = i;
                    phonenum = result[i].replace(" ", "");
                    Log.v("matcher.group(0) ==", result[i].toString());
                    mEditTextPhone.setText(phonenum);
                }
            }
        }
        if(from.equals("ocr")&&(check.equals("list")||check.equals("list_update"))){
            inimgurl = "http://192.168.1.102/bcm/"+user_id+"/"+phonenum+".jpg";
        }else if(from.equals("ocr")&&check.equals("mypage")) {
            inimgurl = "http://192.168.1.102/bcm/"+user_id+"/"+user_id+".jpg";
        }
        /*Get Tel*/
        for (int i = 0; i < result.length; i++) {
            pattern = Pattern.compile(telpattern);
            matcher = pattern.matcher(result[i]);
            if(matcher.find()){
                int j = matcher.groupCount();
                if(j==1){
                    mEditTextTel.setText(matcher.group(0));
                }
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
                if(j==1){
                    mEditTextTel.setText(matcher.group(0));
                }
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
            if(result!=null) {
                mJsonString = result;
                check_success();
            }
            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        //수정한 정보들을 서버로 전송
        protected String doInBackground(String... params) {
            String serverURL = "";
            String id = (String) params[0];
            String company = (String) params[1];
            String name = (String) params[2];
            String phone = (String) params[3];
            String tel = (String) params[4];
            String email = (String) params[5];
            String address = (String) params[6];
            String imgurl = (String ) params[7];
            String is_imgdata="";
            String aleary_img="";
            String update_table = "user";

            Log.v("add 에서의 태그값은 :", name);
            if (check.equals("list")) {
                serverURL = "http://192.168.1.102/bcm/insert.php";
            } else if (check.equals("mypage")||check.equals("list_update")) {
                serverURL = "http://192.168.1.102/bcm/update.php";
                if(imgurl.equals("url")){
                    aleary_img = "no";
                }else{
                    aleary_img = "yes";
                }
            }

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "****!@#*";

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; charset=utf-8; boundary=" + boundary);

                DataOutputStream dos =
                        new DataOutputStream(httpURLConnection.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                if(bytes!=null) {
                    is_imgdata = "yes";
                    if(check.equals("mypage")) {
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + id + ".jpg\"" + lineEnd);
                    }else{
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + phone + ".jpg\"" + lineEnd);
                    }
                    dos.writeBytes(lineEnd);
                    dos.write(bytes, 0, bytes.length);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                }else{
                    is_imgdata = "no";
                }
                //명함 수정할때만
                if(check.equals("mypage")||check.equals("list_update")) {
                    //이미 사진이 있는지
                    dos.writeBytes("Content-Disposition: form-data; name=\"aleary_img\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(aleary_img);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    //list의 수정할 명함을 찾기위한 old_phone
                    dos.writeBytes("Content-Disposition: form-data; name=\"old_phone\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(old_phone);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                }
                dos.writeBytes("Content-Disposition: form-data; name=\"update_table\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(check);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"is_imgdata\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(is_imgdata);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(id);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"phone\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(phone);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"email\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(email);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"tel\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(tel);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"address\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(address);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"imgurl\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(imgurl);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"company\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(company);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"name\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(name);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.flush();
                dos.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
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
    public void check_success(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            JSONObject item = jsonArray.getJSONObject(0);

            String check = item.getString(TAG_CHECK);
            switch (check){
                case "success":
                    Intent intent = new Intent(add.this, MainActivity.class);
                    intent.putExtra("user_id",user_id);
                    if (check.equals("mypage")){
                        intent.putExtra("from","add");
                    }
                    startActivity(intent);
                    finish();
                    break;
                case "mysql_update_false":
                    Toast.makeText(getApplicationContext(),"정보 업데이트 실패 ",Toast.LENGTH_SHORT).show();
                    break;
                case "upload_img_false":
                    Toast.makeText(getApplicationContext(),"이미지 업로드 실패",Toast.LENGTH_SHORT).show();
                    break;
                case "delete_img_false":
                    Toast.makeText(getApplicationContext(),"기존 이미지 삭제실패",Toast.LENGTH_SHORT).show();
                    break;
                case "null_value":
                    Toast.makeText(getApplicationContext(),"빈값이 있습니다.",Toast.LENGTH_SHORT).show();
                    break;

            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }
}
