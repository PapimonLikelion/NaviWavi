package com.example.naviwavi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {
        private static String appkey = "l7xx7605cdc317c24677991d6d78a182ff5f\n"; //앱키
        private static TMapView tMapView; //지도객체

        /*처음 지도 객체 생성시, FirstPage 에서 입력한 정보를 바탕으로 경로 표시함 */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.tMap);
            tMapView = new TMapView(this);
            tMapView.setSKTMapApiKey(appkey);

            linearLayoutTmap.addView(tMapView);

            Intent firstPageSetting = getIntent();
            double startLatitude  = Double.parseDouble(firstPageSetting.getStringExtra("startLatitude"));
            double startLongitude = Double.parseDouble(firstPageSetting.getStringExtra("startLongitude"));
            double endLatitude    = Double.parseDouble(firstPageSetting.getStringExtra("endLatitude"));
            double endLongitude   = Double.parseDouble(firstPageSetting.getStringExtra("endLongitude"));

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
            tMapView.addTMapPolyLine("route", route);
        }



//        /* 출발~도착 경로 찾기 */
//        public void routeFind(View v) throws ParserConfigurationException, SAXException, IOException {
//            EditText startPoint = (EditText) findViewById(R.id.startPoint);
//            EditText endPoint = (EditText) findViewById(R.id.endPoint);
//            String start = startPoint.getText().toString();
//            String end = endPoint.getText().toString();
//            if (start.equals("") || end.equals("")) {
//                Toast.makeText(getApplicationContext(), "출발지와 도착지를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
//            } else {
//                String routeName = start + " ~ " + end;
//                Toast.makeText(getApplicationContext(), routeName + "경로를 안내합니다", Toast.LENGTH_SHORT).show();
//                /* 비동기 처리하지 않고 우선 구현 */
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                StrictMode.setThreadPolicy(policy);
//                /* 사용자가 검색한 출발점, 도착점 위도 경도 받아오기 */
//                TMapData tmapdata = new TMapData();
//                ArrayList<TMapPOIItem> StartPOI = tmapdata.findAllPOI(start);
//                ArrayList<TMapPOIItem> EndPOI = tmapdata.findAllPOI(end);
//
//                double startLatitude = Double.parseDouble(StartPOI.get(0).frontLat);
//                double startLongitude = Double.parseDouble(StartPOI.get(0).frontLon);
//                double endLatitude = Double.parseDouble(EndPOI.get(0).frontLat);
//                double endLongitude = Double.parseDouble(EndPOI.get(0).frontLon);
//
//                TMapPoint routeStart = new TMapPoint(startLatitude, startLongitude);
//                TMapPoint routeEnd = new TMapPoint(endLatitude, endLongitude);
//
//                TMapPolyLine route = tmapdata.findPathData(routeStart, routeEnd);
//                route.setLineColor(Color.BLUE);
//                route.setLineWidth(5);
//                tMapView.addTMapPolyLine("route", route);
//            }
//        }

}