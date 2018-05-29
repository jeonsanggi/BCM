package com.example.lg.bcm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ImageView imageView;

    private static final String TAG_JSON="webnautes";
    private static final String TAG_CHECK="check";
    public static  final int CAPURE_CAMERA = 444;
    public static  final int CROP_PHOTO = 555;
    private Uri mImageCaptureUri;
    private Bitmap resizedBitmap;
    private Bitmap result_bitmap;
    private String string_byte;
    private byte[] bytes;
    String mJsonString;
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;
    Spinner spinner4;
    Spinner spinner5;
    Spinner spinner6;
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
        imageView = (ImageView)findViewById(R.id.signup_imgView);
        spinner1=(Spinner)findViewById(R.id.spinner1);
        spinner2=(Spinner)findViewById(R.id.spinner2);
        spinner3=(Spinner)findViewById(R.id.spinner3);
        spinner4=(Spinner)findViewById(R.id.spinner4);
        spinner5=(Spinner)findViewById(R.id.spinner5);
        spinner6=(Spinner)findViewById(R.id.spinner6);
        spiiner_init();
        Button buttonInsert = (Button)findViewById(R.id.signup_bt);
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

                String id=mEdittextId.getText().toString();
                String password=mEditPassword.getText().toString();
                String company=spinner_position[0];
                String name=spinner_position[1];
                String phone=spinner_position[2];
                String tel=spinner_position[3];
                String email=spinner_position[4];
                String address=spinner_position[5];;

                Insert task = new Insert();
                task.execute(id,password,company,name,phone,tel,email,address);
            }
        });
        Button camera_btn = (Button)findViewById(R.id.signup_camera_btn);
        camera_btn.setOnClickListener(new View.OnClickListener() {
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
                    if(ContextCompat.checkSelfPermission(SignUp.this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED||
                            ContextCompat.checkSelfPermission(SignUp.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED||
                            ContextCompat.checkSelfPermission(SignUp.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED||
                            ContextCompat.checkSelfPermission(SignUp.this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SignUp.this,new String[]{Manifest.permission.CAMERA,
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(!(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(this,"권한을 허용해야 명함촬영이 가능합니다,",Toast.LENGTH_SHORT);
                }
        }
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
            imageView.setImageBitmap(result_bitmap);
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
                            t = t.replace("+","");
                            mEditTextTel.setText(t);

                        }
                    }
                }
            }
            Log.v("Tmptell",mEditTextTel.getText().toString());
            Log.d("Tmptell",mEditTextTel.getText().toString());
            Log.i("Tmptell",mEditTextTel.getText().toString());
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
    public void crop_photo(){
        Intent cropintent = new Intent("com.android.camera.action.CROP");
        cropintent.setDataAndType(mImageCaptureUri,"image/*");
        cropintent.putExtra("scale",true);
        cropintent.putExtra("output",mImageCaptureUri);
        startActivityForResult(cropintent, CROP_PHOTO);
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
    class Insert extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 5 * 1024 * 1024;
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
            String imgurl="url";
            String is_imgdata="no";
            if(bytes!=null) {
                imgurl = "http://192.168.1.102/bcm/users_dir/" + id + "/" + id + ".jpg";
                is_imgdata = "yes";
            }

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "****!@#*";

            String serverURL = "http://192.168.1.102/bcm/signup.php";

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                httpURLConnection.setRequestProperty("Connection","Keep-Alive");
                httpURLConnection.setRequestProperty("Content-Type","multipart/form-data; charset=utf-8; boundary="+boundary);

                DataOutputStream dos =
                        new DataOutputStream(httpURLConnection.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                if(bytes!=null) {

                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + id + ".jpg\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(bytes, 0, bytes.length);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                }
                dos.writeBytes("Content-Disposition: form-data; name=\"is_imgdata\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(is_imgdata);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"id\"" +lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(id);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"password\"" +lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(password);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"phone\"" +lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(phone);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"email\"" +lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(email);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"tel\"" +lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(tel);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"address\"" +lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(address);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"imgurl\"" +lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(imgurl);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"company\"" +lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(company);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"name\"" +lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(name);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.flush();
                dos.close();

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
                    intent.putExtra("Logout", "SingUp");
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
                case "img_upload_error":
                    Toast.makeText(getApplicationContext(),"img_upload_error 에러",Toast.LENGTH_SHORT).show();
                    break;
                case "make_directory_error":
                    Toast.makeText(getApplicationContext(),"make_directory_error 에러",Toast.LENGTH_SHORT).show();
                    break;
                case "null_value":
                    Toast.makeText(getApplicationContext(),"null_value 에러",Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }
}
