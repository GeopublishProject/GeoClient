package com.geopublish.geoclient.ui.mobility;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geopublish.geoclient.GeoclientApplication;
import com.geopublish.geoclient.R;
import com.geopublish.geoclient.ui.markers.StateItem;
import com.geopublish.geoclient.Utils;
import com.geopublish.geoclient.adapters.StopMarkerAdapter;
import com.geopublish.geoclient.gcm.services.RegistrationIntent;
import com.geopublish.geoclient.ui.controls.ProperGridView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import cn.pedant.SweetAlert.SweetAlertDialog;
import serialization.MessageData;
import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;

public class RegisterUserStopActivity extends ActionBarActivity {

    private Button btnRegisterUserData;
    private Handler updateConversationHandler;
    private MessageData messageData;
    private ObjectOutputStream oos;
    private DataInputStream dataInputStream;
    private Spinner spinnerDirection1;
    private Spinner spinnerDirection2;
    private String dir1;
    private String dir2;
    private String dir1Number;
    private String dir2Number;
    private String distance;
    private String userName;
    private boolean clickable = true;

    private GridView gvStopMarkerIcons;
    private StopMarkerAdapter stopMarkerAdapter ;
    // set color palette

    private int selectedColor=0;
    private int selectedStopMarkerIndex;
    private String selectedColorName;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ProgressDialog barProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        barProgressDialog = new ProgressDialog(RegisterUserStopActivity.this);

        barProgressDialog.setTitle("Acércame ...");
        barProgressDialog.setMessage("Estamos procesando tu solicitud ...");
        barProgressDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setIndeterminate(true);
        barProgressDialog.setCancelable(false);

