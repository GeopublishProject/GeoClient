package com.geopublish.geoclient.ui.markers;

/**
 * Created by edgar on 11/19/2015.
 * Esta clase es la encargada de manejar los datos relacionados con el icono o llamada que selecciono el usuario
 * con el cual se presentara en el mapa
 */
public class ColorMarker {
    public String colorName;
    public String rgbColor;
    public String iconName;

    public ColorMarker(String colorName, String rgbColor, String iconName)
    {
        this.colorName=colorName;
        this.rgbColor=rgbColor;
        this.iconName=iconName;
    }
}
