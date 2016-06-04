package com.geopublish.geoclient.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geopublish.geoclient.R;
import com.geopublish.geoclient.db.model.PromoData;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by edgar on 01/04/2015.
    Adaptador para los items de las promociones
    TODO: Implementar Holder
    TODO: Implementar glide y gildeTransformations
 */

public class PromosAdapter extends ArrayAdapter<PromoData> {
    private ArrayList<PromoData> promos;
    private Context contexto;

    public PromosAdapter(Context contexto, ArrayList<PromoData> promos) {
        super(contexto, R.layout.promo_item, promos);
        this.contexto = contexto;
        this.promos = promos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.promo_item, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.txtComments);
        TextView txtPromoCode = (TextView) rowView.findViewById(R.id.txtPromoCode);
        TextView txtNewDate = (TextView) rowView.findViewById(R.id.txtNewDate);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView_imagen);

        PromoData promoData=promos.get(position);

        String fullFileName=contexto.getApplicationInfo().dataDir+ "/images/" + promoData.clientIconFileName ; //+ promoData.();
        Bitmap myBitmap ;
        File imgFile = new  File(fullFileName);

        if(imgFile.exists()){
            myBitmap = getThumbnail(fullFileName);
            imageView.setImageBitmap(myBitmap);
        }

        textView.setText(promoData.description);
        txtPromoCode.setText(promoData.code);
        txtNewDate.setText( new SimpleDateFormat("dd/MM/yyyy").format(promoData.expirationDate));

        return rowView;
    }
    private Bitmap getThumbnail(String fileName)
    {
        final int THUMBNAIL_SIZE = 64;

        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileName), THUMBNAIL_SIZE, THUMBNAIL_SIZE);
    }

}