        spinnerDirection1 = (Spinner) findViewById(R.id.direction1);
        spinnerDirection2 = (Spinner) findViewById(R.id.direction2);
        spinnerDirection2.setSelection(1);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegisterUserStopActivity.this);
        userName=sharedPreferences.getString("userName", null);

        btnRegisterUserData = (Button) findViewById(R.id.btnRegisterUserData);
        TextView lblUser=(TextView) findViewById(R.id.lblUser);

        messageData = new MessageData();
        lblUser.setText(userName);

        btnRegisterUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (clickable) {
                    clickable = true;

                    Log.i("INFO", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());

                    EditText txtDirection1Number = (EditText) findViewById(R.id.direction1Number);
                    EditText txtDirection2Number = (EditText) findViewById(R.id.direction2Number);
                    EditText txtDistance = (EditText) findViewById(R.id.txtDistance);

                    dir1 = spinnerDirection1.getSelectedItem().toString();
                    dir2 = spinnerDirection2.getSelectedItem().toString();
                    dir1Number = txtDirection1Number.getText().toString();
                    dir2Number = txtDirection2Number.getText().toString();
                    distance = txtDistance.getText().toString();

                    if (dir1Number.trim().length() == 0) {
                        Toast.makeText(RegisterUserStopActivity.this, "Debe establecer la primera parte de la dirección", Toast.LENGTH_LONG).show();
                        txtDirection1Number.requestFocus();
                        return;
                    }

                    if (dir2Number.trim().length() == 0) {
                        Toast.makeText(RegisterUserStopActivity.this, "Debe establecer la segunda parte de la dirección", Toast.LENGTH_LONG).show();
                        txtDirection2Number.requestFocus();
                        return;
                    }

                    //Debido a que debemos sacar un mensaje claro hacemos esta validacion al final
                    if (dir1.equals(dir2)) {
                        Toast.makeText(RegisterUserStopActivity.this, dir1 + " " + dir1Number + " con " + dir2 + " " + dir2Number + " no es una dirección válida. Por favor verifique", Toast.LENGTH_LONG).show();
                        spinnerDirection1.requestFocus();
                        return;
                    }

                    if (distance.trim().length() == 0) {
                        Toast.makeText(RegisterUserStopActivity.this, "Debe establecer el número de la dirección", Toast.LENGTH_LONG).show();
                        txtDistance.requestFocus();
                        return;

                    } else {
                        if (distance.equals("0")) {
                            Toast.makeText(RegisterUserStopActivity.this, "El número de la dirección no puede ser cero (0)", Toast.LENGTH_LONG).show();
                            txtDistance.requestFocus();
                            return;
                        }
                    }

                    new SweetAlertDialog(RegisterUserStopActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Atención")
                            .setContentText("¿Los datos son correctos?")
                            .setCancelText("No")
                            .setConfirmText("Si, continuar")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.cancel();

                                    messageData.Action = 2;
                                    messageData.deviceId = Utils.getDeviceId(RegisterUserStopActivity.this);
                                    messageData.deviceToken = GeoclientApplication.deviceToken;
                                    messageData.fullName = userName;
                                    messageData.direction1 = dir1;
                                    messageData.directionNumber1 = dir1Number;
                                    messageData.direction2 = dir2;
                                    messageData.directionNumber2 = dir2Number;
                                    messageData.distance = Integer.parseInt(distance);
                                    messageData.stopMarkerId = GeoclientApplication.stateItems.get(selectedStopMarkerIndex).colorMarker.iconName;

                                    updateConversationHandler = new Handler();

                                    ConnectThread connectThread = new ConnectThread(messageData);
                                    connectThread.start();

                                    barProgressDialog.show();

                                }
                            })
                            .show();
                }
            }
        });

        //Como es una lista global para la aplicacion, establecemos que todos los itmes no estan seleccionados por si acaso
        //se selecciono un icono en una ocasion anterior
        for (int i = 0; i < GeoclientApplication.stateItems.size(); i++) {
            GeoclientApplication.stateItems.get(i).isItemSelected=false;
        }

        gvStopMarkerIcons = (ProperGridView) findViewById(R.id.gvIcons);
        stopMarkerAdapter=new StopMarkerAdapter(this, GeoclientApplication.stateItems);

        highlightStopMarker(getRandomNumber(0,11));

        gvStopMarkerIcons.setAdapter(stopMarkerAdapter);
        gvStopMarkerIcons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                ((StopMarkerAdapter)gvStopMarkerIcons.getAdapter()).setSelectable(position);
                selectedStopMarkerIndex=position;

                if (position == 0) {

                    final Dialog d = new Dialog(RegisterUserStopActivity.this, R.style.NoTitleDialog);

                    d.setContentView(R.layout.color_picker);
                    d.setCanceledOnTouchOutside(true);
                    d.show();

                    final LineColorPicker colorPicker = (LineColorPicker) d.findViewById(R.id.picker);

                    String[] pallete = new String[]{"#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5"
                            , "#2196F3", "#4CAF50", "#CDDC39", "#FF9800", "#795548", "#9E9E9E", "#607D8B"};

                    int[] colors = new int[pallete.length];

                    for (int i = 0; i < colors.length; i++) {
                        colors[i] = Color.parseColor(pallete[i]);
                    }

                    colorPicker.setColors(colors);

                    // set selected color [optional]
                    colorPicker.setSelectedColor(selectedColor);

                    // set on change listener
                    colorPicker.setOnColorChangedListener(new OnColorChangedListener() {
                        @Override
                        public void onColorChanged(int c) {
                            Log.d("Test", "Selected color " + Integer.toHexString(c));
                        }
                    });

                    // get selected color
                    Button dialogButtonAccept = (Button) d.findViewById(R.id.btnSetColor);
                    // if button is clicked, close the custom dialog
                    dialogButtonAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();

                            selectedColor = colorPicker.getColor();
                            selectedColorName = getIconNameFromColor(selectedColor);

                            StateItem stateItem = GeoclientApplication.stateItems.get(0);

                            stateItem.colorMarker.colorName=null;
                            stateItem.colorMarker.rgbColor=Integer.toHexString(selectedColor);
                            stateItem.colorMarker.iconName=selectedColorName;

                            stopMarkerAdapter.notifyDataSetChanged();
                        }
                    });

                    d.show();
                }
            }
        });

        //TODO: Este registro se debe hacer cuando el usuario hace click en el boton de solcitud de parada
        //pero recordar que si en la accion onClick se ejecuta el registro del dispositivo para obtener el token
        //puede que el registro del usuario no se guarde con el token debido a que en la operacion asyncrona aun
        //se esta obteniendo el midmo. por el contrario si la operacion es sincronica entonces se tendria que
        //esperar a que se resuelva el token

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntent.class);
            startService(intent);
        }
    }

    /**
     * Selecciona el item basado en el indice
     * @param stopMarkerIndex Indice del item a seleccionar
     */
    private void highlightStopMarker(int stopMarkerIndex)
    {
        selectedColor =Color.parseColor(GeoclientApplication.stopMarkerColors.get(stopMarkerIndex).rgbColor) ;
        selectedColorName = getIconNameFromColor(selectedColor);

        StateItem stateItem = GeoclientApplication.stateItems.get(0);

        stateItem.isItemSelected=true;
        stateItem.colorMarker.colorName=null;
        stateItem.colorMarker.rgbColor=Integer.toHexString(selectedColor);
        stateItem.colorMarker.iconName=selectedColorName;
    }

    /**
     * Obtiene un entero aleatorio en determinado rango
     * @param min Limite inferior del rango.
     * @param max Limite superior del rango
     * @return Entero aleatorio
     */
    private int getRandomNumber(   int min, int max)
    {
        return min + (int)(Math.random() * ((max - min) + 1));
    }


    /**
     * Obtiene el icono adecuado basado en el color pasado como parametro
     * @param color Color (entero)
     * @return Cadena con el nombre del icono
     */
    private String getIconNameFromColor(int color) {
        String hex = Integer.toHexString(color);
        String iconName=null;

        hex = hex.toUpperCase();

        // "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#4CAF50", "#CDDC39", "#FF9800"  , "#795548", "#9E9E9E", "#607D8B"
        switch (hex)
        {
            case "FFF44336":
                iconName= "call_red";
                break;

            case "FFE91E63":
                iconName= "call_pink";
                break;

            case "FF9C27B0":
                iconName= "call_purple";
                break;

            case "FF673AB7":
                iconName= "call_deeppurple";
                break;

            case "FF3F51B5":
                iconName= "call_indigo";
                break;

            case "FF2196F3":
                iconName= "call_blue";
                break;

            case "FF4CAF50":
                iconName= "call_green";
                break;

            case "FFCDDC39":
                iconName= "call_lime";
                break;

            case "FFFF9800":
                iconName= "call_orange";
                break;

            case "FF795548":
                iconName= "call_brown";
                break;

            case "FF9E9E9E":
                iconName= "call_gray";
                break;

            case "FF607D8B":
                iconName= "call_bluegray";
                break;
        }

        return iconName;
    }

    public void onCancelButtonClick (View v)
    {
        finish();
    }


    @Override
    public void onResume(){
        super.onResume();
    }

    /**
     * Clase encargada de enviar/recibir  los mensajes al servidor
     */
     public class ConnectThread extends Thread {
        private MessageData messageData;

        public ConnectThread(MessageData messageData) {
            this.messageData = messageData;
        }

        public void run() {
            Socket s ;
            String responseMessage = "Se ha presentado un error en la aplicación. Por favor revise que se encuentre conectado(a) a la red";
            try {
                s = new Socket(GeoclientApplication.ServerIP, GeoclientApplication.ServerPort);

                oos = new ObjectOutputStream(s.getOutputStream());
                dataInputStream = new DataInputStream(s.getInputStream());

                oos.writeObject(this.messageData);
                oos.flush();

                //accept server response
                int response = dataInputStream.readInt();

                switch (response) {
                    case 50:
                        responseMessage = "El sistema aun no esta disponible. Espera un momento e intenta mas tarde";
                        break;

                    case 100:
                        responseMessage = "No es posible obtener la dirección de destino. Por favor revisa la dirección";
                        break;

                    case 200:
                        responseMessage = "La dirección de parada esta muy alejada de la ruta del bus. Por favor intenta con una dirección mas cercana";
                        break;

                    case 250:
                        responseMessage = "Este bus se pasó de la dirección solicitada.";
                        break;

                    case 300:
                        //TODO. recordar que esto hay que corregirlo y manejar el tipo de operacion en el servidor de datos. La actualizacion de la parada hace referecnai
                        //a que el usuario introdujo una nueva direccion sobre una que ya tenia en estado m1, stop o waiting mientras que una parada nueva es cuando el usuario
                        //no existe o existiendo no tiene parada asignada
                        responseMessage = "Se registró exitosamente la parada";
                        //responseMessage = "Se actualizaron correctamente los datos de la parada";
                        break;

                    case 400:
                        responseMessage = "Se registró exitosamente la parada";
                        break;

                    case 500:
                        responseMessage = "El sistema no se encuentra disponible en este momento. Por favor intente mas tarde";
                        break;

                    default:
                        responseMessage = "Se ha presentado un error mientras se procesaba la solicitud." + String.valueOf(response);
                        break;
                }

                Log.i("TcpClient", "received: " + responseMessage);
                dataInputStream.close();
                //close connection
                oos.close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                updateConversationHandler.post(new updateUIThread(responseMessage));
            }
        }
    }

    /**
     * Clase encargada de mostrar los mensajes recibidos y procesados por la clase de comunicacion con el server
     */
    class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String message) {
            this.msg = message;
        }

        @Override
        public void run() {
            if (barProgressDialog !=null)
                barProgressDialog.dismiss();

            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            clickable = true;
        }
    }

    /**
     * Determinar si el dispositivo tiene Google Play Service. Si no lo tiene
     * muestra un dialogo para descargar el APK desde la Play Store o lo habilita
     * en los parametros del sistema
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("Check PLAY SERVICES", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
