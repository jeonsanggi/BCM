package com.example.lg.bcm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class IntroActivity extends AppCompatActivity {
    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            intent.putExtra("Logout", "intro");
            startActivity(intent);
            finish();
        }
    };
    public static  final int CAMERA_PERMISSION_REQUEST_CODE = 111;
    public static  final int READ_PERMISSION_REQUEST_CODE = 222;
    public static  final int WRITE_PERMISSION_REQUEST_CODE = 333;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        ActionBar bar = getSupportActionBar();
        bar.hide();
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED||
                    ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED||
                    ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED||
                    ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CALL_PHONE},1);
            }

        }
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
    protected void onResume() {
        super.onResume();
// 다시 화면에 들어어왔을 때 예약 걸어주기
        handler.postDelayed(r, 3000); // 4초 뒤에 Runnable 객체 수행
    }

    @Override
    protected void onPause() {
        super.onPause();
// 화면을 벗어나면, handler 에 예약해놓은 작업을 취소하자
        handler.removeCallbacks(r); // 예약 취소
    }
}
