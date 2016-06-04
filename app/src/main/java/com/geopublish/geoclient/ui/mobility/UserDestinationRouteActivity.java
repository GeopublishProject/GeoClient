package com.geopublish.geoclient.ui.mobility;


import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.geopublish.geoclient.R;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapDataStore;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Muestra el mapa con la posicion del usuario y su destino
 */
public class UserDestinationRouteActivity extends AppCompatActivity {

    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;
    private String destinationPointLat;
    private String destinationPointLong;
    private String stopPointLat;
    private String stopPointLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Linea necesaria para el correcto funcionamiento de MapsForge
        AndroidGraphicFactory.createInstance(getApplication());
        setContentView(R.layout.activity_user_destination_route);

        try
        {
            Bundle bundle= getIntent().getExtras();

            if (bundle!=null)
            {
                destinationPointLat=bundle.getString("destinationPointLat");
                destinationPointLong=bundle.getString("destinationPointLong");
                stopPointLat=bundle.getString("stopPointLat");
                stopPointLong= bundle.getString("stopPointLong");
            }

            this.mapView = (MapView)findViewById(R.id.mapView);

            this.mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
            this.mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

            // create a tile cache of suitable size
            this.tileCache = AndroidUtil.createTileCache(this, "mapcache",
                    mapView.getModel().displayModel.getTileSize(), 1f,
                    this.mapView.getModel().frameBufferModel.getOverdrawFactor());
        }
        catch (Exception ex)
        {
            Log.i("INFO","Error showing map");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        this.mapView.getModel().mapViewPosition.setZoomLevel((byte) 16);

        // tile renderer layer using internal render theme
        MapDataStore mapDataStore = new MapFile(getMapFile());

        this.tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                this.mapView.getModel().mapViewPosition, false, true, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

        // only once a layer is associated with a mapView the rendering starts
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);


        //Dibujamos los iconos del usuario y la parada
        if(stopPointLat != null && !stopPointLat.isEmpty())
        {
            Marker userStopMarker;
            Marker assignedStopMarker;

            org.mapsforge.core.graphics.Bitmap userStopBitmap;
            org.mapsforge.core.graphics.Bitmap assignedStopBitmap;

            userStopBitmap=AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(R.drawable.person_64));
            assignedStopBitmap=AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(R.drawable.destination_pin_64));

            userStopMarker = new Marker(new LatLong(Double.parseDouble(stopPointLat),Double.parseDouble(stopPointLong)), userStopBitmap, 0, 0);
            mapView.getLayerManager().getLayers().add(userStopMarker);

            assignedStopMarker = new Marker(new LatLong(Double.parseDouble(destinationPointLat),Double.parseDouble(destinationPointLong)), assignedStopBitmap, 0, 0);
            mapView.getLayerManager().getLayers().add(assignedStopMarker);

            double midLat= (Double.parseDouble(stopPointLat) + Double.parseDouble(destinationPointLat))/2;
            double midLong= (Double.parseDouble(stopPointLong) + Double.parseDouble(destinationPointLong))/2;

            this.mapView.getModel().mapViewPosition.setCenter(new LatLong(midLat, midLong));
        }

        mapView.postInvalidate();
        mapView.repaint();
    }

    /**
     * Obtiene el mapa de la ciudad. Si es la primera vez que se llama este metodo obtiene el archivo y lo guarda en la sd,
     * sino obtiene directamente el mapa desde la sdcard
     * @return Archivo con el mapa en formato binario
     */
    private File getMapFile() {

        //TODO: Este forma de operar debe cambiar. El incluir el mapa en los recursos del apk le agrega un peso innecesario para la descarga
        //y la instalacion. A mayor peso del archivo en la Play Store, va descendiendo la preferncia del usuario por descargarlo.
        //La idea es que el archivo de mapa se descargue desde la aplicacion

        String filePath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File targetFile=new File(filePath + "/barranquilla.map");

        if(targetFile.exists())
        {
            return targetFile;
        }
        else
        {
            try {
                InputStream fileIS = getResources().openRawResource(R.raw.barranquilla);

                //InputStream initialStream = new FileInputStream(new File("src/main/resources/sample.txt"));
                byte[] buffer = new byte[fileIS.available()];
                fileIS.read(buffer);

                targetFile.createNewFile();

                OutputStream outStream = new FileOutputStream(targetFile);
                outStream.write(buffer);

                outStream.close();

                return targetFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;
    }



}
