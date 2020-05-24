package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class instructionsActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    MainAdapter adapter;
    String[] group = {"How to use?", "Stops Counting?", "Counting when shaking phone?", "Counting when driving?",
            "Accuracy", "Placement Suggestion", "Battery Saving", "Privacy", "Calories & Distance", "Step Goal"};
    HashMap<String,List<String>> item;
    Map<String,String> trial = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        instructionsActivity.this.setTitle("INSTRUCTIONS");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        expandableListView = (ExpandableListView) findViewById(R.id.expandable_one);
        trial.put("How to use?","Click PAUSE button on home page to stop counting so as to reduce power consumption, Please keep your phone with you for accurate results");
        trial.put("Stops Counting?","Pedometer can be forcibly stopped due to the limitation of some devices.");
        trial.put("Counting when shaking phone?","Because of the built in sensor, it is not a bug if shaking your phone leads to additional steps");
        trial.put("Counting when driving?","Tap the PAUSE button when going for a long drive if this problem occurs. This is because of system restrictions and driving conditions");
        trial.put("Accuracy","We use the built-in sensor. The accuracy of the results is based on how good the sensor is");
        trial.put("Placement Suggestion","We recommend placing in areas not far away from the body like your hand pocket or bag.");
        trial.put("Battery Saving","You can pause the app to save battery when you're not walking");
        trial.put("Privacy","There is no personal data collection or data sharing with any 3rd party. We are not Facebook");
        trial.put("Calories & Distance","Your height and weight are used to calculate burned calories and distance. Inputting accurate information is strongly advised");
        trial.put("Step Goal","We help to set your goal to keep you properly motivated for future achievements.");
        adapter = new MainAdapter(this,group,trial);
        expandableListView.setAdapter(adapter);
        //expandableListView.setPadding(10,10,10,10);
        expandableListView.setIndicatorBounds(0,100);
        //expandableListView.setChildIndicatorBounds(0,100);
        item = new HashMap<>();
//        addStuff();
    }

    public void addStuff() {

        group = new String[]{"How to use?", "Stops Counting?", "Counting when shaking phone?", "Counting when driving?",
                "Accuracy", "Placement Suggestion", "Battery Saving", "Privacy", "Calories & Distance", "Step Goal"};

        adapter.notifyDataSetChanged();

        String[] array;

        List<String> list1 = new ArrayList<>();
        array = getResources().getStringArray(R.array.bigtext1);
        for(String item: array) {
            list1.add(item);
        }

        List<String> list2 = new ArrayList<>();
        array = getResources().getStringArray(R.array.bigtext2);
        for(String item: array) {
            list2.add(item);
        }
        List<String> list3 = new ArrayList<>();
        array = getResources().getStringArray(R.array.bigtext3);
        for(String item: array) {
            list3.add(item);
        }
        List<String> list4 = new ArrayList<>();
        array = getResources().getStringArray(R.array.bigtext4);
        for(String item: array) {
            list4.add(item);
        }
        List<String> list5 = new ArrayList<>();
        array = getResources().getStringArray(R.array.bigtext5);
        for(String item: array) {
            list5.add(item);
        }
        List<String> list6 = new ArrayList<>();
        array = getResources().getStringArray(R.array.bigtext6);
        for(String item: array) {
            list6.add(item);
        }
        List<String> list7 = new ArrayList<>();
        array = getResources().getStringArray(R.array.bigtext7);
        for(String item: array) {
            list7.add(item);
        }
        List<String> list8 = new ArrayList<>();
        array = getResources().getStringArray(R.array.bigtext8);
        for(String item: array) {
            list8.add(item);
        }
        List<String> list9 = new ArrayList<>();
        array = getResources().getStringArray(R.array.bigtext9);
        for(String item: array) {
            list9.add(item);
        }
        List<String> list10 = new ArrayList<>();
        array = getResources().getStringArray(R.array.bigtext10);
        for(String item: array) {
            list10.add(item);
        }

        item.put(group[0],list1);
        item.put(group[1],list2);
        item.put(group[2],list3);
        item.put(group[3],list4);
        item.put(group[4],list5);
        item.put(group[5],list6);
        item.put(group[6],list7);
        item.put(group[7],list8);
        item.put(group[8],list9);
        item.put(group[9],list10);

        adapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);
    }
}
