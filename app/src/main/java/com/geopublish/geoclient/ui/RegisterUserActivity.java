package com.geopublish.geoclient.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import com.geopublish.geoclient.GeoclientApplication;
import com.geopublish.geoclient.R;
import com.geopublish.geoclient.Utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import serialization.MessageData;

public class RegisterUserActivity extends AppCompatActivity {

    private EditText txtName;
    private EditText txtDays;
    private EditText txtMonths;
    private EditText txtYears;
    private Button btnSend;
    private Spinner spinnerOccupation;
    private Handler updateConversationHandler;
    private ObjectOutputStream oos;
    private DataInputStream dataInputStream;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegisterUserActivity.this);

        if (sharedPreferences.contains("userName"))
        {
            finish();

            Intent intent = new Intent(RegisterUserActivity.this,MenuActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_register);

        txtName=(EditText) findViewById(R.id.txtName);
        txtDays=(EditText) findViewById(R.id.txtDay);
        txtMonths=(EditText) findViewById(R.id.txtMonth);
        txtYears=(EditText) findViewById(R.id.txtYear);
        btnSend=(Button) findViewById(R.id.btnRegisterUser);
        spinnerOccupation = (Spinner) findViewById(R.id.ddlOccupations);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areDataValid())
                {
                    /*
                    Intent intent = new Intent(RegisterUserActivity.this,MenuActivity.class);
                    startActivity(intent);
                    */

                    MessageData messageData=new MessageData();

                    messageData.Action=1;
                    messageData.deviceId = Utils.getDeviceId(RegisterUserActivity.this);
                    messageData.fullName = txtName.getText().toString();
                    messageData.bornDate = txtDays.getText() + "/" + txtMonths.getText() + "/" +  txtYears.getText();
                    messageData.ocupation = spinnerOccupation.getSelectedItem().toString();

                    updateConversationHandler = new Handler();

                    ConnectThread connectThread = new ConnectThread(messageData);
                    connectThread.start();

                }
            }
        });

    }

    public class ConnectThread extends Thread
    {
        private MessageData messageData;

        public ConnectThread(MessageData messageData)
        {
            this.messageData=messageData;
        }

        public void run()
        {
            Socket s;
            String responseMessage="No pudimos procesar tu solicitud. Por vafor verifica que estas conectado a la red GeoPublish";
            try {
                s = new Socket(GeoclientApplication.ServerIP, GeoclientApplication.ServerPort);

                oos = new ObjectOutputStream(s.getOutputStream());
                dataInputStream= new DataInputStream(s.getInputStream());

                //message
                messageData.Action=1; //register user data

                oos.writeObject(this.messageData);
                oos.flush();

                //accept server response
                int response = dataInputStream.readInt(); //.readLine() + System.getProperty("line.separator");

                if (response==0) responseMessage="No es posible registrar el usuario"; else responseMessage=null;

                //close connection
                oos.close();
                dataInputStream.close();
                //s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                updateConversationHandler.post(new updateUIThread(responseMessage));
            }
        }
    }

    class updateUIThread implements Runnable {
        private String msg;
        public updateUIThread(String message) {
            this.msg = message;
        }

        @Override
        public void run() {
            if (msg==null)
            {
                //Save preferences
                //TODO: Preguntar si es los settings existen entonces no  se tienen que escribir nuevamente. Aqui se estan sobreescribiendo
                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(RegisterUserActivity.this);
                SharedPreferences.Editor editor=sharedPreferences.edit();

                editor.putString("userName", txtName.getText().toString());
                editor.apply();

                finish();

                Intent intent = new Intent(RegisterUserActivity.this,MenuActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean areDataValid()
    {
        if (txtName.getText().length()==0)
        {
            Toast.makeText(RegisterUserActivity.this, "Digite el nombre o usuario",Toast.LENGTH_LONG).show();
            txtName.requestFocus();
            return false;
        }

        if (txtDays.getText().length()==0)
        {
            Toast.makeText(RegisterUserActivity.this, "Digite el dia de su nacimiento",Toast.LENGTH_LONG).show();
            txtDays.requestFocus();
            return false;
        }

        if (txtMonths.getText().length()==0)
        {
            Toast.makeText(RegisterUserActivity.this, "Digite el mes de su nacimiento",Toast.LENGTH_LONG).show();
            txtMonths.requestFocus();
            return false;
        }

        if (txtYears.getText().length()==0)
        {
            Toast.makeText(RegisterUserActivity.this, "Digite el año de su nacimiento",Toast.LENGTH_LONG).show();
            txtYears.requestFocus();
            return false;
        }

        int years=Integer.valueOf(txtYears.getText().toString());

        //1935 - 2005
        if (!(years>=1935 && years<=2005))
        {
            Toast.makeText(RegisterUserActivity.this, "El año de nacimiento no es válido",Toast.LENGTH_LONG).show();
            return false;
        }

        if (!isDateValid(txtDays.getText() + "/" + txtMonths.getText() + "/" +  txtYears.getText(), "dd/MM/yyyy"))
        {
            Toast.makeText(RegisterUserActivity.this, "La fecha de nacimiento no es válida. por favor verifique",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public boolean isDateValid(String dateToValidate, String dateFromat){

        if(dateToValidate == null){
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
        sdf.setLenient(false);

        try {

            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);
            System.out.println(date);

        } catch (ParseException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

}
