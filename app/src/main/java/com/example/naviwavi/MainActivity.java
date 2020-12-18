package com.example.naviwavi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static String appkey = "l7xx7605cdc317c24677991d6d78a182ff5f\n"; //앱키
    private static TMapView tMapView; //지도객체
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private double stopNavigationThreshold = 0.0005;

    /* 음악 재생 모듈 */
    public MediaPlayer mediaPlayer;

    /* 캡쳐 관련 */
    static int i = 0;
    private Camera mCamera;
    TextureView cameraView;
    String clientId = "c3o8u3iq7i";//애플리케이션 클라이언트 아이디값";
    String clientSecret = "O4iTbHkxKNoQtDVHMpID7WHYKJ650qcmmiqQ6har";//애플리케이션 클라이언트 시크릿값";

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
        cameraView = (TextureView)findViewById(R.id.cameraTextureView);
        cameraView.setSurfaceTextureListener(this);
    }

    private void markDestination() {
        TMapMarkerItem destination = new TMapMarkerItem();
        TMapPoint Destination = new TMapPoint(endLatitude, endLongitude);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.poi_dot);
        destination.setIcon(bitmap); // 마커 아이콘 지정
        destination.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        destination.setTMapPoint(Destination); // 마커의 좌표 지정
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
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mLocationListener);
    }

    public void showRoute() {
        if (Math.abs(startLatitude - endLatitude) < stopNavigationThreshold
                && Math.abs(startLongitude - endLongitude) < stopNavigationThreshold) {
            Toast.makeText(getApplicationContext(), "목적지에 도착하였습니다\n경로 안내를 종료합니다", Toast.LENGTH_SHORT).show();
            finish();
            /* 음악 모듈 제거 */
            if(mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
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


    /* 감정 상태에 따라 음악 재생 */
    public void playMusic(String feeling) {
        if (feeling.equals("neutral")) {
            return;
        } else if (feeling.equals("anger")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.anger);
            mediaPlayer.start();
        } else if (feeling.equals("disgust")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.disgust);
            mediaPlayer.start();
        } else if (feeling.equals("fear")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.fear);
            mediaPlayer.start();
        }
        /*else if (feeling.equals("laugh")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.laugh);
            mediaPlayer.start();
        } else if (feeling.equals("sad")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.sad);
            mediaPlayer.start();
        } else if (feeling.equals("surprise")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.surprise);
            mediaPlayer.start();
        } else if (feeling.equals("smile")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.smile);
            mediaPlayer.start();
        } else if (feeling.equals("talking")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.talking);
            mediaPlayer.start();
        }*/
    }

    public void endMusic() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(width, 0);
        cameraView.setTransform(matrix);

        try{
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        }catch(IOException ioe){
            Log.e("camera-reverse", ioe.getMessage());
        }
    }
    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        if (null != mCamera) {
            mCamera.stopPreview();
            mCamera.release();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        cameraView = (TextureView)findViewById(R.id.cameraTextureView);
        cameraView.setSurfaceTextureListener(this);

//        Toast.makeText(getApplicationContext(), Integer.toString(i), Toast.LENGTH_SHORT).show();
        if (i==8){
            i=0;
            Toast.makeText(getApplicationContext(), "Capture!", Toast.LENGTH_SHORT).show();

            // TextureView에서 이미지 캡쳐
            Bitmap bitmap = cameraView.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] currentData = stream.toByteArray();

//            Toast.makeText(getApplicationContext(), Integer.toString(currentData.length), Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(), currentData[0], Toast.LENGTH_SHORT).show();
//            String file_name = "tem_face.jpg";
//            String string_path = "";
//            File file_path;
//            try{
//                file_path = new File(string_path);
//                if(!file_path.isDirectory()){
//                    file_path.mkdirs();
//                }
//                FileOutputStream out = new FileOutputStream(string_path+file_name);
//
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                out.close();
//            }catch(FileNotFoundException exception){
//                Log.e("FileNotFoundException", exception.getMessage());
//            }catch(IOException exception){
//                Log.e("IOException", exception.getMessage());
//            }

            try {
                String paramName = "image"; // 파라미터명은 image로 지정
//                String imgFile = "이미지 파일 경로 ";
//                File uploadFile = new File(imgFile);
                String apiURL = "https://naveropenapi.apigw.ntruss.com/vision/v1/face"; // 얼굴 감지
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);
                // multipart request
                String boundary = "---" + System.currentTimeMillis() + "---";
                con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
                OutputStream outputStream = con.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
                String LINE_FEED = "\r\n";
                // file 추가
//                String fileName = uploadFile.getName();
                String fileName = "test_file";
                writer.append("--" + boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
                writer.append("Content-Type: "  + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();
//                FileInputStream inputStream = new FileInputStream(uploadFile);
//                byte[] buffer = new byte[4096];
//                int bytesRead = -1;
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, bytesRead);
//                }
                Toast.makeText(getApplicationContext(), Integer.toString(currentData.length), Toast.LENGTH_SHORT).show();
                outputStream.write(currentData, 0, currentData.length);
                outputStream.flush();
//                inputStream.close();
                writer.append(LINE_FEED).flush();
                writer.append("--" + boundary + "--").append(LINE_FEED);
                writer.close();
                BufferedReader br = null;
                int responseCode = con.getResponseCode();
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 오류 발생
                    System.out.println("error!!!!!!! responseCode= " + responseCode);
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                }
                String inputLine;
                if(br != null) {
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();
                    System.out.println(response.toString());
                } else {
                    System.out.println("error !!!");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        else
            i++;
    }
}