package com.geopublish.geoclient.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.geopublish.geoclient.ui.promos.PromosActivity;
import com.geopublish.geoclient.R;
import com.geopublish.geoclient.ui.mobility.RegisterUserStopActivity;

/**
 * Menu unico de la aplicacion
 */
public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void onShowMobilityActivity (View v) {
        Intent intent = new Intent(MenuActivity.this,RegisterUserStopActivity.class);
        startActivity(intent);
    }

    public void onShowPromosActivity (View v) {
        Intent intent = new Intent(MenuActivity.this,PromosActivity.class);
        startActivity(intent);
    }
}
