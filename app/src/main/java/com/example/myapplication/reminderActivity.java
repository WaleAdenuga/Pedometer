package com.example.myapplication;

import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.ui.record.RecordFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class reminderActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    TextView text1;
    EditText edit1;
    Button button;
    Switch activate;
    String system_time = "";
    String system_date = "";
    String time_selected = "";
    int hour_selected;
    int minute_selected;
    String[] nice_statements = {"Start this week by setting walking goals and let's reach them together", "You did a good job yesterday, let's improve that by doing greater work today","Are you ready?",
    "Come on, let's go show people what you're capable of", "With your progress, you should feel good about yourself","Yesterday was great, let's kill it today", "It's the final countdown"};

    String insert_url = "https://studev.groept.be/api/a19sd704/setReminder/";
    String update_url = "https://studev.groept.be/api/a19sd704/updateReminder/";
    String get_url = "https://studev.groept.be/api/a19sd704/getReminder/";
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        reminderActivity.this.setTitle("REMINDER");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(reminderActivity.this);
        final boolean lastState = pref.getBoolean("Checked", false);
        activate = (Switch) findViewById(R.id.switch1);
        activate.setChecked(lastState);

        activate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(reminderActivity.this);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("Checked", activate.isChecked());
                editor.apply();
            }
        });


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(reminderActivity.this);
        String ID = prefs.getString("ID", null);
        queue = Volley.newRequestQueue(reminderActivity.this);
        String get = get_url + ID;
        final JsonArrayRequest json = new JsonArrayRequest(Request.Method.GET,get, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i<response.length();i++) {
                    JSONObject obj = null;
                    try {
                        obj = response.getJSONObject(i);
                        time_selected = obj.getString("TimePicked");
                        edit1.setText(time_selected);
                        ArrayList<Integer> int_time = new ArrayList<Integer>();
                        String time = edit1.getText().toString();
                        String[] text = time.split(":");
                        for (String k : text) {
                            //System.out.println(k);
                            int_time.add(Integer.parseInt(k));
                            //System.out.println(int_time);
                        }
                        System.out.println(int_time);
                        int hour = int_time.get(0);
                        int minute = int_time.get(1);
                        int second = int_time.get(2);
                        if (lastState) {
                            dailyNotifications(hour,minute,second);
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(reminderActivity.this,"Error connecting to database",Toast.LENGTH_SHORT).show();
            }
        });

        String request = insert_url + time_selected + "/" + ID;
        StringRequest submit = new StringRequest(Request.Method.GET, request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                queue.add(json);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(reminderActivity.this,"Database compromised",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(submit);


        edit1 = (EditText) findViewById(R.id.time_picker);
        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(reminderActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View time_picker = inflater.inflate(R.layout.activity_timepicker,null);
                mBuilder.setView(time_picker);
                final TimePicker picker = (TimePicker) time_picker.findViewById(R.id.dialog_time_picker);
                picker.setEnabled(true);
                picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        Log.d("Time","Time is " + String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                        hour_selected = hourOfDay;
                        minute_selected = minute;
                        if (minute <= 9) {
                            time_selected = String.valueOf(hourOfDay) + ":0" + String.valueOf(minute);
                            edit1.setText(time_selected);
                            updateSelection(time_selected);
                        } else {
                            time_selected = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
                            edit1.setText(time_selected);
                            updateSelection(time_selected);
                        }

                    }
                });

                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        button = (Button) findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //time_selected = edit1.getText().toString();
                //updateSelection(time_selected);
                finish();
            }
        });


    }

//    @Override
//    protected void onStart() {
//        updateTime();
//        super.onStart();
//    }
//
//    @Override
//    protected void onStop() {
//        updateTime();
//        super.onStop();
//    }
//
//    @Override
//    protected void onPause() {
//        updateTime();
//        super.onPause();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void dailyNotifications(int hour, int minute, int second) {


        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(this,0,intent,0);


        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,second);

        assert manager != null;
        manager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pending);
    }


    public void updateSelection(String addition) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(reminderActivity.this);
        String ID = prefs.getString("ID", null);
        queue = Volley.newRequestQueue(reminderActivity.this);
        String submit = update_url + addition + "/" + ID;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, submit, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(reminderActivity.this,"Error noticed", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);
    }
}
