package com.geopublish.geoclient;

import android.app.Application;
import android.os.Environment;

import com.geopublish.geoclient.ui.markers.ColorMarker;
import com.geopublish.geoclient.ui.markers.StateItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by edgar on 11/08/2015.
 * El siguiente código se ejecuta al inicio del server para leer los parametros de configuracion
 * Los datos de configuracion son creados por la app ClientConfigurator y generan un archivo XML
 * que luego es leido por GeoClient(Acercame). Si el archivo de configuración no es encontrado,
 * GeoClient usara los parametros del ambiente real
 */
public class GeoclientApplication extends Application {

    //Datos de configuracion por defecto
    public static  String ServerIP="192.168.43.1";
    public static  int ServerPort=7000;
    private String configFilePath;
    public static ArrayList<StateItem> stateItems;
    public static  ArrayList<ColorMarker> stopMarkerColors;
    public static String deviceToken="";

    public  GeoclientApplication()
    {
        stateItems=new ArrayList<>();
        stopMarkerColors=new ArrayList<>();

        stateItems.add(new StateItem(1,new ColorMarker("red", "#F44336","call_red")));
        stateItems.add(new StateItem(2,new ColorMarker("lady", null,"lady")));
        //stateItems.add(new StateItem(3,new ColorMarker("clown", null,"clown")));
        stateItems.add(new StateItem(4,new ColorMarker("irish", null,"irish")));
        stateItems.add(new StateItem(5,new ColorMarker("chief", null,"chief")));
        stateItems.add(new StateItem(6,new ColorMarker("nurse", null,"nurse")));
        stateItems.add(new StateItem(7,new ColorMarker("pirate", null,"pirate")));
        stateItems.add(new StateItem(7,new ColorMarker("black_01", null,"black_01")));
        stateItems.add(new StateItem(7,new ColorMarker("woman_01", null,"woman_01")));

        stopMarkerColors.add(new ColorMarker("red", "#F44336","call_red"));
        stopMarkerColors.add(new ColorMarker("pink", "#E91E63","call_pink"));
        stopMarkerColors.add(new ColorMarker("purple", "#9C27B0","call_purple"));
        stopMarkerColors.add(new ColorMarker("deeppurple", "#673AB7","call_deeppurple"));
        stopMarkerColors.add(new ColorMarker("indigo", "#3F51B5","call_indigo"));
        stopMarkerColors.add(new ColorMarker("blue", "#2196F3","call_blue"));
        stopMarkerColors.add(new ColorMarker("green", "#4CAF50","call_green"));
        stopMarkerColors.add(new ColorMarker("lime", "#CDDC39","call_lime"));
        stopMarkerColors.add(new ColorMarker("orange", "#FF9800","call_orange"));
        stopMarkerColors.add(new ColorMarker("brown", "#795548","call_brown"));
        stopMarkerColors.add(new ColorMarker("gray", "#9E9E9E","call_gray"));
        stopMarkerColors.add(new ColorMarker("bluegray", "#607D8B","call_bluegray"));

        //Lectura del archivo de configuracion
        File root = Environment.getExternalStorageDirectory();

        configFilePath= root.getAbsolutePath()  + "/GeoPublishSettings/Client.xml";

        File file = new File(configFilePath);

        if(file.exists())
            readXML(configFilePath);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    /**
     * Obtiene el valor del parametro requerido
     * @param doc Docuemnto XML de configuracion
     * @param tag Key o parametro de configuracion
     * @return Cadena que contiene el valor del parametro requerido
     */
    private String getTextValue( Element doc, String tag) {
        String value=null;
        NodeList nl;
        nl = doc.getElementsByTagName(tag);

        if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
            value = nl.item(0).getFirstChild().getNodeValue();
        }

        return value;
    }

    /**
     * Lee el archivo de configuracion establecido por la aplicacion GeoClientConfigurator
     * y asigna automaticamente los parametros obtenidos
     * @param fileName Nombre del archivo de configuracion
     */
    public void readXML(String fileName) {
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            File file = new File(fileName);
            InputStream is = new FileInputStream(file.getPath());
            Document doc = db.parse(new InputSource(is));
            doc.getDocumentElement().normalize();

            Element docElement = doc.getDocumentElement();

            ServerIP = getTextValue( docElement, "ServerIP");
            ServerPort = Integer.parseInt(getTextValue(docElement, "ServerPort"));

            is.close();
        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }


}
