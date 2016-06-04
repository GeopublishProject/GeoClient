package com.geopublish.geoclient.adapters;

/**
 * Created by edgar on 11/17/2015.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.geopublish.geoclient.R;
import com.geopublish.geoclient.ui.markers.StateItem;

import java.util.ArrayList;
import java.util.HashMap;

public class StopMarkerAdapter extends BaseAdapter {
    private Context context;
    // Keeping the currently selected item
    public int selectedItemPosition = 0;
    private final ArrayList<StateItem> values;

    public StopMarkerAdapter(Context context, ArrayList<StateItem> values) {
        this.context = context;
        this.values = values;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        //Para implementar la funcionalidad de seleccionar el tooltip , cambiar el color de este y seleccionarlo presento muchos problemas
        //El modo de seleccion normal SingleChoice no funciona debido a la implementacion del modo touch de Android que hace que la seleccion se pierda.
        // Despues se cambio a un modo de pintar el backgrouund de cada elemento del gridview dependiendo si en este se habia hecho click o  a un item, si se hacia
        // click el item se ponia de un color mientras que los otros items cambiaban a transparente. Esta aproximacion aunque exitosa en un principio presento un
        // problema debido a que al momento de actualizar la imagen del tootip seleccionado se podria hacer el cambio de imagen y mantenia el color pero se perdia
        // nuevamente el color de seleccion al hacer click en textview por ejemplo y aunque se llamadra al meotod de invalidacion o notifyDataSetChanged no se
        // disparaba y no obligaba a redibujar el gridView. Despues de implementar el properGridView con un observador sobre cambios en el adaptador aun no funcionaba
        // la actualizacion del gridView ahora porque la implementacion del metodo GetView normalmente reusa la vista de los elementos y no infla nuevamente
        //el item por lo que toco quitar las verificaciones de vista existente.Haciendo esto se solucionan todos los problemas (aparicion del teclado, cambio de
        // elemento seleccionado, asignacion de color para cambiar imagen en el tooltip) pero el desempeño queda mal porque cada vez que se hace click en un icono
        //se tiene que redibujar todo el gridView



        // get layout from mobile.xml
        gridView = inflater.inflate(R.layout.stop_marker_item, null);

        StateItem stateItem= ((StateItem)getItem(position));

        if (stateItem.isItemSelected)
            gridView.setBackgroundColor(Color.parseColor("#666666"));
        else
            gridView.setBackgroundColor(Color.TRANSPARENT);

        // set image based on selected text
        ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);

        try
        {
            int drawableResourceId = this.context.getResources().getIdentifier(stateItem.colorMarker.iconName, "drawable", this.context.getPackageName());
            imageView.setImageResource(drawableResourceId);
        }
        catch (Exception e)
        {

        }

        return gridView;
    }


    /**
     * Setting the item in the argumented position - as selected.
     * @param position
     * @return
     */
    public long setSelectable(int position) {

        //for (int i=0; i<values.size();i++)
        //    values.get(i).isItemSelected = false;

        values.get(selectedItemPosition).isItemSelected = false;
        values.get(position).isItemSelected = true;

        selectedItemPosition=position;

        // Making the list redraw
        notifyDataSetChanged();
        return getSelectedId();
    }

    /**
     * Needed to notify the ListView's system that the IDs we use here are unique to each item
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    public long getSelectedId() {
        if (selectedItemPosition == -1)
            return -1;
        else {
            return getItemId(selectedItemPosition);
        }
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
