package com.example.lg.bcm;


import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lg.bcm.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.googlecode.tesseract.android.TessBaseAPI;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static String TAG = "휴대폰 정보 가져오기";
    private final int FRAGMENT1 =1;
    private final int FRAGMENT3 =3;
    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
    public static  final int CAMERA_PERMISSION_REQUEST_CODE = 111;
    public static  final int READ_PERMISSION_REQUEST_CODE = 222;
    public static  final int WRITE_PERMISSION_REQUEST_CODE = 333;
    public static  final int CAPURE_CAMERA = 444;
    public static  final int CROP_PHOTO = 555;

    private  Uri mImageCaptureUri;
    private ImageView iv_UserPhoto;
    private String absoultePath;

    private ImageView bt_tab1, bt_tab2, bt_tab3;
    ///////////////////
    private Bitmap bmp;
    /////
    static TessBaseAPI sTess;
    private String language ="";
    private String datapath="";
    private String ocrresult;
    private String user_id;

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        /*마시멜로 이상 카메라 권한*/
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            int cameraPermissionResult = checkSelfPermission(Manifest.permission.CAMERA);
            int readstoragePermissionResult = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int writestoragePermissionResult = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (cameraPermissionResult == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }if (readstoragePermissionResult == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQUEST_CODE);
            }if (writestoragePermissionResult == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
            }
        }
        String from="";
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        if(intent.hasExtra("from")) {
            from = intent.getStringExtra("from");
        }

        bt_tab1 = (ImageView)findViewById(R.id.bt_tab1);
        bt_tab2 = (ImageView)findViewById(R.id.bt_tab2);
        bt_tab3 = (ImageView)findViewById(R.id.bt_tab3);


        // 탭 버튼에 대한 리스너 연결
        bt_tab1.setOnClickListener(this);
        bt_tab2.setOnClickListener(this);
        bt_tab3.setOnClickListener(this);



        // 임의로 액티비티 호출 시점에 어느 프레그먼트를 프레임레이아웃에 띄울 것인지를 정함
        if(from!=null&&from.equals("add")){
            callFragment(FRAGMENT3);
        }else if(from!=null&&from.equals("frag1")){
            startCamera();
        }else{
            callFragment(FRAGMENT1);
        }

        sTess = new TessBaseAPI();

        language = "eng";
        datapath = getFilesDir()+"/tesseract/";
        if(checkFile(new File(datapath+"tessdata/")))
        {
            sTess.init(datapath, language);
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }
        readFromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };


    }
    public void startCamera(){
        Intent fintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String url = "tmp+" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        fintent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        //권한 확인
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
                startActivityForResult(fintent,CAPURE_CAMERA);
            }
        }else {
            startActivityForResult(fintent, CAPURE_CAMERA);
        }
    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }
    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

        String text = "";
        // NFC 레코드를 읽어온다.
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        // 텍스트 인코딩
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        // Language Code
        int languageCodeLength = payload[0] & 0063;


        try {
            // 텍스트를 읽어온다
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }

        String[] array = text.split("\\*\\*\\*");

        String company = array[1];
        String name = array[2];
        String phone = array[3];
        String tel = array[4];
        String email = array[5];
        String address = array[6];
        String imgurl = array[7];


        Intent intent = new Intent(MainActivity.this,add.class);

        intent.putExtra("user_id",user_id);
        intent.putExtra("check","list");
        intent.putExtra("company", company);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("tel", tel);
        intent.putExtra("email", email);
        intent.putExtra("address", address);
        intent.putExtra("imgurl",imgurl);
        startActivityForResult(intent, 1);
    }
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        readFromIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }
    public void onPause(){
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }
    /******************************************************************************
     **********************************Disable Write*******************************
     ******************************************************************************/
    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }


    boolean checkFile(File dir)
    {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if(!dir.exists() && dir.mkdirs()) {
            copyFiles();
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            String datafilepath = datapath + "/tessdata/" + language + ".traineddata";
            File datafile = new File(datafilepath);
            if(!datafile.exists()) {
                copyFiles();
            }
        }
        return true;
    }

    void copyFiles()
    {
        AssetManager assetMgr = this.getAssets();

        InputStream is = null;
        OutputStream os = null;

        try {
            is = assetMgr.open("tessdata/"+language+".traineddata");

            String destFile = datapath + "/tessdata/" + language + ".traineddata";

            os = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            is.close();
            os.flush();
            os.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_tab1 :
                // '버튼1' 클릭 시 '프래그먼트1' 호출
                callFragment(FRAGMENT1);
                break;

            case R.id.bt_tab2 :
                // '버튼2' 클릭 시 '프래그먼트2' 호출
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startCamera();
                break;

            case R.id.bt_tab3 :
                // '버튼2' 클릭 시 '프래그먼트2' 호출
                callFragment(FRAGMENT3);
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_PERMISSION_REQUEST_CODE:
                for(int i=0;i<grantResults.length;i++) {
                    if (grantResults[i]<0){
                        Toast.makeText(MainActivity.this,"해당권한을 활서화 하셔야 이용가능합니다.",Toast.LENGTH_SHORT);
                    }
                }
                break;
            case READ_PERMISSION_REQUEST_CODE :
                for(int i=0;i<grantResults.length;i++) {
                    if (grantResults[i]<0){
                        Toast.makeText(MainActivity.this,"해당권한을 활서화 하셔야 이용가능합니다.",Toast.LENGTH_SHORT);
                    }
                }
                break;
            case WRITE_PERMISSION_REQUEST_CODE :
                for(int i=0;i<grantResults.length;i++) {
                    if (grantResults[i]<0){
                        Toast.makeText(MainActivity.this,"해당권한을 활서화 하셔야 이용가능합니다.",Toast.LENGTH_SHORT);
                    }
                }
                break;
        }
    }
    private void callFragment(int frament_no){

        // 프래그먼트 사용을 위해넘기기
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);;

        switch (frament_no){
            case 1:
                // '프래그먼트1' 호출
                Fragment1 fragment1 = new Fragment1();
                fragment1.setArguments(bundle);
                transaction.replace(R.id.fragment_container, fragment1);
                transaction.commit();
                break;

            case 3:
                // '프래그먼트2' 호출
                Fragment3 fragment3 = new Fragment3();
                fragment3.setArguments(bundle);
                transaction.replace(R.id.fragment_container, fragment3);
                transaction.commit();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String company="";
        String name="";
        String result="";
        StringBuilder stringBuilder = new StringBuilder();
        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == CAPURE_CAMERA){
            crop_photo();
        }
        if(requestCode == CROP_PHOTO) {
            Bitmap bitmap = BitmapFactory.decodeFile(mImageCaptureUri.getPath());
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
            Bitmap result_bitmap = (rotate(bitmap,exifDegree));
            int j = 0;
            TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            if (!txtRecognizer.isOperational()) {
            } else {
                Frame frame = new Frame.Builder().setBitmap(result_bitmap).build();
                SparseArray items = txtRecognizer.detect(frame);
                Log.d("length",items.size()+"");
                for (int i = 0; i < items.size(); i++) {
                    TextBlock item = (TextBlock) items.valueAt(i);
                    if (i == 0) {
                        company = item.getValue();
                    } else if (i == 1) {
                        name = item.getValue();
                    } else {
                        Log.v("Block", item.getValue());
                        for (Text line : item.getComponents()) {
                            Log.v("detect ocr ==", line.getValue());
                            j++;
                            result += line.getValue().toString() + "/";
                        }
                    }
                }
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizeBitmapImg(result_bitmap).compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            File f = new  File(mImageCaptureUri.getPath());
            if(f.exists())
                f.delete();

            Intent intent = new Intent(MainActivity.this, add.class);
            intent.putExtra("user_id",user_id);
            intent.putExtra("img",byteArray);
            intent.putExtra("from","ocr");
            intent.putExtra("check","list");
            intent.putExtra("company",company);
            intent.putExtra("name",name);
            intent.putExtra("imgurl","url");
            intent.putExtra("ocr_result",result);
            startActivity(intent);
        }
    }
    public void crop_photo(){
        Intent cropintent = new Intent("com.android.camera.action.CROP");
        cropintent.setDataAndType(mImageCaptureUri,"image/*");
        cropintent.putExtra("scale",true);
        cropintent.putExtra("output",mImageCaptureUri);
        startActivityForResult(cropintent, CROP_PHOTO);
    }
    public Bitmap resizeBitmapImg(Bitmap source){
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;

        if(width > height){
            newWidth = 800;
            newHeight = 500;
        }else{
            newWidth= 500;
            newHeight = 500;
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
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
}
