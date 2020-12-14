package com.example.naviwavi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {
    private static String appkey = "l7xx7605cdc317c24677991d6d78a182ff5f\n"; //앱키
    private static TMapView tMapView; //지도객체
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private double stopNavigationThreshold = 0.0005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.tMap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey(appkey);
        tMapView.setIconVisibility(true);
        tMapView.setZoomLevel(17);
        linearLayoutTmap.addView(tMapView);
        Intent firstPageSetting = getIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            return;
        }
        gpsSetting();
        endLatitude = Double.parseDouble(firstPageSetting.getStringExtra("endLatitude"));
        endLongitude = Double.parseDouble(firstPageSetting.getStringExtra("endLongitude"));
        markDestination();
    }

    private void markDestination() {
        TMapMarkerItem destination = new TMapMarkerItem();
        TMapPoint Destination = new TMapPoint(endLatitude, endLongitude);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.poi_dot);
        destination.setIcon(bitmap); // 마커 아이콘 지정
        destination.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        destination.setTMapPoint( Destination ); // 마커의 좌표 지정
        destination.setName("도착지"); // 마커의 타이틀 지정
        tMapView.addMarkerItem("도착지", destination); // 지도에 마커 추가
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                startLatitude = latitude;
                startLongitude = longitude;
                showRoute();
            }
        }
        public void onProviderDisabled(String provider) { }
        public void onProviderEnabled(String provider) { }
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    };

    public void gpsSetting() {
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,1, mLocationListener);
    }

    public void showRoute() {
        if(Math.abs(startLatitude-endLatitude) < stopNavigationThreshold
                && Math.abs(startLongitude-endLongitude) < stopNavigationThreshold) {
            Toast.makeText(getApplicationContext(), "목적지에 도착하였습니다\n경로 안내를 종료합니다", Toast.LENGTH_SHORT).show();
            finish();
        }
        TMapPoint routeStart = new TMapPoint(startLatitude, startLongitude);
        TMapPoint routeEnd = new TMapPoint(endLatitude, endLongitude);
        TMapData tmapdata = new TMapData();
        TMapPolyLine route = null;
        try {
            route = tmapdata.findPathData(routeStart, routeEnd);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        route.setLineColor(Color.BLUE);
        route.setLineWidth(5);
        tMapView.setCenterPoint(startLongitude, startLatitude);
        tMapView.setLocationPoint(startLongitude, startLatitude);
        tMapView.addTMapPolyLine("route", route);
    }
}