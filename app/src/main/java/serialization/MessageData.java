package serialization;

import java.io.Serializable;
/**
 * Created by edgar on 08/08/2015.
 * Almacena los datos de registro de usuario y solicitud de parada
 * Esta clase se usa para transmitir datos
 */
public class MessageData implements Serializable{
    private static final long serialVersionUID=9178463713495654837L;

    public int Action;
    public String texto;
    public boolean LastMessage=false;

    public String deviceToken;
    public String deviceId;
    public String fullName;
    public String ocupation;
    public String bornDate;
    public String direction1;
    public String directionNumber1;
    public String direction2;
    public String directionNumber2;
    public int distance;
    public String stopMarkerId;

}
