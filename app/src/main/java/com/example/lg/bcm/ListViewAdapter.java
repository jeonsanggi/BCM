package com.example.lg.bcm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by byunseonuk on 2018-06-04.
 */

public class ListViewAdapter extends BaseAdapter {
    TextView companyview;
    TextView nameview;
    TextView phoneview;
    TextView telview;
    TextView emailview;
    TextView addressview;
    TextView imgurlview;
    ImageView call_btn;
    ImageView delete_btn;
    private ArrayList<ListViewItem> listViewItems;
    String user_id;
    Context context;

    public ListViewAdapter(Context context, String user_id, ArrayList<ListViewItem> listViewItems) {
        this.listViewItems = listViewItems;
        this.user_id = user_id;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.listViewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        int pos = position;
        if (view == null) {
            /*LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list,null);*/
            view = LayoutInflater.from(context).inflate(R.layout.item_list, null);
        }
        companyview = (TextView) view.findViewById(R.id.textView_list_company);
        nameview = (TextView) view.findViewById(R.id.textView_list_name);
        phoneview = (TextView) view.findViewById(R.id.textView_list_phone);
        telview = (TextView) view.findViewById(R.id.textView_list_tel);
        emailview = (TextView) view.findViewById(R.id.textView_list_email);
        addressview = (TextView) view.findViewById(R.id.textView_list_address);
        imgurlview = (TextView) view.findViewById(R.id.textView_list_imgurl);

        call_btn = (ImageView) view.findViewById(R.id.list_call_btn);

        delete_btn = (ImageView) view.findViewById(R.id.list_delete_btn);

        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callintent = new Intent(Intent.ACTION_CALL);
                callintent.setData(Uri.parse("tel:" + phoneview.getText().toString()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(callintent);
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteData task = new DeleteData();
                String url = "http://192.168.1.102/bcm/delete_list_item.php";
                task.execute(url,phoneview.getText().toString());
            }
        });
        companyview.setText(listViewItems.get(position).getCompany());
        nameview.setText(listViewItems.get(position).getName());
        phoneview.setText(listViewItems.get(position).getPhone());
        telview.setText(listViewItems.get(position).getTel());
        emailview.setText(listViewItems.get(position).getEmail());
        addressview.setText(listViewItems.get(position).getAddress());
        imgurlview.setText(listViewItems.get(position).getImgurl());

        return view;
    }
    /*public void addItem(String user_id, String company,String name,String phone,String tel,String email,String address,String imgurl){
        ListViewItem item = new ListViewItem();
        item.setCompany(company);
        item.setName(name);
        item.setPhone(phone);
        item.setTel(tel);
        item.setEmail(email);
        item.setAddress(address);
        item.setImgurl(imgurl);
        item.setUser_id(user_id);
        listViewItems.add(item);
    }*/
    private class DeleteData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /*progressDialog = ProgressDialog.show(this,
                    "Please Wait", null, true, true);*/
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            if (result == null) {
                //  mTextViewResult.setText(errorString);
            } else {
                //mJsonString = result;
            }
        }


        @Override
        //http 통신 및 해당 id에대한 정보를 가져옴
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            //String postParameters = "id=" + user_id + "&phone=" + phone;

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
                //outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();


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

                errorString = e.toString();

                return null;
            }
        }
    }


}
