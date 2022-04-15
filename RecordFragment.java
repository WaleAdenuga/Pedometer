package com.example.myapplication.ui.record;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.lifetimeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class RecordFragment extends Fragment {
    ListView listView;
    ArrayAdapter<String> adapter;
    String[] categories = {"LIFETIME RECORDS"};

    TextView monthlySteps;
    TextView monthlyDistance;
    TextView monthlyCalories;
    TextView monthlyTime;
    CalendarView calendarView;

    String getSameDay = "https://studev.groept.be/api/a19sd704/getSameDayData/";
    String getMonthData = "https://studev.groept.be/api/a19sd704/getMonthDataDate/";
    String getLaunchData = "https://studev.groept.be/api/a19sd704/getLaunchData/";
    RequestQueue queue;

    String steps_daily;
    String distance_daily;
    String calories_daily;
    String time_daily;
    Context context;
    String clicked_date;

    ArrayList<String> monthly = new ArrayList<String>();
    ArrayList<String> dist = new ArrayList<String>();
    ArrayList<String> calc = new ArrayList<String>();
    ArrayList<Integer> stp_m = new ArrayList<Integer>();
    ArrayList<Double> dist_m = new ArrayList<Double>();
    ArrayList<Double> calc_m = new ArrayList<Double>();
    ArrayList<String> time_d = new ArrayList<String>();
    ArrayList<String> time_m = new ArrayList<String>();
    ArrayList<String> hour = new ArrayList<String> ();
    ArrayList<String> hour_separated = new ArrayList<>();
    ArrayList<String> minute_separated = new ArrayList<>();
    ArrayList<Integer> actual_hour = new ArrayList<>();
    ArrayList<Integer> actual_minute = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_record, container, false);
        context = getContext();


        listView = (ListView) root.findViewById(R.id.dynamic_text);
        adapter = new ArrayAdapter<>(requireContext(),android.R.layout.simple_list_item_1,categories);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(requireContext(), lifetimeActivity.class);
                startActivity(intent);
            }
        });

        queue = Volley.newRequestQueue(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String ID = prefs.getString("ID", null);
        Date date= new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH); //REMEMBER IT STARTS FROM 0 FOR JANUARY!!
        int year = cal.get(Calendar.YEAR);
        String first_date = year + "-" + (month+1) + "-01" ;
        String second_date = year + "-" + (month+2) + "-01";
        String url = getMonthData + first_date + "/" + second_date + "/" +ID;
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i<response.length(); i++) {

                    JSONObject obj = null;
                    try {
                        obj = response.getJSONObject(i);
                        monthly.add(obj.getString("Steps"));
                        dist.add(obj.getString("Distance"));
                        calc.add(obj.getString("Calories"));
                        time_d.add(obj.getString("MinsWalked"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for (String k : time_d) {
                    time_m.add(k);
                }

                for (String s: time_m) {
                    String[] split = s.split(" ");
                    for (String k: split) {
                        hour.add(k);
                    }
                }

                for (int i = 0; i<hour.size(); i++) {
                    if (i % 2 == 1) {
                        minute_separated.add(hour.get(i));
                    } else {
                        hour_separated.add(hour.get(i));
                    }
                }
                for (String l : minute_separated) {
                    String[] split = l.split("m");
                    for (String i : split) {
                        actual_minute.add(Integer.parseInt(i));
                    }
                }

                for (String l : hour_separated) {
                    String[] split = l.split("h");
                    for (String i : split) {
                        actual_hour.add(Integer.parseInt(i));
                    }
                }

                int sum_hours = 0;
                int sum_minute = 0;
                for (int i : actual_hour) {
                    sum_hours += i;
                }
                for (int i : actual_minute) {
                    sum_minute += i;
                }
                float d = ((float) sum_minute )/ 60;
                BigDecimal decimal = new BigDecimal(String.valueOf(d));
                int initial_h = decimal.intValue();
                int final_hours = sum_hours + initial_h;
                float left = (float) (Math.round((d - initial_h)*100d) / 100d);
                int final_m = Math.round((left * 60));

                String final_time = final_hours + "h " + final_m + "m";
                monthlyTime.setText(final_time);


                for (String i : monthly) {
                    stp_m.add(Integer.parseInt(i));
                }
                for (String i : dist) {
                    dist_m.add(Double.parseDouble(i));
                }
                for (String i : calc) {
                    calc_m.add(Double.parseDouble(i));
                }
                int sum_steps = 0;
                int sum_distance = 0;
                int sum_calories = 0;

                for (int i : stp_m) {
                    sum_steps += i;
                }
                for (double i : dist_m) {
                    sum_distance += i;
                }
                for (double i :calc_m) {
                    sum_calories += i;
                }

                monthlySteps.setText(String.valueOf(sum_steps));
                monthlyDistance.setText(String.valueOf(sum_distance));
                monthlyCalories.setText(String.valueOf(sum_calories));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Couldn't retrieve values", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonArrayRequest);

        calendarView = (CalendarView) root.findViewById(R.id.reportCalendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                queue = Volley.newRequestQueue(context);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String ID = prefs.getString("ID", null);

                if (month <= 9) {
                    clicked_date = year + "-0" + (month+1) + "-" + dayOfMonth;
                } else {
                    clicked_date = year + "-" + (month+1) + "-" + dayOfMonth;
                }

                System.out.println(clicked_date);

                String get_url = getSameDay + ID +"/" + clicked_date;
                final JsonArrayRequest animate = new JsonArrayRequest(Request.Method.GET, get_url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i<response.length(); i++) {
                            JSONObject o = null;
                            try {
                                o = response.getJSONObject(i);
                                steps_daily = o.getString("Steps");
                                distance_daily = o.getString("Distance");
                                calories_daily = o.getString("Calories");
                                time_daily = o.getString("MinsWalked");

                                monthlySteps.setText(steps_daily);
                                monthlyDistance.setText(distance_daily);
                                monthlyCalories.setText(calories_daily);
                                monthlyTime.setText(time_daily);

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
                queue.add(animate);
            }
        });

        monthlySteps = (TextView) root.findViewById(R.id.total_steps_calc);
        monthlyDistance = (TextView) root.findViewById(R.id.distance_calc_monthly);
        monthlyCalories = (TextView) root.findViewById(R.id.calorie_calc_monthly);
        monthlyTime = (TextView) root.findViewById(R.id.time_monthly);

        return root;
    }



}
