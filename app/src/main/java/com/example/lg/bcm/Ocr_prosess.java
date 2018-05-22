package com.example.lg.bcm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

/**
 * Created by byunseonuk on 2018-05-23.
 */

public class Ocr_prosess extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fragment2);
        Bitmap result_bitmap=null;
        TextView textView = (TextView)findViewById(R.id.ocrresult);
        String[] result=null;
        int j=0;
        Intent intent = getIntent();
        if(intent.hasExtra("bitmap")){
            result_bitmap = BitmapFactory.decodeByteArray(intent.getByteArrayExtra("bitmap"),0, intent.getByteArrayExtra("bit").length);
        }
        TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
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
        }
    }
}
