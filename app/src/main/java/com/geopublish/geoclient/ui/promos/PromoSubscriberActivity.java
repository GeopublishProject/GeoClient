package com.geopublish.geoclient.ui.promos;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geopublish.geoclient.GeoclientApplication;
import com.geopublish.geoclient.R;
import com.geopublish.geoclient.Utils;
import com.geopublish.geoclient.db.model.GeoClientDataSource;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import serialization.PromoMessage;
import serialization.UserPromotionMessage;

public class PromoSubscriberActivity extends AppCompatActivity {
    private String userName;
    private EditText txtCode;
    private Handler updateConversationHandler;
    private ObjectOutputStream oos;
    private ObjectInputStream objectInputStream;
    private boolean clickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_subscriber);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PromoSubscriberActivity.this);
        userName = sharedPreferences.getString("userName", null);

        File f = new File( getApplicationInfo().dataDir+ "/images");
        if (!f.isDirectory())
        {
            f.mkdir();

        }

        txtCode=(EditText) findViewById(R.id.txtCode);
        Button btnSubscribeToPromo= (Button) findViewById(R.id.btnSubscribeToPtomo);

        btnSubscribeToPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (clickable) {
                    if (txtCode.getText().length()==0)
                    {
                        Toast.makeText(PromoSubscriberActivity.this, "Escribe el código de la promoción",Toast.LENGTH_LONG).show();
                        txtCode.requestFocus();
                    }
                    else
                    {
                        PromoMessage promoMessage=new PromoMessage();

                        promoMessage.action=1;
                        promoMessage.deviceId = Utils.getDeviceId(PromoSubscriberActivity.this);
                        promoMessage.code=txtCode.getText().toString().trim().replace("[\n\r]", "");

                        updateConversationHandler = new Handler();

                        ConnectThread connectThread = new ConnectThread(promoMessage);
                        connectThread.start();
                    }
                }


            //}
        });

    }

    public class ConnectThread extends Thread
    {
        private PromoMessage promoMessage;

        public ConnectThread(PromoMessage promoMessage)
        {
            this.promoMessage=promoMessage;
        }

        public void run()
        {
            Socket s;
            String responseMessage="Se ha presentado un error en la aplicación. Por favor revise que se encuentre conectado(a) a la red";
            UserPromotionMessage userPromotionMessage=null;

            try {
                s = new Socket(GeoclientApplication.ServerIP, GeoclientApplication.ServerPort);

                oos = new ObjectOutputStream(s.getOutputStream());

                Log.i("Geoclient", "Flujos de entrada y salida creaados");
                //send output msg
                String outMsg = "TCP connecting to " + GeoclientApplication.ServerIP + System.getProperty("line.separator");
                Log.i("Geoclient", "Propiedades obtenidas");

                //message
                promoMessage.action=1; //register user data

                oos.writeObject(this.promoMessage);
                oos.flush();

                Log.i("TcpClient", "sent: " + outMsg);
                //accept server response
                objectInputStream= new ObjectInputStream(s.getInputStream());

                userPromotionMessage= (UserPromotionMessage) objectInputStream.readObject();

                objectInputStream.close();
                Log.i("TcpClient", "received: " + responseMessage);
                //close connection
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {

                updateConversationHandler.post(new updateUIThread(promoMessage,userPromotionMessage));
            }
        }
    }

    class updateUIThread implements Runnable {
        private UserPromotionMessage userPromotionMessage;
        private PromoMessage promoMessage;

        public updateUIThread(PromoMessage promoMessage,UserPromotionMessage userPromotionMessage) {
            this.userPromotionMessage = userPromotionMessage;
            this.promoMessage=promoMessage;
        }


        @Override
        public void run() {

            String responseMessage= "Se ha presentado un error en la aplicación. Por favor revise que se encuentre conectado(a) a la red";

            if (userPromotionMessage==null)
            {
                Toast.makeText(getBaseContext(),responseMessage, Toast.LENGTH_LONG).show();
            }
            else
            {
                switch (userPromotionMessage.result) {
                    case 100:
                        responseMessage = "El código de promoción no existe. Por favor revisa tu código";
                        break;

                    case 200:
                        responseMessage = "Este usuario ya esta suscrito a la promoción";
                        break;

                    case 300:
                        responseMessage = "La promoción relacionada con el código escrito ha expirado";
                        break;

                    case 400:
                        responseMessage = "No hay mas promociones disponibles relacionadas con este código";
                        break;
                }

                if (userPromotionMessage.result==500)
                {
                        final GeoClientDataSource geoClientDataSource= new GeoClientDataSource(PromoSubscriberActivity.this);

                        long clientId=geoClientDataSource.SaveClient(userPromotionMessage.clientName, userPromotionMessage.iconFileName );
                        geoClientDataSource.SavePromotion((int) clientId, userPromotionMessage.promoDescription, promoMessage.code, userPromotionMessage.promoExpirationDate, null, userPromotionMessage.promotionalPictureName + ".png");

                        //Save client icon
                        saveImage(userPromotionMessage.iconFileName, userPromotionMessage.clientLogo);

                        //Save promotional picture
                        if (userPromotionMessage.promotionalPicture!=null)
                            saveImage(userPromotionMessage.promotionalPictureName + ".png", userPromotionMessage.promotionalPicture);

                        //Save qrCode Format: clientName | userName | deviceId | promoCode | expirationDate
                        saveQRCode(promoMessage.code,userPromotionMessage.clientName + "|" + userName + "|" + promoMessage.deviceId + "|" + promoMessage.code + "|" + userPromotionMessage.promoExpirationDate);

                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK,returnIntent);

                        finish();
                }
                else
                {
                    Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_LONG).show();
                }
            }
            clickable = true;
        }
    }

    private void saveImage(String fileName, byte[] data)
    {
        File photo=new File(getApplicationInfo().dataDir+ "/images/", fileName);

        //Almacenamos la foto solo si esta no existe
        if (!photo.exists()) {
            try {
                FileOutputStream fos=new FileOutputStream(photo.getPath());

                fos.write(data);
                fos.close();
            }
            catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }
        }
    }

    private Bitmap saveQRCode(String promoCode,String text)
    {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            File photo=new File(getApplicationInfo().dataDir+ "/images/", promoCode + ".png");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(photo);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance

                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

}
