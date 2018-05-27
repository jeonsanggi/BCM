package com.example.lg.bcm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class List_show extends AppCompatActivity {
    private static String TAG = "phptest_MainActivity";

    private String user_id;
    private TextView mTextcompany;
    private TextView mTextname;
    private TextView mTextphone;
    private TextView mTexttel;
    private TextView mTextemail;
    private TextView mTextaddress;
    private TextView mTextImgurl;
    private ImageView my_bc_img;
    private Bitmap bitmap;
    String company;
    String name;
    String phone;
    String tel;
    String email;
    String address;
    String imgurl;


    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fragment3);

        mTextcompany = (TextView) findViewById(R.id.textView_list_company);
        mTextname = (TextView) findViewById(R.id.textView_list_name);
        mTextphone = (TextView) findViewById(R.id.textView_list_phone);
        mTexttel = (TextView) findViewById(R.id.textView_list_tel);
        mTextemail = (TextView) findViewById(R.id.textView_list_email);
        mTextaddress = (TextView) findViewById(R.id.textView_list_address);
        my_bc_img = (ImageView) findViewById(R.id.my_bc_img);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        company = intent.getStringExtra("company");
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        tel = intent.getStringExtra("tel");
        email = intent.getStringExtra("email");
        address = intent.getStringExtra("address");
        imgurl = intent.getStringExtra("imgurl");


        mTextcompany.setText(company);
        mTextname.setText(name);
        mTextphone.setText(phone);
        mTexttel.setText(tel);
        mTextemail.setText(email);
        mTextaddress.setText(address);


        byte[] deimgurl;
        Bitmap debitmap = null;
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


    public boolean onCreateOptionsMenu(Menu menu){
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
        }

        return super.onOptionsItemSelected(item);
    }
}

