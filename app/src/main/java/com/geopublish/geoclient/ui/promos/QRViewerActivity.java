package com.geopublish.geoclient.ui.promos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.geopublish.geoclient.R;
import com.geopublish.geoclient.db.model.GeoClientDataSource;

import java.io.File;

public class QRViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrviewer);

        Bundle b = getIntent().getExtras();

        if (b!=null)
        {
            String promoClientName=b.getString("clientName");
            String promoCode= b.getString("promoCode");
            String promotionalPictureName= b.getString("promotionalPictureName");

            File promotionalImgFile = new File(getApplicationInfo().dataDir+ "/images/", promotionalPictureName);
            File imgFile = new File(getApplicationInfo().dataDir+ "/images/", promoCode + ".png");
            TextView txtClientName= (TextView)findViewById(R.id.txtClientName);

            txtClientName.setText(promoClientName);

            if (promotionalImgFile.exists())
            {
                Bitmap bitmapImage = BitmapFactory.decodeFile(promotionalImgFile.getAbsolutePath());
                Drawable drawableImage = new BitmapDrawable(bitmapImage);

                FrameLayout frame=(FrameLayout)findViewById(R.id.frame);
                frame.setBackground(drawableImage);
            }

            if(imgFile.exists()){
                ImageView imgQRCode=(ImageView)findViewById(R.id.imgQRCodeViewer);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                imgQRCode.setImageBitmap(myBitmap);
            }

            GeoClientDataSource geoClientDataSource=new GeoClientDataSource(this);
            geoClientDataSource.incrementQRViews(promoCode);
        }

    }

    @Override
    protected void onStop(){
        super.onStop();

    }
}
