package com.example.lg.bcm;

/**
 * Created by byunseonuk on 2018-03-19.
 */

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lg.bcm.R;
import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {

    public Bitmap bitmap;
    private String ocrresult;
    private TessBaseAPI mTesss;
    private String lang ="";
    private String datapath = "";
    private TextView ocrtext;
    public Fragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_fragment2, container, false);


            ocrtext = (TextView) view.findViewById(R.id.ocrresult);
            ocrtext.setText(ocrresult);


            return view;

    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }
    public void setString(String ocrresult){
        this.ocrresult = ocrresult;
    }

}
