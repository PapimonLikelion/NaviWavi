package com.example.naviwavi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class OptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        Intent firstPageSetting = getIntent();
        String oneName = firstPageSetting.getStringExtra("oneName");
        String oneRoadName = firstPageSetting.getStringExtra("oneRoadName");
        String twoName = firstPageSetting.getStringExtra("twoName");
        String twoRoadName = firstPageSetting.getStringExtra("twoRoadName");
        String threeName = firstPageSetting.getStringExtra("threeName");
        String threeRoadName = firstPageSetting.getStringExtra("threeRoadName");

        Button optionOne = (Button)findViewById(R.id.optionOne);
        optionOne.setText(oneName + "\n" + oneRoadName);
        Button optionTwo = (Button)findViewById(R.id.optionTwo);
        optionOne.setText(twoName + "\n" + twoRoadName);
        Button optionThree = (Button)findViewById(R.id.optionThree);
        optionOne.setText(threeName + "\n" + threeRoadName);
    }
}