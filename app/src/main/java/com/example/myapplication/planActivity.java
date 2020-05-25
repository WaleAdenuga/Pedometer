package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.os.CountDownTimer;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class planActivity extends AppCompatActivity implements SensorEventListener {

    Chronometer timer_chronometer;
    ImageButton pause;
    ImageButton reset;
    ImageButton start;
    String chronometer_format = "HH:mm:ss";
    long pausedValue;
    TextView training_Steps;
    TextView training_distance;
    TextView training_calories;

    Sensor sensor1;
    SensorManager manager;
    long steps = 0;

    float distance_calculated = 0.0f;
    float calories_calculated = 0.0f;

    float information_height = 0.0f;
    float information_weight = 0.0f;

    float stride_prop_average = 0.414f;
    float stride_length;

    float defacto_height = 1.82f;
    float defacto_weight = 109.9f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        planActivity.this.setTitle("TRAINING");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert manager != null;
        sensor1 = manager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (sensor1 == null) {
            Toast.makeText(this,"Sensor not present in phone",Toast.LENGTH_LONG).show();
        }

        training_Steps = (TextView) findViewById(R.id.training_steps);
        //training_Steps.setText(String.valueOf(steps));
        training_distance = (TextView) findViewById(R.id.training_distance);
        training_calories = (TextView) findViewById(R.id.training_calorie);
        information_height = defacto_height;
        information_weight = defacto_weight;
        stride_length = stride_prop_average * information_height;

        timer_chronometer = (Chronometer) findViewById(R.id.timer_chronometer);
        //timer_chronometer.setFormat(chronometer_format);
        start = (ImageButton) findViewById(R.id.play_button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pausedValue!=0) {
                    timer_chronometer.setBase(timer_chronometer.getBase() + SystemClock.elapsedRealtime() - pausedValue);
                } else {
                    timer_chronometer.setBase(SystemClock.elapsedRealtime());
                }
                timer_chronometer.start();
                //pause.setEnabled(true);
                //reset.setEnabled(true);
                manager.registerListener(planActivity.this,sensor1,SensorManager.SENSOR_DELAY_NORMAL);
            }
        });
        pause = (ImageButton) findViewById(R.id.imageButton);
        //pause.setEnabled(false);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausedValue = SystemClock.elapsedRealtime();
                timer_chronometer.stop();
                manager.unregisterListener(planActivity.this,sensor1);
            }
        });
        reset = (ImageButton) findViewById(R.id.stopButton);
        //reset.setEnabled(false);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer_chronometer.setBase(SystemClock.elapsedRealtime());
                timer_chronometer.stop();
                pausedValue = 0;
                String zero = "0";
                float zero2 = 0.0f;
                training_Steps.setText(String.valueOf(zero));
                training_distance.setText(String.valueOf(zero2));
                training_calories.setText(String.valueOf(zero2));
                manager.unregisterListener(planActivity.this,sensor1);
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION},1);
        };

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            float[] step_values = event.values;
            System.out.println(Arrays.toString(event.values));
            long trial = steps;
            trial = steps++;
            training_Steps.setText(String.valueOf(trial));

            distance_calculated = (trial * stride_length) / 1000; // distance is no of steps multiplies by stride length (in metres)
            distance_calculated = (float) (Math.round(distance_calculated* 100d) / 100d);
            training_distance.setText(String.valueOf(distance_calculated));

            calories_calculated = (0.57f * information_weight * trial) / 1000; // calories burned is calculated as if normal multiplier depending on weight and number of steps
            calories_calculated = (float) (Math.round(calories_calculated * 100d)/ 100d);
            System.out.println(calories_calculated);
            training_calories.setText(String.valueOf(calories_calculated));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

