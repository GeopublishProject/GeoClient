package serialization;

import java.io.Serializable;

/**
 * Created by edgar on 10/2/2015.
 * Almacena los datos de la promoción a la cual se quiere suscribir el usuario.
 * Esta clase se usa para transmitir datos
 */
public class PromoMessage implements Serializable {
    private static final long serialVersionUID=800828L;

    public int action;
    public String deviceId;
    public String code;


}
