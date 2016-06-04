package com.geopublish.geoclient.ui.promos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.geopublish.geoclient.R;
import com.geopublish.geoclient.adapters.PromosAdapter;
import com.geopublish.geoclient.db.model.GeoClientDataSource;
import com.geopublish.geoclient.db.model.PromoData;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PromosActivity extends AppCompatActivity {
    static final int ADD_SUBSCRIPTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promos);

        com.software.shell.fab.ActionButton btnShowPromoSubscriber= (ActionButton) findViewById(R.id.btnShowPromoSubscriber);

        btnShowPromoSubscriber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PromosActivity.this, PromoSubscriberActivity.class);
                startActivityForResult(intent, ADD_SUBSCRIPTION);

            }
        });

        ListView listViewPromos = (ListView)findViewById(R.id.listView);
        listViewPromos.setEmptyView(findViewById(R.id.emptyElement));

        listViewPromos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PromoData promoData = (PromoData) parent.getItemAtPosition(position);

                if (promoData.views==0)
                {
                    ShowQRViewer(promoData.clientName,promoData.code,promoData.promotionalPictureName);
                }

                if (promoData.views==1)
                {
                    new SweetAlertDialog(PromosActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Atención")
                            .setContentText("El código QR de esta promoción solo debe ser visualizado al momento de reclamar la promoción. Si muestras este código ahora ya no podrás hacer uso del mismo mas adelante. ¿Desea continuar?")
                            .setCancelText("No, cerrar")
                            .setConfirmText("Si, ver")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.cancel();

                                    ShowQRViewer(promoData.clientName, promoData.code,  promoData.promotionalPictureName);
                                }
                            })
                            .show();
                }

                if (promoData.views>1)
                {
                    Toast.makeText(PromosActivity.this,"El código QR de esta promoción no puede ser mostrado debido a que ya se uso en una ocasión anterior", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void ShowQRViewer(String clientName,String promoCod, String promotionalPictureName)
    {
        Intent intent = new Intent(PromosActivity.this, QRViewerActivity.class);
        Bundle b = new Bundle();

        b.putString("clientName", clientName);
        b.putString("promoCode", promoCod);
        b.putString("promotionalPictureName", promotionalPictureName);

        intent.putExtras(b); //Put your id to your next Intent
        startActivityForResult(intent, 1);
    }

    private void publishPromos()
    {
        final GeoClientDataSource geoClientDataSource = new GeoClientDataSource(this);
        ArrayList<PromoData> promos= geoClientDataSource.getPromos();
        PromosAdapter promosAdapter=new PromosAdapter(this, promos);

        ListView listViewPromos = (ListView)findViewById(R.id.listView);
        listViewPromos.setAdapter(promosAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        publishPromos();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    }


}
