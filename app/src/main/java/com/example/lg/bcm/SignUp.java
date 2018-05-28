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
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
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
    public static  final int CAMERA_PERMISSION_REQUEST_CODE = 111;
    public static  final int READ_PERMISSION_REQUEST_CODE = 222;
    public static  final int WRITE_PERMISSION_REQUEST_CODE = 333;
    public static  final int CAPURE_CAMERA = 444;
    public static  final int CROP_PHOTO = 555;
    private Uri mImageCaptureUri;
    private Bitmap resizedBitmap;
    private Bitmap result_bitmap;
    private String string_byte;
    private byte[] bytes;
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
        imageView = (ImageView)findViewById(R.id.signup_imgView);

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
        Button camera_btn = (Button)findViewById(R.id.signup_camera_btn);
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String url = "tmp+" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                    int cameraPermissionResult = checkSelfPermission(Manifest.permission.CAMERA);
                    int readstoragePermissionResult = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                    int writestoragePermissionResult = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (cameraPermissionResult == PackageManager.PERMISSION_DENIED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                    }
                    if(readstoragePermissionResult== PackageManager.PERMISSION_DENIED){
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQUEST_CODE);
                    }
                    if(writestoragePermissionResult== PackageManager.PERMISSION_DENIED){
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
                    }else{
                        startActivityForResult(intent,CAPURE_CAMERA);
                    }
                }else {
                    startActivityForResult(intent, CAPURE_CAMERA);
                }
            }
        });
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
            result_bitmap = (rotate(bitmap,exifDegree));
            TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            if (!txtRecognizer.isOperational()) {
            } else {
                Frame frame = new Frame.Builder().setBitmap(result_bitmap).build();
                SparseArray items = txtRecognizer.detect(frame);
                Log.d("length",items.size()+"");
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
                    phoneindex = i;
                    phonenum = matcher.group(0);
                    mEditTextPhone.setText(phonenum);
                } else {
                    pattern = Pattern.compile(phonepattern2);
                    matcher = pattern.matcher(split_result[i]);
                    if (matcher.find()) {
                        phoneindex = i;
                        phonenum = matcher.group(0);
                        mEditTextPhone.setText(phonenum);
                    }
                }
            }
        /*Get Tel*/
            for (int i = 0; i < split_result.length; i++) {
                pattern = Pattern.compile(telpattern);
                matcher = pattern.matcher(split_result[i]);
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
                    matcher = pattern.matcher(split_result[i]);
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
