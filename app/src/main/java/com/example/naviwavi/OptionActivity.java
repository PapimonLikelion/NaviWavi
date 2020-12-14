package com.example.naviwavi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class OptionActivity extends AppCompatActivity {
    public String oneName;
    public String twoName;
    public String threeName;
    public String oneRoadName;
    public String twoRoadName;
    public String threeRoadName;

    Context mContext;

    public OptionActivity() {}

    public OptionActivity(Context context) {
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        /* 부모 인텐트에서 받아오기 */
        ArrayList<String> nameAndRoadName = (ArrayList<String>) getIntent().getSerializableExtra("nameAndRoadName");

        if(nameAndRoadName.size() >= 2) {
            oneName = nameAndRoadName.get(0);
            oneRoadName = nameAndRoadName.get(1);
        }
        if(nameAndRoadName.size() >= 4) {
            twoName = nameAndRoadName.get(2);
            twoRoadName = nameAndRoadName.get(3);
        }
        if(nameAndRoadName.size() >= 6) {
            threeName = nameAndRoadName.get(4);
            threeRoadName = nameAndRoadName.get(5);
        }
        writeToButton();
    }

    private void writeToButton() {
        Button optionOne = (Button) findViewById(R.id.optionOne);
        optionOne.setText(oneName + "\n" + oneRoadName);
        Button optionTwo = (Button) findViewById(R.id.optionTwo);
        optionTwo.setText(twoName + "\n" + twoRoadName);
        if(twoName == null) {
            optionTwo.setEnabled(false);
        }
        Button optionThree = (Button) findViewById(R.id.optionThree);
        optionThree.setText(threeName + "\n" + threeRoadName);
        if(threeName == null) {
            optionThree.setEnabled(false);
        }
    }

    public void chooseOne(View v) {
        FirstPageActivity.userChosenOption = 0;
        FirstPageActivity.finalDestination = oneName;
        FirstPageActivity.searchDone = true;
        finish();
    }

    public void chooseTwo(View v) {
        FirstPageActivity.userChosenOption = 1;
        FirstPageActivity.finalDestination = twoName;
        FirstPageActivity.searchDone = true;
        finish();

    }

    public void chooseThree(View v) {
        FirstPageActivity.userChosenOption = 2;
        FirstPageActivity.finalDestination = threeName;
        FirstPageActivity.searchDone = true;
        finish();
    }
}