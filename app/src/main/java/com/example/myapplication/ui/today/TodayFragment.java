package com.example.myapplication.ui.today;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.planActivity;
import com.example.myapplication.preferenceActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TodayFragment extends Fragment implements SensorEventListener {

    Sensor sensor4;
    Sensor sensor5;
    SensorManager manager;
    private Button pauseButton;
    private Button resumeButton;
    private TextView stepCounter;
    TextView distance_calc;
    TextView calorie_calc;
    TextView time_calc;
    TextView steps_string;
    FloatingActionButton planButton;
    TextView goal_string;
    TextView distance_unit;

    float[] step_values;
    long steps;
    float distance_calculated = 0.0f;
    float calories_calculated = 0.0f;

    String launch_unit;
    float height_launch = 0.0f;
    String height_launch_string;
    float weight_launch = 0.0f;
    String weight_launch_string;
    String launch_daily_goal;
    String sensitivity = "Medium";

    float stride_prop_average = 0.414f;
    float average_human_walking_speed = 4.5f;
    float average_human_walking_speed_miles = 3.1f;
    double decimal_time = 0.0;
    float stride_length;
    String time_display = "0h 0m";

    String launchData = "https://studev.groept.be/api/a19sd704/getLaunchData/";
    String goal = "https://studev.groept.be/api/a19sd704/getPreference/";
    String addData = "https://studev.groept.be/api/a19sd704/stepsInitial/";
    String updateData = "https://studev.groept.be/api/a19sd704/sensorChangeData/";
    String getData = "https://studev.groept.be/api/a19sd704/getSameDayData/";
    String getPreference = "https://studev.groept.be/api/a19sd704/getPreference/";

    RequestQueue queue;
    Context context;
    int goal_int;
    boolean firstState = false;
    boolean only = false;
    int check;
    ArrayList<Float> size_steps = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_today, container, false);
        updateTime();

        manager = (SensorManager) this.requireActivity().getSystemService(Context.SENSOR_SERVICE);
        assert manager != null;
        context = getContext();

        sensor4 = manager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensor5 = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        steps_string = (TextView) root.findViewById(R.id.steps_string);
        goal_string = (TextView) root.findViewById(R.id.goal_counter);
        distance_unit = (TextView) root.findViewById(R.id.distance_unit);

        queue = Volley.newRequestQueue(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String ID = prefs.getString("ID", null);

        String launch_url = launchData + ID;
        final JsonArrayRequest json = new JsonArrayRequest(Request.Method.GET, launch_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i<response.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = response.getJSONObject(i);
                        height_launch_string = obj.getString("Height");
                        weight_launch_string = obj.getString("Weight");
                        launch_unit = obj.getString("Unit");
                        height_launch = Float.parseFloat(height_launch_string);
                        weight_launch = Float.parseFloat(weight_launch_string);
                        stride_length = stride_prop_average * height_launch;

                        if (launch_unit.equals("Imperial")) {
                            String miles = "Miles";
                            distance_unit.setText(miles);
                            distance_calculated = (float) (Math.round((distance_calculated / 1.609f)*100d)/100d);

                            //average_human_walking_speed = 3.1f;
                        }

                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(),"Null database", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(json);


        Date current = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String system_date = sdf.format(current);
        String get_url = getData + ID +"/" + system_date;
        final JsonArrayRequest animate = new JsonArrayRequest(Request.Method.GET, get_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i<response.length(); i++) {
                    JSONObject o = null;
                    try {
                        o = response.getJSONObject(i);
                        steps = Long.parseLong(o.getString("Steps"));
                        distance_calculated = Float.parseFloat(o.getString("Distance"));
                        calories_calculated = Float.parseFloat(o.getString("Calories"));
                        time_display = o.getString("MinsWalked");



                        stepCounter.setText(String.valueOf(steps));
                        distance_calc.setText(String.valueOf(distance_calculated));
                        calorie_calc.setText(String.valueOf(calories_calculated));
                        time_calc.setText(time_display);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(), "Couldn't connect to database",Toast.LENGTH_SHORT).show();
            }
        });
        SharedPreferences prefss = PreferenceManager.getDefaultSharedPreferences(context);
        boolean state = prefss.getBoolean("Performed",false);
        if (!state) {
            String add_url = addData + String.valueOf(steps) + "/" + String.valueOf(distance_calculated) + "/" + String.valueOf(calories_calculated) + "/" + system_date + "/" + ID + "/" + time_display;
            final StringRequest submit = new StringRequest(Request.Method.GET, add_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    queue.add(animate);
                    firstState = true;
                    SharedPreferences prefss = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefss.edit();
                    editor.putBoolean("Performed", firstState);
                    editor.apply();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(submit);
        }
        queue.add(animate);
        updateCertainTime();

        String goal_url = goal + ID;
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, goal_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i<response.length(); i++) {
                    JSONObject o = null;
                    try {
                        o = response.getJSONObject(i);
                        launch_daily_goal = o.getString("Step Goal");
                        goal_string.setText(launch_daily_goal);
                        goal_int = Integer.parseInt(launch_daily_goal);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(), "Couldn't retrieve information", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonArrayRequest);


        distance_calc = (TextView) root.findViewById(R.id.distance_calc);
        //distance_calc.setText(String.valueOf(distance_calculated));

        calorie_calc = (TextView) root.findViewById(R.id.calorie_calc);
        //calorie_calc.setText(String.valueOf(calories_calculated));

        time_calc = (TextView) root.findViewById(R.id.time_calc);
        //time_calc.setText(time_display);

        stepCounter = (TextView) root.findViewById(R.id.number_steps);
        //stepCounter.setText(String.valueOf(steps));



        resumeButton = (Button) root.findViewById(R.id.resume_button);
        resumeButton.setEnabled(false);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeButton.setEnabled(false);
                pauseButton.setEnabled(true);

                String steps = "Steps";
                steps_string.setText(steps);
                manager.registerListener(TodayFragment.this,sensor4,SensorManager.SENSOR_DELAY_NORMAL);

            }
        });
        pauseButton = (Button) root.findViewById(R.id.button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeButton.setEnabled(true);
                pauseButton.setEnabled(false);
                String paused = "Paused";
                steps_string.setText(paused);
                manager.unregisterListener(TodayFragment.this,sensor4);

            }
        });

        planButton = (FloatingActionButton) root.findViewById(R.id.plan_button);
        planButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), planActivity.class);
                startActivity(intent);
            }
        });

        getSensitivity();

        if(sensor4 != null) {
            manager.registerListener(this,sensor4,check);
        } else {
            Toast.makeText(requireContext(),"Sensor not in phone",Toast.LENGTH_LONG).show();
        }

        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION},1);
        };


        return root;
    }


    @Override
    public void onResume() {
        if(sensor4 != null) {
            manager.registerListener(this,sensor4, check);
        } else {
            Toast.makeText(requireContext(),"Sensor not in phone",Toast.LENGTH_LONG).show();
        }
        updateTime();
        updateCertainTime();
        showNotification();
        super.onResume();
    }

    @Override
    public void onPause() {
        if(sensor4 != null) {
            manager.registerListener(this,sensor4, check);
        } else {
            Toast.makeText(requireContext(),"Sensor stopped working",Toast.LENGTH_LONG).show();
        }
        updateTime();
        updateCertainTime();
        showNotification();
        super.onPause();
    }

    @Override
    public void onStop() {
        if(sensor4 != null) {
            manager.registerListener(this,sensor4, check);
        } else {
            Toast.makeText(requireContext(),"Sensor stopped working",Toast.LENGTH_LONG).show();
        }
        updateTime();
        updateCertainTime();
        showNotification();
        super.onStop();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            size_steps.add(event.values[0]);
            long trial = steps;
            trial = steps++;
            stepCounter.setText(String.valueOf(steps));

            distance_calculated = (trial * stride_length) / 1000; // distance is no of steps multiplies by stride length (in metres)
            distance_calculated = (float) (Math.round(distance_calculated * 100d) / 100d);
            distance_calc.setText(String.valueOf(distance_calculated));

            calories_calculated = (0.57f * weight_launch * trial) / 1000; // calories burned is calculated as if normal multiplier depending on weight and number of steps
            calories_calculated = (float) (Math.round(calories_calculated * 100d) / 100d);
            calorie_calc.setText(String.valueOf(calories_calculated));

            decimal_time = (distance_calculated / average_human_walking_speed); // (km/Km/hr), getting walking time in hour
            decimal_time = Math.round(decimal_time * 100d) / 100d;
            BigDecimal number = new BigDecimal(String.valueOf(decimal_time));
            int hour = number.intValue();
            double minute_double = Math.round((decimal_time - hour) * 60);
            int minute = (int) minute_double;
            String final_time = hour + "h" + " " + minute + "m";
            time_display = final_time;
            time_calc.setText(final_time);
        }

        //updateStepData(String.valueOf(steps),String.valueOf(distance_calculated), time_display,String.valueOf(calories_calculated));

    }

    public void getSensitivity() {
        queue = Volley.newRequestQueue(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String ID = prefs.getString("ID", null);
        String tests_url = getPreference + ID;
        final JsonArrayRequest test = new JsonArrayRequest(Request.Method.GET, tests_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i<response.length(); i++) {
                    JSONObject o = null;
                    try {
                        o = response.getJSONObject(i);
                        sensitivity = o.getString("Sensitivity");
                        if (sensitivity.equals("Low")) {
                            check = SensorManager.SENSOR_STATUS_ACCURACY_LOW;
                        }
                        if (sensitivity.equals("Medium")) {
                            check = SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
                        }
                        if (sensitivity.equals("High")) {
                            check = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Couldn't get your sensitivity",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(test);
    }



    public void updateCertainTime() {

        final Handler someHandler = new Handler(Looper.getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                someHandler.postDelayed(this,10000);
                System.out.println(size_steps.size());
                updateStepData(String.valueOf(steps),String.valueOf(distance_calculated), time_display,String.valueOf(calories_calculated));
            }
        },10);

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void updateStepData(String addition, String plus, String minus, String burned) {
            Date current = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String system_date = sdf.format(current);
            //queue = Volley.newRequestQueue(context);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String ID = prefs.getString("ID", null);

            String update_url = updateData + addition + "/" + plus + "/" + minus + "/" + burned + "/" + ID + "/" + system_date;
            StringRequest submit = new StringRequest(Request.Method.GET, update_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(requireContext(),"Update failed", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(submit);
    }

    public void showNotification() {

        if (size_steps.size() >= Integer.parseInt(goal_string.getText().toString())) {
            Intent intent2 = new Intent(context, MainActivity.class);
            PendingIntent notificationIntent = PendingIntent.getActivity(context,0,intent2,0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                int notifyID = 62;
                String notification_ID = "GoalChannel";
                CharSequence name = "GoalActivity";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel mChannel = new NotificationChannel(notification_ID, name, importance);

                Notification notification = new NotificationCompat.Builder(context, notification_ID)
                        .setSmallIcon(R.drawable.ic_feet)
                        .setContentTitle("CONGRATULATIONS")
                        .setContentText("You've reached your daily goal for today! Keep up the good work")
                        .setContentIntent(notificationIntent)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .build();

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(mChannel);
                mNotificationManager.notify(27,notification);
            }
        }
    }


    public void updateTime() {

        final Handler someHandler = new Handler(Looper.getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String current_time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                someHandler.postDelayed(this,1000);
                String midnight = "00:00:02";

                if (current_time.equals(midnight)) {
                    steps = 0;
                    distance_calculated = 0.0f;
                    calories_calculated = 0.0f;
                    String reset_time = "0h 0m";
                    stepCounter.setText(String.valueOf(steps));
                    distance_calc.setText(String.valueOf(distance_calculated));
                    calorie_calc.setText(String.valueOf(calories_calculated));
                    time_calc.setText(reset_time);

                    Date current = Calendar.getInstance().getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String system_date = sdf.format(current);
                    //queue = Volley.newRequestQueue(context);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String ID = prefs.getString("ID", null);
                    String get_url = getData + ID +"/" + system_date;
                    final JsonArrayRequest animate = new JsonArrayRequest(Request.Method.GET, get_url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i<response.length(); i++) {
                                JSONObject o = null;
                                try {
                                    o = response.getJSONObject(i);
                                    steps = Long.parseLong(o.getString("Steps"));
                                    distance_calculated = Float.parseFloat(o.getString("Distance"));
                                    calories_calculated = Float.parseFloat(o.getString("Calories"));
                                    time_display = o.getString("MinsWalked");

                                    stepCounter.setText(String.valueOf(steps));
                                    distance_calc.setText(String.valueOf(distance_calculated));
                                    calorie_calc.setText(String.valueOf(calories_calculated));
                                    time_calc.setText(time_display);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(requireContext(), "Couldn't connect to database",Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (!only) {

                        String add_url = addData + String.valueOf(steps) + "/" + String.valueOf(distance_calculated) + "/" + String.valueOf(calories_calculated) + "/" + system_date + "/" + ID + "/" + reset_time;
                        StringRequest submit = new StringRequest(Request.Method.GET, add_url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                only = true;
                                queue.add(animate);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        queue.add(submit);
                    }
                    queue.add(animate);


                }
                //System.out.println(current_time);
            }
        },10);
    }





}
