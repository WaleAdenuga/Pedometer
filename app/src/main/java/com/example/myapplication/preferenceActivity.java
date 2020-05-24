package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class preferenceActivity extends AppCompatActivity {

    String[] list_topics = {"Step Goal", "Sensitivity"};
    ArrayAdapter<String> adapter;
    String[] sensitivity_options;
    String[] step_goal_options;
    ListView listView;
    TextView goal;
    TextView sensitivity;
    int checked = 0;
    String settings_goal = "10000";
    String sensitivity_goal = "Medium";

    String goal_url = "https://studev.groept.be/api/a19sd704/setGoalStuff/";
    String update_goal = "https://studev.groept.be/api/a19sd704/updateGoal/";
    String get_stuff = "https://studev.groept.be/api/a19sd704/getPreference/";
    String update_sensitivity= "https://studev.groept.be/api/a19sd704/updateSensitivity/";
    RequestQueue queue;

    public String getSettings_goal() {
        return settings_goal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        preferenceActivity.this.setTitle("PREFERENCE SETTINGS");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preferenceActivity.this);
        String ID = prefs.getString("ID", null);
        //System.out.println(id);
        queue = Volley.newRequestQueue(preferenceActivity.this);
        String getURL = get_stuff + ID;
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i<response.length(); i++) {
                    JSONObject o = null;
                    try {
                        o = response.getJSONObject(i);
                        settings_goal = o.getString("Step Goal");
                        sensitivity_goal = o.getString("Sensitivity");
                        goal.setText(settings_goal);
                        sensitivity.setText(sensitivity_goal);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(preferenceActivity.this,"Database failure",Toast.LENGTH_SHORT).show();
            }
        });
        String requestURL = goal_url + settings_goal + "/" + sensitivity_goal + "/" + ID;
        StringRequest submit = new StringRequest(Request.Method.GET, requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                queue.add(request);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(preferenceActivity.this,"Error connecting to database", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(submit);

        goal = (TextView) findViewById(R.id.string_goal);
        sensitivity = (TextView) findViewById(R.id.string_sensitivity);

        listView = (ListView) findViewById(R.id.dynamic_preferences);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_topics);
        listView.setAdapter(adapter);
        listView.setDividerHeight(4);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                if (adapter.getAdapter().getItem(position).toString().equals("Step Goal")) {
                    ArrayList<String> name = new ArrayList<String>();
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(preferenceActivity.this);
                    mBuilder.setTitle("Step Goal");
                    step_goal_options = new String[]{"1000", "2000", "3000", "4000", "5000", "6000", "7000", "8000", "9000", "10000", "11000", "12000", "13000", "14000", "15000", "16000", "17000", "18000", "19000", "20000", "21000", "22000", "23000", "24000", "25000", "26000", "27000", "28000", "29000", "30000"};
                    for (int i = 0; i<step_goal_options.length; i++) {
                        name.add(step_goal_options[i]);
                    }
                    int checker = name.indexOf(settings_goal);

                    mBuilder.setSingleChoiceItems(step_goal_options, checker, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dialog.dismiss();
                        }
                    });
                    mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    mBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ListView view = ((AlertDialog) dialog).getListView();
                            if (view.getCheckedItemCount() > 0) {
                                Object checked = view.getAdapter().getItem(view.getCheckedItemPosition());
                                settings_goal = String.valueOf(checked);
                                goal.setText(settings_goal);
                                updateGoal(settings_goal);
                            }
                            updateGoal(settings_goal);
                        }
                    });
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }

                if (adapter.getAdapter().getItem(position).toString().equals("Sensitivity")) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(preferenceActivity.this);
                    mBuilder.setTitle("Sensitivity");
                    //mBuilder.setMessage("We need this to calculate stride length");
                    sensitivity_options = new String[]{"Low","Medium","High"};
                    switch (sensitivity_goal) {
                        case "Low":
                            checked = 0;
                            break;
                        case "Medium":
                            checked = 1;
                            break;
                        case "High":
                            checked = 2;
                            break;
                    }
                    mBuilder.setSingleChoiceItems(sensitivity_options, checked, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dialog.dismiss();
                        }
                    });
                    mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    mBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ListView view = ((AlertDialog) dialog).getListView();
                            if(view.getCheckedItemCount() > 0) {
                                Object checked = view.getAdapter().getItem(view.getCheckedItemPosition());
                                String sensitivity_choice = String.valueOf(checked);
                                sensitivity.setText(sensitivity_choice);
                                sensitivity_goal = sensitivity_choice;
                                updateSensitivity(sensitivity_goal);
                            }
                            updateSensitivity(sensitivity_goal);
                        }
                    });
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }
            }
        });
    }

    public void updateGoal(String addition) {
        queue = Volley.newRequestQueue(preferenceActivity.this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preferenceActivity.this);
        String ID = prefs.getString("ID", null);

        String URL = update_goal + addition + "/" + ID;
        StringRequest submit = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(preferenceActivity.this,"Error connecting to database",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(submit);
    }

    public void updateSensitivity(String addition) {
        queue = Volley.newRequestQueue(preferenceActivity.this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preferenceActivity.this);
        String ID = prefs.getString("ID", null);
        String URL = update_sensitivity + addition + "/" + ID;
        StringRequest submit = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(preferenceActivity.this,"Error connecting to database",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(submit);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);
    }
}
