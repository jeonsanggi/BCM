package com.example.lg.bcm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;

/**
 * Created by byunseonuk on 2018-05-23.
 */

public class Ocr_prosess extends AppCompatActivity{
    private String filename = "tmpfile";
    String uri;
    Uri pathUri;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fragment2);
        TextView textView = (TextView)findViewById(R.id.ocrresult);
        String[] result=null;
        int j=0;
        Intent intent = getIntent();
        uri = intent.getStringExtra("uri");
        pathUri = Uri.parse(uri);


        Bitmap result_bitmap = BitmapFactory.decodeFile(pathUri.getPath());
        ImageView imageView = (ImageView)findViewById(R.id.test);
        imageView.setImageBitmap(result_bitmap);
        /*TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!txtRecognizer.isOperational()) {
        } else {
            Frame frame = new Frame.Builder().setBitmap(result_bitmap).build();
            SparseArray items = txtRecognizer.detect(frame);
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = (TextBlock) items.valueAt(i);
                strBuilder.append(item.getValue());
                strBuilder.append("/");
                for (Text line : item.getComponents()) {
                   result[j] = line.getValue();
                   j++;
                }
            }
            textView.setText(strBuilder.toString().substring(0, strBuilder.toString().length() - 1));
        }*/
    }
}
