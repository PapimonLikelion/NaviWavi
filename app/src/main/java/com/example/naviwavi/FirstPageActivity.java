package com.example.naviwavi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class FirstPageActivity extends AppCompatActivity {
    private static String appkey = "l7xx7605cdc317c24677991d6d78a182ff5f\n"; //앱키

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
    }

    /*도착 지점 입력 후 엔터 누르면 해당 지점 경로를 위한 도착지점으로 지정*/
    public void endEnter(View v) throws ParserConfigurationException, SAXException, IOException {
        EditText endPoint = (EditText) findViewById(R.id.endPoint);
        String destination = endPoint.getText().toString();
        if (destination.equals("")) {
            Toast.makeText(getApplicationContext(), "도착지를 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), destination, Toast.LENGTH_SHORT).show();
            TMapTapi tmaptapi = new TMapTapi(this);
            tmaptapi.setSKTMapAuthentication (appkey);
            /* 비동기 처리하지 않고 우선 구현 */
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            /* 사용자가 검색한 출발점, 도착점 위도 경도 받아오기 */
            TMapData tmapdata = new TMapData();
            ArrayList<TMapPOIItem> destinationPOI;
            destinationPOI = tmapdata.findAllPOI(destination);
            if (destinationPOI.size() != 0) {
                Intent option = new Intent(this, OptionActivity.class);
                option.putExtra("oneName", destinationPOI.get(0).name);
                option.putExtra("oneRoadName", destinationPOI.get(0).roadName);
                option.putExtra("twoName", destinationPOI.get(1).name);
                option.putExtra("twoRoadName", destinationPOI.get(1).roadName);
                option.putExtra("threeName", destinationPOI.get(2).name);
                option.putExtra("threeRoadName", destinationPOI.get(2).roadName);
                startActivity(option);
            } else {
                Toast.makeText(getApplicationContext(), "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* 도착 위도 경도 정보 MainActivity로 넘겨줌 */
    public void startNavigation(View v) throws ParserConfigurationException, SAXException, IOException {
        EditText Destination = (EditText) findViewById(R.id.endPoint);
        String destination = Destination.getText().toString();
        TMapTapi tmaptapi = new TMapTapi(this);
        tmaptapi.setSKTMapAuthentication (appkey);

        if (destination.equals("")) {
            Toast.makeText(getApplicationContext(), "도착지를 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {
            /* 비동기 처리하지 않고 우선 구현 */
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            /* 사용자가 검색한 출발점, 도착점 위도 경도 받아오기 */
            TMapData tmapdata = new TMapData();
            ArrayList<TMapPOIItem> destinationPOI = tmapdata.findAllPOI(destination);

            Intent main = new Intent(this, MainActivity.class);
            main.putExtra("endLatitude", destinationPOI.get(0).frontLat);
            main.putExtra("endLongitude", destinationPOI.get(0).frontLon);
            String routeName = "현 위치에서 " + destination + "까지" + "\n";
            Toast.makeText(getApplicationContext(), routeName + "경로 안내를 시작합니다", Toast.LENGTH_SHORT).show();
            startActivity(main);
        }
    }
}