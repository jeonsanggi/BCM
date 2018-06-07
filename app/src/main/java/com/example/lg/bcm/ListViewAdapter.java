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
    private static String TAG = "phptest_MainActivity";
    private static final String TAG_JSON = "delete_item";
    private static final String TAG_CHECK = "check";
    /*TextView companyview;
    TextView nameview;
    TextView phoneview;
    TextView telview;
    TextView emailview;
    TextView addressview;
    TextView imgurlview;
    ImageView call_btn;
    ImageView delete_btn;*/
    private ArrayList<ListViewItem> listViewItems;
    String mJsonString;
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
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_list, null);
        }
        TextView companyview = (TextView) view.findViewById(R.id.textView_list_company);
        TextView nameview = (TextView) view.findViewById(R.id.textView_list_name);
        final TextView phoneview = (TextView) view.findViewById(R.id.textView_list_phone);
        TextView telview = (TextView) view.findViewById(R.id.textView_list_tel);
        TextView emailview = (TextView) view.findViewById(R.id.textView_list_email);
        TextView addressview = (TextView) view.findViewById(R.id.textView_list_address);
        final TextView imgurlview = (TextView) view.findViewById(R.id.textView_list_imgurl);

        ImageView call_btn = (ImageView) view.findViewById(R.id.list_call_btn);

        ImageView delete_btn = (ImageView) view.findViewById(R.id.list_delete_btn);


        companyview.setText(listViewItems.get(position).getCompany());
        nameview.setText(listViewItems.get(position).getName());
        phoneview.setText(listViewItems.get(position).getPhone());
        telview.setText(listViewItems.get(position).getTel());
        emailview.setText(listViewItems.get(position).getEmail());
        addressview.setText(listViewItems.get(position).getAddress());
        imgurlview.setText(listViewItems.get(position).getImgurl());
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
                task.execute(url,phoneview.getText().toString(),imgurlview.getText().toString());
            }
        });
        return view;
    }

    private class DeleteData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(context,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);
            if (result == null) {

            } else {
                mJsonString = result;
                showResult();
            }
        }
        @Override
        //http 통신 및 해당 id에대한 정보를 가져옴
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String phone = params[1];
            Log.d("phone",phone);
            Log.d("phone",user_id);
            String is_img = params[2];
            Log.d("is_img",is_img);
            String postParameters = "id=" + user_id + "&phone=" + phone+"&is_img="+is_img;

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
                    Toast.makeText(context, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,MainActivity.class);
                    intent.putExtra("user_id",user_id);
                    context.startActivity(intent);
                    break;
                case "sql_error":
                    Toast.makeText(context, "SQL 에러", Toast.LENGTH_SHORT).show();
                    break;
                case "delete_img_error":
                    Toast.makeText(context, "서버 이미지 삭제 에러", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }


}
