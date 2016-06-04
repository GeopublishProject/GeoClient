package com.geopublish.geoclient.db.model;

import java.util.Date;

/**
 * Created by edgar on 10/5/2015.
 * Clase para guardar los datos del las promociones a los que el usuario se suscribe
 */
public class PromoData {
    public int id;
    public String clientIconFileName;
    public String clientName;
    public String description;
    public String code;
    public Date expirationDate;
    public int views;
    public String promotionalPictureName;

    public PromoData(String clientName,String clientIconFileName,String description, String code, Date expirationDate,String promotionalPictureName, int views)
    {
        this.clientName=clientName;
        this.clientIconFileName=clientIconFileName;
        this.description=description;
        this.code=code;
        this.expirationDate=expirationDate;
        this.promotionalPictureName=promotionalPictureName;
        this.views=views;
    }
}
