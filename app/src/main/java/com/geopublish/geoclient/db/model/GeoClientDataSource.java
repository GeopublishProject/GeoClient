package com.geopublish.geoclient.db.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by edgar on 28/03/2015.
 * Esta clase permite realizar la administracion de la base de datos del usuario. Administra los datos de los clientes de GeoPublish y sus promociones
 */
public class GeoClientDataSource {
    //Metainformación de la base de datos
    public static final String CLIENTS_TABLE_NAME = "Clients";
    public static final String PROMOS_TABLE_NAME = "Promos";
    public static final String STRING_TYPE = "nvarchar";
    public static final String INT_TYPE = "integer";
    public static final String REAL_TYPE = "real";
    public static final String BOOL_TYPE = "bool";

    //Campos de la tabla Client
    public static class ClientColumns{
        public static final String ID= BaseColumns._ID;
        public static final String NAME= "name";
        public static final String ICON_FILE_NAME = "iconFileName";
    }

    //Campos de la tabla Promos
    public static class PromoColumns{
        public static final String ID= BaseColumns._ID;
        public static final String ID_CLIENT= "idClient";
        public static final String DESCRIPTION = "description";
        public static final String CODE="code";
        public static final String EXPIRATION_DATE="expirationDate";
        public static final String PROMOTIONAL_PICTURE_NAME="promotionalPictureName";
        public static final String VIEWS="views";
            }

    //Script de Creación de la tabla Clients
    public static final String CREATE_CLIENT_SCRIPT =
            "create table "+ CLIENTS_TABLE_NAME  +"(" +
                    ClientColumns.ID+" "+INT_TYPE+" primary key autoincrement," +
                    ClientColumns.NAME +" "+STRING_TYPE+" (50) not null," +
                    ClientColumns.ICON_FILE_NAME +" "+ STRING_TYPE  +" not null)";

    //Script de Creación de la tabla Promos
    public static final String CREATE_PROMOS_SCRIPT =
            "create table "+ PROMOS_TABLE_NAME +"(" +
                    PromoColumns.ID+" "+INT_TYPE+" primary key autoincrement," +
                    PromoColumns.ID_CLIENT +" "+INT_TYPE +" not null," +
                    PromoColumns.DESCRIPTION +" "+STRING_TYPE +" (255), " +
                    PromoColumns.CODE + " " + STRING_TYPE +"(25) NOT NULL," +
                    PromoColumns.EXPIRATION_DATE + " " + REAL_TYPE +" NOT NULL," +
                    PromoColumns.PROMOTIONAL_PICTURE_NAME + " " + STRING_TYPE +"(100) NULL," +
                    PromoColumns.VIEWS + " " + INT_TYPE +" NOT NULL DEFAULT 0)";

    private GeoClientDBHelper openHelper;
    private SQLiteDatabase database;

    /**
     * Inicializa y obtiene un contexto de base de datos en el que se puede escribir
     * */
    public GeoClientDataSource(Context context) {
        //Creando una instancia hacia la base de datos
        openHelper = new GeoClientDBHelper(context);
        database = openHelper.getWritableDatabase();
    }

    /**
     * Created by edgar
     * Guarda los datos de un cliente Geopublish en la base de datos.
     * Si el cliente existe actualiza los datos
     */
    public long SaveClient(String name, String iconFileName)
    {
        ClientData clientData= getClientByName(name);

        if (clientData==null)
        {
            ContentValues values = new ContentValues();

            values.put(ClientColumns.NAME, name);
            values.put(ClientColumns.ICON_FILE_NAME, iconFileName);

            return database.insert(CLIENTS_TABLE_NAME, null, values);
        }

        return clientData.id;
    }

    /**
     * Created by edgar
     * Guarda los datos de la promoción a la que se ha suscrito el cliente
     */
    public void SavePromotion(int idClient, String description, String code, Date expirationDate, String qrCode,String promotionalPictureName)
    {
        //TODO:comprobar si el codigo de promocion ya esta registrada.  Aunque debe estar validado en la base de datos remota
        ContentValues values = new ContentValues();

        values.put(PromoColumns.ID_CLIENT, idClient);
        values.put(PromoColumns.DESCRIPTION, description);
        values.put(PromoColumns.CODE, code);
        values.put(PromoColumns.EXPIRATION_DATE, expirationDate.getTime());
        values.put(PromoColumns.PROMOTIONAL_PICTURE_NAME,promotionalPictureName );
        values.put(PromoColumns.VIEWS, 0);

        database.insert(PROMOS_TABLE_NAME, null, values);

        //TODO: Arreglar la recomendacion de conexion: Please fix your application to end transactions in progress properly and to close the database when it is no longer needed
    }

    /**
     *  Created by Edgar Molina on 28/03/2015.
     *  Actualiza la cantidad de veces que se miro la promociòn
     *  @param promoCode Codigo de promocion
     */
    public void incrementQRViews(String promoCode)
    {
        //TODO : Hacer la actualizacion con una instruccion update

        String selectQuery = "SELECT " + PromoColumns.VIEWS + " FROM " + PROMOS_TABLE_NAME + " WHERE " + PromoColumns.CODE + " ='" + promoCode+ "'";
        int views=0;

        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())  {
            do {
                views=(cursor.getInt(0));

            } while (cursor.moveToNext());
        }

        if (cursor.isClosed()==false)
        {
            cursor.close();
        }

        ContentValues args = new ContentValues();
        args.put( PromoColumns.VIEWS, views + 1);

        database.update(PROMOS_TABLE_NAME, args, PromoColumns.CODE + "='" + promoCode + "'", null) ;

    }

    /**
     * Created by edgar on 28/03/2015.
     * Obtiene los datos de un cliente a partir de su nombre
     */
    public ClientData getClientByName(String name)
    {
        boolean exit=false;
        String selectQuery = "SELECT * FROM " + CLIENTS_TABLE_NAME + " WHERE " + ClientColumns.NAME + "='" + name + "'" ;

        Cursor cursor = database.rawQuery(selectQuery, null);
        ClientData clientData=null;

        // looping through all rows and adding to list
        if (cursor.moveToFirst() && exit==false) {
            do {
                clientData=new ClientData(cursor.getInt(0), cursor.getString(1),cursor.getString(2));
            } while (cursor.moveToNext());
        }

        if (cursor.isClosed()==false)
        {
            cursor.close();
        }

        return clientData;
    }

    /**
     * Created by edgar on 28/03/2015.
     * Obtiene la lista de promociones a la que esta suscrito el usuario
     */
    public ArrayList<PromoData> getPromos()
    {
        //TODO:Por el AFAN tocò hacer esta vaina asi. Se metieron directamente los nombres de los campos de las tablas en vez de usar los campos declarados arriba

        String selectQuery = "SELECT C.name,C."  + ClientColumns.ICON_FILE_NAME  +  ",P.description, P.code, P.expirationDate" + ",P." + PromoColumns.PROMOTIONAL_PICTURE_NAME + ",P." + PromoColumns.VIEWS + " FROM " + CLIENTS_TABLE_NAME + " C INNER JOIN " + PROMOS_TABLE_NAME + " P ON C._id=P.idClient ORDER BY P._id DESC" ;
        ArrayList<PromoData> promos = new ArrayList<PromoData>();

        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())  {
            do {
                PromoData promoData=new PromoData(cursor.getString(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), new Date(cursor.getLong(4)),cursor.getString(5), cursor.getInt(6));

                promos.add(promoData);
            } while (cursor.moveToNext());
        }

        if (cursor.isClosed()==false)
        {
            cursor.close();
        }

        return promos;
    }

}
