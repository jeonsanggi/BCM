package com.example.lg.bcm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
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
    public static  final int CAPURE_CAMERA = 444;
    public static  final int CROP_PHOTO = 555;
    private EditText mEditTextCompany;
    private EditText mEditTextName;
    private EditText mEditTextPhone;
    private EditText mEditTextTel;
    private EditText mEditTextEmail;
    private EditText mEditTextAddress;
    private TextView mEditTextImgurl;
    private Button capture_btn;
    private Spinner spinner1;
    private Spinner spinner2;
    private Spinner spinner3;
    private Spinner spinner4;
    private Spinner spinner5;
    private Spinner spinner6;
    private ImageView imgview;
    private String inimgurl="url";
    private String old_phone="";
    private String mJsonString;
    private String user_id;
    private String from="";
    private String check="";
    private Bitmap bitmap;
    private Uri mImageCaptureUri;
    private Bitmap result_bitmap;
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
        capture_btn = (Button) findViewById(R.id.recapture_camera_btn);
        imgview = (ImageView) findViewById(R.id.imgView);
        spinner1=(Spinner)findViewById(R.id.spinner1);
        spinner2=(Spinner)findViewById(R.id.spinner2);
        spinner3=(Spinner)findViewById(R.id.spinner3);
        spinner4=(Spinner)findViewById(R.id.spinner4);
        spinner5=(Spinner)findViewById(R.id.spinner5);
        spinner6=(Spinner)findViewById(R.id.spinner6);
        //spiner init set
        spiiner_init();
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
                String[] spinner_position = new String[6];
                spinner_position[spinner1.getSelectedItemPosition()]=mEditTextCompany.getText().toString();
                spinner_position[spinner2.getSelectedItemPosition()]=mEditTextName.getText().toString();
                spinner_position[spinner3.getSelectedItemPosition()]=mEditTextPhone.getText().toString();
                spinner_position[spinner4.getSelectedItemPosition()]=mEditTextTel.getText().toString();
                spinner_position[spinner5.getSelectedItemPosition()]=mEditTextEmail.getText().toString();
                spinner_position[spinner6.getSelectedItemPosition()]=mEditTextAddress.getText().toString();

                String company=spinner_position[0];
                String name=spinner_position[1];
                String phone=spinner_position[2];
                String tel=spinner_position[3];
                String email=spinner_position[4];
                String address=spinner_position[5];

                if(from.equals("ocr")&&(check.equals("list")||check.equals("list_update"))){
                    inimgurl = "http://172.20.10.13/bcm/users_dir/"+user_id+"/"+phone+".jpg";
                }else if(from.equals("ocr")&&check.equals("mypage")) {
                    inimgurl = "http://172.20.10.13/bcm/users_dir/"+user_id+"/"+user_id+".jpg";
                }
                InsertData task = new InsertData();
                task.execute(user_id,company,name,phone,tel,email,address,inimgurl);
            }
        });
        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //기본 내장 되어있는 카메라를 사용하기위한 INTENT
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String url = "tmp+" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                //사진촬영시 원본파일 임시저장 경로 받아오기
                mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                //카메라, 외부저장소 읽기/쓰기, 전화 권한 검사 및 획득
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                    if(ContextCompat.checkSelfPermission(add.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED||
                            ContextCompat.checkSelfPermission(add.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED||
                            ContextCompat.checkSelfPermission(add.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED||
                            ContextCompat.checkSelfPermission(add.this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(add.this,new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CALL_PHONE},1);
                    }
                    // 카메라 실행
                    startActivityForResult(intent,CAPURE_CAMERA);
                }else {
                    startActivityForResult(intent, CAPURE_CAMERA);
                }
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    void spiiner_init(){
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.option,android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setSelection(0);
        spinner2.setAdapter(adapter);
        spinner2.setSelection(1);
        spinner3.setAdapter(adapter);
        spinner3.setSelection(2);
        spinner4.setAdapter(adapter);
        spinner4.setSelection(3);
        spinner5.setAdapter(adapter);
        spinner5.setSelection(4);
        spinner6.setAdapter(adapter);
        spinner6.setSelection(5);
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
        String phonepattern = "(Moblie.?|M.?|Phone.?)?+(01[0-9]).(\\d{3,4}).(\\d{4})";
        String phonepattern2 = "(Moblie.?|M.?|Phone.?)?+([0-9]{2}).(\\d{2}).(\\d{3,4}).(\\d{4})";
        String telpattern = "(Tel.?|T.?|TEL.?)?+(0[0-9]{1,2}).(\\d{3,4}).(\\d{4})";
        String telpattern2 = "(Tel.?|T.?|TEL.?)?+([0-9]{2}).(\\d{1,2}).(\\d{3,4}).(\\d{4})";
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
        String phonenum1="";
        for (int i = 0; i < result.length; i++) {
            pattern = Pattern.compile(phonepattern);
            matcher = pattern.matcher(result[i]);
            if (matcher.find()) {
                phoneindex = i;
                phonenum1 =matcher.group(0);
                phonenum = matcher.group(0).replace(" ", "");
                phonenum = phonenum.replace("+", "");
                        Log.v("matcher.group(0) ==", result[i].toString());
                mEditTextPhone.setText(phonenum);
            } else {
                pattern = Pattern.compile(phonepattern2);
                matcher = pattern.matcher(result[i]);
                if (matcher.find()) {
                    phoneindex = i;
                    phonenum1 =matcher.group(0);
                    phonenum = matcher.group(0).replace(" ", "");
                    //phonenum = phonenum.replace("+", "");
                    Log.v("matcher.group(0) ==", result[i].toString());
                    mEditTextPhone.setText(phonenum);
                }
            }
        }
        String t="";
        String tmptell="";
        for (int i = 0; i < result.length; i++) {
            pattern = Pattern.compile(telpattern);
            matcher = pattern.matcher(result[i]);
            if(matcher.find()){
                while(matcher.find()) {
                    if (!matcher.group(0).equals(phonenum1)) {
                        telindex = i;
                        t = matcher.group().replace(" ", "");
                        t = t.replace("+", "");
                        mEditTextTel.setText(t);
                    }
                }
            }else{
                int j=0;
                pattern = Pattern.compile(telpattern2);
                matcher = pattern.matcher(result[i]);
                while(matcher.find()){
                    Log.v("matcher.group(0) ==", result[i].toString());
                    Log.v("matcher.group(0) ==", matcher.group(0));
                    if(!matcher.group(0).equals(phonenum1)){
                        telindex = i;
                        t = matcher.group().replace(" ","");
                        t = t.replace("+","");
                        mEditTextTel.setText(t);

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
        switch (id){
            case R.id.done:
                String[] spinner_position = new String[6];
                spinner_position[spinner1.getSelectedItemPosition()]=mEditTextCompany.getText().toString();
                spinner_position[spinner2.getSelectedItemPosition()]=mEditTextName.getText().toString();
                spinner_position[spinner3.getSelectedItemPosition()]=mEditTextPhone.getText().toString();
                spinner_position[spinner4.getSelectedItemPosition()]=mEditTextTel.getText().toString();
                spinner_position[spinner5.getSelectedItemPosition()]=mEditTextEmail.getText().toString();
                spinner_position[spinner6.getSelectedItemPosition()]=mEditTextAddress.getText().toString();

                String company=spinner_position[0];
                String name=spinner_position[1];
                String phone=spinner_position[2];
                String tel=spinner_position[3];
                String email=spinner_position[4];
                String address=spinner_position[5];
                if(from.equals("ocr")&&(check.equals("list")||check.equals("list_update"))){
                    inimgurl = "http://172.20.10.13/bcm/users_dir/"+user_id+"/"+phone+".jpg";
                }else if(from.equals("ocr")&&check.equals("mypage")) {
                    inimgurl = "http://172.20.10.13/bcm/users_dir/"+user_id+"/"+user_id+".jpg";
                }
                InsertData task = new InsertData();
                task.execute(user_id,company,name,phone,tel,email,address,inimgurl);
                return true;
            case R.id.cancel :
                Intent intent = getIntent();
                setResult(0, intent);
                finish();
                return true;

            case android.R.id.home:
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
                serverURL = "http://172.20.10.13/bcm/insert.php";
            } else if (check.equals("mypage")||check.equals("list_update")) {
                serverURL = "http://172.20.10.13/bcm/update.php";
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

                URL url = new URL(serverURL); //서버 URL CLASS 생성
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //URL연결
                httpURLConnection.setDoOutput(true);         // 쓰기 모드 True
                httpURLConnection.setUseCaches(false);       // 캐싱 데이터 사용 False
                httpURLConnection.setRequestMethod("POST");  // POST방식 통신
                httpURLConnection.setDoInput(true);          // 읽기 모드 True
                //Request Header 설정
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
                        imgurl = "http://172.20.10.13/bcm/users_dir/"+user_id+"/"+user_id+".jpg";
                    }else{
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + phone + ".jpg\"" + lineEnd);
                        imgurl = "http://172.20.10.13/bcm/users_dir/"+user_id+"/"+phone+".jpg";
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
    public void crop_photo(){
        Intent cropintent = new Intent("com.android.camera.action.CROP");
        cropintent.setDataAndType(mImageCaptureUri,"image/*");
        cropintent.putExtra("scale",true);
        cropintent.putExtra("output",mImageCaptureUri);
        startActivityForResult(cropintent, CROP_PHOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result="";

        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == CAPURE_CAMERA){
            crop_photo();
        }
        if(requestCode == CROP_PHOTO) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(mImageCaptureUri.getPath(),options);
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(mImageCaptureUri.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegrees(exifOrientation);
            } else {
                exifDegree = 0;
            }
            //임시저장된 원본파일을 bitmap으로 변환 및 회전
            result_bitmap = (rotate(bitmap,exifDegree));
            //구글에서 재공하는 vision API중 TextRecognizer를 이용하여 글자 추출
            TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            if (!txtRecognizer.isOperational()) {
            } else {
                //Fram에 촬영한 사진 set
                Frame frame = new Frame.Builder().setBitmap(result_bitmap).build();
                // TextRecognizer로 추출한 TEXTBLOCK들을 SparseArray에 저장
                SparseArray items = txtRecognizer.detect(frame);
                //각 블럭들을 라인으로 나눔
                for (int i = 0; i < items.size(); i++) {
                    TextBlock item = (TextBlock) items.valueAt(i);
                    if (i == 0) {
                        mEditTextCompany.setText(item.getValue());
                    } else if (i == 1) {
                        mEditTextName.setText(item.getValue());
                    } else {
                        Log.v("Block", item.getValue());
                        for (Text line : item.getComponents()) {
                            Log.v("detect ocr ==", line.getValue());
                            result += line.getValue().toString() + "/";
                        }
                    }
                }
            }

            String[] split_result = result.split("/");
            imgview.setImageBitmap(result_bitmap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            result_bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();

            /* Pattern */
            String phonepattern = "(Moblie.?|M.?|Phone.?)?.(01[0-9]).(\\d{3,4}).(\\d{4})";
            String phonepattern2 = "(Moblie.?|M.?|Phone.?)?.([0-9]{2}).(\\d{2}).(\\d{3,4}).(\\d{4})";
            String telpattern = "(Tel|T|TEL)?.(0[0-9]{1,2}).(\\d{3,4}).(\\d{4})";
            String telpattern2 = "(Tel|T|TEL)?.([0-9]{2}).(\\d{1,2}).(\\d{3,4}).(\\d{4})";
            String emailpattern = "\\b(\\S+)+@(\\S.\\S.\\S?)+";
            int phoneindex = -1;
            int telindex = -1;
            int emailindex = -1;
            Pattern pattern;
            Matcher matcher;
            String phonenum="";
            String phonenum1="";

        /*Get Email*/
            pattern = Pattern.compile(emailpattern);
            for (int i = 0; i < split_result.length; i++) {
                matcher = pattern.matcher(split_result[i]);
                if (matcher.find()) {
                    emailindex = i;
                    mEditTextEmail.setText(matcher.group(0));
                }
            }
        /*Get Phone*/
            for (int i = 0; i < split_result.length; i++) {
                pattern = Pattern.compile(phonepattern);
                matcher = pattern.matcher(split_result[i]);
                if (matcher.find()) {
                    while(matcher.find()) {
                        phoneindex = i;
                        phonenum1 = matcher.group(0);
                        phonenum = matcher.group(0).replace(" ", "");
                        phonenum = phonenum.replace("-","");
                        phonenum = phonenum.replace("+", "");
                        mEditTextPhone.setText(phonenum);
                    }
                } else {
                    pattern = Pattern.compile(phonepattern2);
                    matcher = pattern.matcher(split_result[i]);
                    while (matcher.find()) {
                        phoneindex = i;
                        phonenum1 =matcher.group(0);
                        phonenum = matcher.group(0).replace(" ","");
                        phonenum = phonenum.replace("-","");
                        phonenum = phonenum.replace("+", "");
                        mEditTextPhone.setText(phonenum);
                    }
                }
            }
            String t="";
            String tmptell="";
            for (int i = 0; i < split_result.length; i++) {
                pattern = Pattern.compile(telpattern);
                matcher = pattern.matcher(split_result[i]);
                if(matcher.find()){
                    while(matcher.find()) {
                        if (!matcher.group(0).equals(phonenum1)) {
                            telindex = i;
                            t = matcher.group().replace(" ", "");
                            t = t.replace("-", "");
                            t = t.replace("+", "");
                            mEditTextTel.setText(t);
                        }
                    }
                }else{
                    int j=0;
                    pattern = Pattern.compile(telpattern2);
                    matcher = pattern.matcher(split_result[i]);
                    while(matcher.find()){
                        Log.v("matcher.group(0) ==", split_result[i].toString());
                        Log.v("matcher.group(0) ==", matcher.group(0));
                        if(!matcher.group(0).equals(phonenum1)){
                            telindex = i;
                            t = matcher.group().replace(" ","");
                            t = t.replace("-", "");
                            t = t.replace("+","");
                            mEditTextTel.setText(t);

                        }
                    }
                }
            }
            String add_result="";
            for(int i=0;i<split_result.length;i++){
                if(i==emailindex|i==telindex|i==phoneindex)
                    continue;
                else{
                    add_result += split_result[i];
                }
            }
            mEditTextAddress.setText(add_result);
            File f = new  File(mImageCaptureUri.getPath());
            if(f.exists())
                f.delete();
        }
    }
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    public void check_success(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            JSONObject item = jsonArray.getJSONObject(0);

            String tag_check = item.getString(TAG_CHECK);
            switch (tag_check){
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
