package com.example.lg.bcm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.lg.bcm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment3 extends Fragment {
    //
    private static String TAG = "phptest_MainActivity";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_COMPANY = "company";
    private static final String TAG_NAME = "name";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_TEL = "tel";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS ="address";
    private static final String TAG_IMGURL="imgurl";
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
    String mJsonString;
    Context ct;

    public Fragment3( ){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =  (View) inflater.inflate(R.layout.fragment_fragment3, container, false);
        setHasOptionsMenu(true);

        mTextcompany = (TextView)view.findViewById(R.id.textView_list_company);
        mTextname = (TextView)view.findViewById(R.id.textView_list_name);
        mTextphone = (TextView)view.findViewById(R.id.textView_list_phone);
        mTexttel = (TextView)view.findViewById(R.id.textView_list_tel);
        mTextemail = (TextView)view.findViewById(R.id.textView_list_email);
        mTextaddress = (TextView)view.findViewById(R.id.textView_list_address);
        mTextImgurl = (TextView) view.findViewById(R.id.textView_list_imgurl);
        my_bc_img = (ImageView)view.findViewById(R.id.my_bc_img);
        ct = inflater.getContext();

        Fragment3.GetData task = new Fragment3.GetData();
        task.execute("http://192.168.1.102/bcm/getMyInfo.php");


        Button buttonInsert = (Button)view.findViewById(R.id.button_nfc_write);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Nfc_Write.class);
                String Parameters = user_id +"***" + company + "***" + name + "***" + phone + "***" + tel +"***" + email + "***" + address+"***" + imgurl;

                intent.putExtra("Parameters", Parameters);
                startActivityForResult(intent, 1);

            }
        });


        return view;
    }

    public void onResume(){
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id =  item.getItemId();

        //편집 button을 눌렀을 경우 화면 전환 및 개인 정보 전달
        if (id == R.id.mEdit){
                Intent intent = new Intent(getActivity(),add.class);
                intent.putExtra("user_id",user_id);
                intent.putExtra("company", company);
                intent.putExtra("check","mypage");
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("tel", tel);
                intent.putExtra("email", email);
                intent.putExtra("address", address);
                intent.putExtra("imgurl",imgurl);
                startActivity(intent);

        }
        //로그아웃 butoon을 눌렀을 경우 로그인 페이지로 전환
        else if (id == R.id.LogOut){
            Intent intent = new Intent(getActivity(),Login.class);
            intent.putExtra("Logout", "Logout");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show( ct,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);
            if (result == null){
              //  mTextViewResult.setText(errorString);
            }
            else {
                mJsonString = result;
                showResult();
            }
        }


        @Override
        //http 통신 및 해당 id에대한 정보를 가져옴
        protected String doInBackground(String... params) {
            Bundle extra = getArguments();
            user_id = extra.getString("user_id");
            String serverURL = params[0];
            String postParameters = "id=" + user_id;

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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();
                return sb.toString().trim();
            }
            catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            byte[] deimgurl;
            Bitmap debitmap=null;
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);

                company = item.getString(TAG_COMPANY);
                name = item.getString(TAG_NAME);
                phone = item.getString(TAG_PHONE);
                tel = item.getString(TAG_TEL);
                email = item.getString(TAG_EMAIL);
                address = item.getString(TAG_ADDRESS);
                imgurl = item.getString(TAG_IMGURL);

            }

            mTextcompany.setText(company);
            mTextname.setText(name);
            mTextphone.setText(phone);
            mTexttel.setText(tel);
            mTextemail.setText(email);
            mTextaddress.setText(address);
            mTextImgurl.setText(imgurl);
            Thread mThread = new Thread(){
                @Override
                public void run(){
                    try{
                        URL url = new URL(imgurl);

                        HttpURLConnection conn= (HttpURLConnection)url.openConnection();
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
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }
}