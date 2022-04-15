package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public class lifetimeActivity extends AppCompatActivity {

    String getLifetimeData = "https://studev.groept.be/api/a19sd704/getLongDataDate/";
    RequestQueue queue;
    TextView lifetime_steps;
    TextView lifetime_distance;
    TextView life_time_calories;
    TextView most_steps;
    TextView most_distance;
    TextView most_logins;
    TextView training_number;
    TextView calorie_number;
    TextView longest_time;


    Context context;

    ArrayList<String> steps = new ArrayList<String>();
    ArrayList<String> dist = new ArrayList<String>();
    ArrayList<String> calc = new ArrayList<String>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<Integer> step_l = new ArrayList<Integer>();
    ArrayList<Double> dist_l = new ArrayList<Double>();
    ArrayList<Double> calc_l = new ArrayList<Double>();
    ArrayList<String> time_l = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifetime);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        lifetimeActivity.this.setTitle("LIFETIME RECORDS");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        queue = Volley.newRequestQueue(lifetimeActivity.this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(lifetimeActivity.this);
        String ID = prefs.getString("ID", null);
        String url = getLifetimeData +ID;
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i<response.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = response.getJSONObject(i);
                        steps.add(obj.getString("Steps"));
                        dist.add(obj.getString("Distance"));
                        calc.add(obj.getString("Calories"));
                        time.add(obj.getString("MinsWalked"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                for (String i : steps) {
                    step_l.add(Integer.parseInt(i));
                }
                for (String i : dist) {
                    dist_l.add(Double.parseDouble(i));
                }
                for (String i : calc) {
                    calc_l.add(Double.parseDouble(i));
                }
                for (String i: time) {
                    time_l.add(i);
                }
                int sum_steps = 0;
                int sum_distance = 0;
                int sum_calories = 0;

                for (int i : step_l) {
                    sum_steps += i;
                }
                for (double i : dist_l) {
                    sum_distance += i;
                }
                for (double i :calc_l) {
                    sum_calories += i;
                }

                String cal_unit = " Kcal";
                String dist_unit = " KM";
                String days = " days";

                String lifetime_distances = String.valueOf(sum_distance) + dist_unit;
                String lifetime_calories = sum_calories + cal_unit;
                String most_calories = Collections.max(calc_l) + cal_unit;
                String most_distances = Collections.max(dist_l)+ dist_unit;
                String login_days = step_l.size() + days;

                int index = step_l.indexOf(Collections.max(step_l));
                longest_time.setText(time_l.get(index));

                lifetime_steps.setText(String.valueOf(sum_steps));
                most_steps.setText(String.valueOf(Collections.max(step_l)));
                lifetime_distance.setText(lifetime_distances);
                most_distance.setText(most_distances);
                life_time_calories.setText(lifetime_calories);
                calorie_number.setText(most_calories);
                most_logins.setText(login_days);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Couldn't retrieve values", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonArrayRequest);

        lifetime_steps = (TextView) findViewById(R.id.steps_int_lifetime);
        lifetime_distance = (TextView) findViewById(R.id.distance_int_lifetime);
        life_time_calories = (TextView) findViewById(R.id.calories_int_lifetime);
        most_steps = (TextView) findViewById(R.id.daily_int_steps);
        most_distance = (TextView) findViewById(R.id.daily_int_distance);
        calorie_number = (TextView) findViewById(R.id.goals_reached_int);
        most_logins = (TextView) findViewById(R.id.final_grade_string);
        longest_time = (TextView) findViewById(R.id.daily_int_time);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);

    }
}
