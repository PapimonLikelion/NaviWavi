package com.example.naviwavi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionActivity extends AppCompatActivity {
    public String oneName;
    public String twoName;
    public String threeName;
    public String oneRoadName;
    public String twoRoadName;
    public String threeRoadName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        /* 부모 인텐트에서 받아오기 */
        Intent firstPageSetting = getIntent();
        oneName = firstPageSetting.getStringExtra("oneName");
        oneRoadName = firstPageSetting.getStringExtra("oneRoadName");
        twoName = firstPageSetting.getStringExtra("twoName");
        twoRoadName = firstPageSetting.getStringExtra("twoRoadName");
        threeName = firstPageSetting.getStringExtra("threeName");
        threeRoadName = firstPageSetting.getStringExtra("threeRoadName");

        writeToButton();
    }

    private void writeToButton() {
        Button optionOne = (Button) findViewById(R.id.optionOne);
        optionOne.setText(oneName + "\n" + oneRoadName);
        Button optionTwo = (Button) findViewById(R.id.optionTwo);
        optionTwo.setText(twoName + "\n" + twoRoadName);
        Button optionThree = (Button) findViewById(R.id.optionThree);
        optionThree.setText(threeName + "\n" + threeRoadName);
    }

    public void chooseOne(View v) {
        FirstPageActivity.userChosenOption = 1;
        FirstPageActivity.finalDestination = oneName;
        finish();
    }

    public void chooseTwo(View v) {
        FirstPageActivity.userChosenOption = 2;
        FirstPageActivity.finalDestination = twoName;
        finish();

    }

    public void chooseThree(View v) {
        FirstPageActivity.userChosenOption = 3;
        FirstPageActivity.finalDestination = threeName;
        finish();
    }
}