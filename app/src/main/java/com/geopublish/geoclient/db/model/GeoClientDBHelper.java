package com.geopublish.geoclient.db.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by edgar on 28/03/2015.
 * Maneja la creacion y actualizacion de la base de datos en el cliente
 */
public class GeoClientDBHelper extends SQLiteOpenHelper {
    public GeoClientDBHelper(Context context){
        super(context,
                "GeoclientDB",//String name
                null,//factory
                9//int version
        );
    }

    /**
     * Crea las tablas de la base de datos
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Crear la base de datos
        db.execSQL(GeoClientDataSource.CREATE_CLIENT_SCRIPT);
        db.execSQL(GeoClientDataSource.CREATE_PROMOS_SCRIPT);
    }

    /**
     * Crea la base de datos si esta no existe y si exsiste entonces se actualiza la estructura de la base de datos.
     * ATENCION: Este metodo recrea la base de datos, es decir que elimina las tablas existentes y las vuelve a crear
     * pero no actualiza las tablas ni mantiene los datos
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Actualizar la estructura de la base de datos

        if ( newVersion > oldVersion )
        {
            //Eliminamos las tablas existentes y las creamos nuevamente
            db.execSQL( "DROP TABLE IF EXISTS " + GeoClientDataSource.CLIENTS_TABLE_NAME );
            db.execSQL( "DROP TABLE IF EXISTS " + GeoClientDataSource.PROMOS_TABLE_NAME );

            //Creamos las tablas nuevamente
            db.execSQL( GeoClientDataSource.CREATE_CLIENT_SCRIPT);
            db.execSQL( GeoClientDataSource.CREATE_PROMOS_SCRIPT);
        }
    }
}
