package com.example.lg.bcm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class List_show extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "phptest_MainActivity";
    private static final String TAG_JSON = "delete_item";
    private static final String TAG_CHECK = "check";
    private String user_id;
    private TextView mTextcompany;
    private TextView mTextname;
    private TextView mTextphone;
    private TextView mTexttel;
    private TextView mTextemail;
    private TextView mTextaddress;
    private TextView mTextImgurl;
    private ImageView my_bc_img;
    private ImageView call_btn;
    private ImageView delete_btn;
    private Bitmap bitmap;
    String mJsonString;
    String company;
    String name;
    String phone;
    String tel;
    String email;
    String address;
    String imgurl;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_show);

        mTextcompany = (TextView) findViewById(R.id.textView_list_company);
        mTextname = (TextView) findViewById(R.id.textView_list_name);
        mTextphone = (TextView) findViewById(R.id.textView_list_phone);
        mTexttel = (TextView) findViewById(R.id.textView_list_tel);
        mTextemail = (TextView) findViewById(R.id.textView_list_email);
        mTextaddress = (TextView) findViewById(R.id.textView_list_address);
        my_bc_img = (ImageView) findViewById(R.id.my_bc_img);
        call_btn = (ImageView) findViewById(R.id.call_btn);
        delete_btn = (ImageView) findViewById(R.id.delete_btn);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        company = intent.getStringExtra("company");
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        tel = intent.getStringExtra("tel");
        email = intent.getStringExtra("email");
        address = intent.getStringExtra("address");
        imgurl = intent.getStringExtra("imgurl");

        Log.v("Listshow imgurl", imgurl);


        mTextcompany.setText(company);
        mTextname.setText(name);
        mTextphone.setText(phone);
        mTexttel.setText(tel);
        mTextemail.setText(email);
        mTextaddress.setText(address);

        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imgurl);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
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
            my_bc_img.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //편집 button을 눌렀을 경우 화면 전환 및 개인 정보 전달
        if (id == R.id.mEdit) {
            Intent intent = new Intent(List_show.this, add.class);
            intent.putExtra("user_id", user_id);
            intent.putExtra("check", "list_update");
            intent.putExtra("company", company);
            intent.putExtra("name", name);
            intent.putExtra("phone", phone);
            intent.putExtra("tel", tel);
            intent.putExtra("email", email);
            intent.putExtra("address", address);
            intent.putExtra("imgurl", imgurl);
            startActivityForResult(intent, 1);
        } else if (id == R.id.LogOut) {
            Intent intent = new Intent(List_show.this, Login.class);
            intent.putExtra("Logout", "Logout");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.call_btn:
                Intent callintent = new Intent(Intent.ACTION_CALL);
                callintent.setData(Uri.parse("tel:" + phone));
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callintent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.delete_btn:
                new deleteData().execute("http://192.168.1.102/bcm/delete_list_item.php");
                break;
        }
    }

    private class deleteData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getApplicationContext(),
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);
            if (result == null) {
                //  mTextViewResult.setText(errorString);
            } else {
                mJsonString = result;
                showResult();
            }
        }


        @Override
        //http 통신 및 해당 id에대한 정보를 가져옴
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = "id=" + user_id + "&phone=" + phone;

            //id에 대한 정보들을 DB에서 가져옴
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
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
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

    public void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            JSONObject item = jsonArray.getJSONObject(0);

            String check = item.getString(TAG_CHECK);
            switch (check) {
                case "success":

                    Intent intent = new Intent(List_show.this, MainActivity.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                    finish();
                    break;
                case "sql_error":
                    Toast.makeText(getApplicationContext(), "SQL 에러", Toast.LENGTH_SHORT).show();
                    break;
                case "delete_img_error":
                    Toast.makeText(getApplicationContext(), "서버 이미지 삭제 에러", Toast.LENGTH_SHORT).show();
                    break;

            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }
}

