package com.geopublish.geoclient.db.model;

/**
 * Created by edgar on 10/5/2015.
 * Clase para guardar los datos relacionados con los clientes de GeoPublish
 */
public class ClientData {

    public int id;
    public String name;
    public String iconFileName;

    public ClientData(int id,String name, String iconFileName)
    {
        this.id=id;
        this.name=name;
        this.iconFileName=iconFileName;
    }
}
