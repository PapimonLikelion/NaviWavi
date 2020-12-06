package com.example.naviwavi;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
//        TMapView tMapView = new TMapView(this);
//        tMapView.setSKTMapApiKey("l7xx7605cdc317c24677991d6d78a182ff5f\n" );
//        TMapTapi tmaptapi = new TMapTapi(this);
//        tmaptapi.invokeRoute("T타워", 126.984098f, 37.566385f);
        TMapTapi tmaptapi = new TMapTapi(this);
        tmaptapi.setSKTMapAuthentication ("l7xx7605cdc317c24677991d6d78a182ff5f\n");
        tmaptapi.invokeRoute("T타워", 126.984098f, 37.566385f);
//        linearLayoutTmap.addView( tMapView );
    }
}