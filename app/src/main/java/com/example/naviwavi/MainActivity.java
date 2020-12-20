package com.example.naviwavi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    /* 지도 관련 */
    private static String appkey = "l7xx7605cdc317c24677991d6d78a182ff5f\n"; //앱키
    private static TMapView tMapView; //지도객체
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private double stopNavigationThreshold = 0.001;
    private static List<String> emotion_li = new ArrayList<>();
    private static int ZOOM_LEVEL = 14;
    private static int LOCATION_RENEWAL_TIME_ = 2000;  //단위는 ms

    /* 음악 재생 모듈 */
    public MediaPlayer mediaPlayer;

    /* 캡쳐 관련 */
    static int cnt = 0;
    static int cnt_limit_sec = 30;
    static int cnt_term = 10;

    private Camera mCamera;
    OutputStream stream;

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
        tMapView.setZoomLevel(ZOOM_LEVEL);
        tMapView.setCompassMode(true);
        tMapView.setTrackingMode(true);
        linearLayoutTmap.addView(tMapView);
        Intent firstPageSetting = getIntent();

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
                Toast.makeText(getApplicationContext(), new Double(startLatitude).toString(), Toast.LENGTH_SHORT).show();
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
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_RENEWAL_TIME_, 1, mLocationListener);
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_RENEWAL_TIME_, 1, mLocationListener);
    }

    public void showRoute() {
        if (Math.abs(startLatitude - endLatitude) < stopNavigationThreshold
                && Math.abs(startLongitude - endLongitude) < stopNavigationThreshold) {
            Toast.makeText(getApplicationContext(), "목적지에 도착하였습니다\n경로 안내를 종료합니다", Toast.LENGTH_SHORT).show();
            finish();
            /* 음악 모듈 제거 */
            if(mediaPlayer != null) {
                mediaPlayer.stop();
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
    public void playMusic(String feeling) throws IOException {
        if(mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer = null;
            return;
        }

        if (feeling.equals("neutral")) {
            return;
        } else if (feeling.equals("anger")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.anger);
            mediaPlayer.start();
        } else if (feeling.equals("sad")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.sad);
            mediaPlayer.start();
        } else if (feeling.equals("surprise")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.surprise);
            mediaPlayer.start();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
                mediaPlayer = null;
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(width, 0);
        cameraView.setTransform(matrix);
        mCamera.setDisplayOrientation(90);
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
        if (cnt==cnt_limit_sec){
            cnt=0;
            // TextureView에서 이미지 캡쳐
            Bitmap bitmap = cameraView.getBitmap();
            String tmp_file_name = "/tmp_face.jpg";
            File tmp_filepath = getCacheDir();

            File tempFile = new File(tmp_filepath, tmp_file_name);
            try {
                tempFile.createNewFile();
                FileOutputStream out = new FileOutputStream(tempFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                String paramName = "image"; // 파라미터명은 image로 지정
                String imgFile = tmp_filepath.toString() + tmp_file_name;
                File uploadFile = new File(imgFile);
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
                String fileName = uploadFile.getName();
                writer.append("--" + boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
                writer.append("Content-Type: "  + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();
                FileInputStream inputStream = new FileInputStream(uploadFile);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                inputStream.close();
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
                JSONObject jsonObject;
                if(br != null) {
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();
                    System.out.println(response.toString());
                    jsonObject = new JSONObject(String.valueOf(response));
                    JSONArray faceInfoArray = (JSONArray) jsonObject.get("faces");
                    JSONObject faceObject = (JSONObject) faceInfoArray.get(0);
                    JSONObject emotionObject = (JSONObject) faceObject.getJSONObject("emotion");

                    Toast.makeText(getApplicationContext(), emotionObject.getString("value"), Toast.LENGTH_SHORT).show();
                    String tmp_emotion = emotionObject.getString("value");

                    if (tmp_emotion.isEmpty())
                        emotion_li.add("neutral");
                    else
                        emotion_li.add(emotionObject.getString("value"));

                    if (emotion_li.size() == cnt_term){
                        String getEmotion = findEmotion(emotion_li);
                        playMusic(getEmotion);
                        TextView tmp_textView = (TextView) findViewById(R.id.textView3);
                        tmp_textView.setText(emotion_li.toString());
                        emotion_li.remove(0);
                    }
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
            cnt++;
    }

    public String findEmotion(List<String> stringList){
        int anger_cnt = 0;
        int sad_cnt = 0;
        int surprise_cnt = 0;
        int neutral_cnt = 0;

        for (int i=0; i < stringList.size(); i++){
            switch (stringList.get(i)){
                case "anger":
                    anger_cnt++;
                    break;
                case "disgust":
                    anger_cnt++;
                    break;
                case "sad":
                    sad_cnt++;
                    break;
                case "fear":
                    sad_cnt++;
                    break;
                case "surprise":
                    surprise_cnt++;
                    break;
                default:
                    neutral_cnt++;
                    break;
            }
        }
        List<Integer> emotion_cnt_li = Arrays.asList(neutral_cnt, anger_cnt, sad_cnt, surprise_cnt);
        int predicted_emotion = getIndexOfLargest(emotion_cnt_li);

        if (predicted_emotion == 0)
            return "neutral";
        else if(predicted_emotion == 1)
            return "anger";
        else if(predicted_emotion == 2)
            return "sad";
        else if(predicted_emotion == 3)
            return "surprise";

        return "neutral";
    };

    public static int getIndexOfLargest( List<Integer> list ){
        if ( list == null || list.size() == 0 ) return -1; // null or empty
        Integer i=0, maxIndex=-1, max=null;
        for (Integer x : list) {
            if ((x!=null) && ((max==null) || (x>max))) {
                max = x;
                maxIndex = i;
            }
            i++;
        }
        return maxIndex;
    }
}
