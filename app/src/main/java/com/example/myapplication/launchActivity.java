package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.NumberPicker;
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

import java.math.BigDecimal;

public class launchActivity extends AppCompatActivity {

    String prevStarted = "prevStarted";
    String[] list = {"Height", "Weight"};
    ListView casing;
    ArrayAdapter<String> adapter;
    Button launch_button;
    CheckBox male;
    CheckBox female;
    float launch_height = 0f;
    float weight_chosen = 0f;
    float stride_length = 0f;
    TextView divider;

    String gender_selected = "Male";
    String height = "1.82";
    String weight = "110.0";
    String unit_decided = "Metric";


    String addLaunch = "https://studev.groept.be/api/a19sd704/addLaunch/";
    static final String getId = "https://studev.groept.be/api/a19sd704/getLastUserID";
    RequestQueue information_launch_db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        launchActivity.this.setTitle("FIRST THINGS FIRST");


        divider = (TextView) findViewById(R.id.decimal_text);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE);
        if(!sharedPreferences.getBoolean(prevStarted,false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(prevStarted,Boolean.TRUE);
            editor.apply();
        } else{
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("Height",launch_height);
            intent.putExtra("Weight",weight_chosen);
            intent.putExtra("Gender",gender_selected);
            startActivity(intent);
        }
        male = (CheckBox) findViewById(R.id.male_launch);
        female = (CheckBox) findViewById(R.id.female_launch);
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(male.isChecked()) {
                    System.out.println("Male");
                    //gender_selected = "Male";
                    //addLaunch = addLaunch + gender_selected;
                }
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(female.isChecked()) {
                    System.out.println("Female");
                    gender_selected = "Female";
                    //addLaunch = addLaunch + gender_selected;
                }
            }
        });

        casing = (ListView) findViewById(R.id.launch_activities);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        casing.setAdapter(adapter);
        casing.setDividerHeight(16);
        casing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

                if(adapter.getAdapter().getItem(position).toString().equals("Height")) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(launchActivity.this);
                    mBuilder.setTitle("Height");
                    mBuilder.setMessage("Please select your height");
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.activity_dialog, null);
                    mBuilder.setView(dialogView);
                    final NumberPicker picker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
                    picker.setMaxValue(6);
                    picker.setMinValue(0);
                    picker.setWrapSelectorWheel(true);
                    picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Log.d("Tag","onValueChange: ");
                            int picker_value = picker.getValue();
                        }
                    });
                    final NumberPicker decimal = (NumberPicker) dialogView.findViewById(R.id.point_number_dialog);
                    decimal.setMaxValue(99);
                    decimal.setMinValue(0);
                    decimal.setWrapSelectorWheel(true);
                    decimal.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Log.d("Tag","onValueChange: ");
                        }
                    });
                    final NumberPicker unit = (NumberPicker) dialogView.findViewById(R.id.unit_string_dialog);
                    String[] unit_values = {"m", "ft'in"};
                    unit.setMinValue(0);
                    unit.setMaxValue(1);
                    unit_decided = "Metric";
                    unit.setWrapSelectorWheel(false);
                    unit.setDisplayedValues(unit_values);
                    unit.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker25, int oldVal, int newVal) {
                            if (unit.getValue() == 1) {
                                unit_decided = "Imperial";
                                double feet_picker =  (picker.getValue() * 3.3);
                                double inches_decimal = (double) (decimal.getValue()) / 100;
                                double pick = inches_decimal * 3.3;
                                double feet_number = feet_picker + pick;
                                BigDecimal number = new BigDecimal(String.valueOf(feet_number));
                                int feet = number.intValue();
                                double actual_feet = feet_number - feet;
                                double inches = (Math.round(actual_feet*100d)/100d);
                                int final_inches = (int) inches;
                                picker.setValue(feet);
                                decimal.setValue(final_inches);
                                decimal.setMaxValue(99);
                                decimal.setMinValue(0);
                                picker.setMaxValue(10);
                            } else {
                                decimal.setMaxValue(99);
                                decimal.setMinValue(0);
                                picker.setMaxValue(10);
                                double metre_picker = (picker.getValue() / 3.3);
                                int updated_decimal = (decimal.getValue());
                                double pick = (((double)updated_decimal)/100) / 3.3;
                                double metre_number = metre_picker + pick;
                                System.out.println(metre_number);
                                BigDecimal number = new BigDecimal(String.valueOf(metre_number));
                                System.out.println(number);
                                int metres = number.intValue();
                                System.out.println(metres);
                                double centimetres = (metre_number - metres);
                                centimetres = (Math.round(centimetres*100d)/100d) * 100;
                                System.out.println(centimetres);
                                int final_metres = (int) centimetres;
                                picker.setValue(metres);
                                decimal.setValue(final_metres);
                            }
                        }
                    });

                    mBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("TAG","onClick: "+String.valueOf(picker.getValue())+ "."+String.valueOf(decimal.getValue()));

                            if (decimal.getValue() <= 9) {
                                String s = "0"+ decimal.getValue();
                                height = picker.getValue() + "." + s;
                                //addLaunch = addLaunch + gender_selected + "/" + height;
                            } else {
                                height = picker.getValue() + "." + decimal.getValue();
                            }
                            launch_height = Float.parseFloat(height);
                            stride_length = 0.415f * launch_height;

                        }
                    });
                    mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();


                }

                if(adapter.getAdapter().getItem(position).toString().equals("Weight")) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(launchActivity.this);
                    mBuilder.setTitle("Weight");
                    mBuilder.setMessage("Please enter your weight");
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.activity_dialog, null);
                    mBuilder.setView(dialogView);
                    final NumberPicker picker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
                    picker.setMaxValue(600);
                    picker.setMinValue(40);
                    picker.setWrapSelectorWheel(true);
                    picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Log.d("Tag","onValueChange: ");
                        }
                    });
                    final NumberPicker decimal = (NumberPicker) dialogView.findViewById(R.id.point_number_dialog);
                    decimal.setMaxValue(9);
                    decimal.setMinValue(0);
                    decimal.setWrapSelectorWheel(true);
                    decimal.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Log.d("Tag","onValueChange: ");
                        }
                    });
                    final NumberPicker unit = (NumberPicker) dialogView.findViewById(R.id.unit_string_dialog);
                    String[] unit_values = {"kg", "lbs"};
                    unit.setMinValue(0);
                    unit.setMaxValue(1);
                    unit.setWrapSelectorWheel(false);
                    unit.setDisplayedValues(unit_values);
                    unit.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker256, int oldVal, int newVal) {
                            if (unit.getValue() == 0) {
                                double kg_initial = (double) picker.getValue() * 0.4535;
                                double initial = (double) (decimal.getValue()/10) * 0.4535;

                                double full_kg = kg_initial + initial;
                                BigDecimal number = new BigDecimal(String.valueOf(full_kg));
                                int picker_kg = number.intValue();
                                double decimal_kg = full_kg - picker_kg;
                                decimal_kg = (Math.round(decimal_kg*10d)/10d) *10;
                                int final_decimal_kg = (int) decimal_kg;

                                picker.setValue(picker_kg);
                                decimal.setValue(final_decimal_kg);

                            } else if (unit.getValue() == 1) {
                                double pound_initial = picker.getValue() * 2.205;
                                System.out.println(pound_initial);
                                double initial = (double) (decimal.getValue())/10;
                                System.out.println(initial);
                                double final_initial = initial * 2.205;

                                double full_pounds = pound_initial + final_initial;
                                System.out.println(full_pounds);
                                BigDecimal number = new BigDecimal(String.valueOf(full_pounds));
                                int picker_pound = number.intValue();
                                double decimal_pound = full_pounds - picker_pound;
                                decimal_pound =( Math.round(decimal_pound * 10d) / 10d) * 10;
                                System.out.println(decimal_pound);
                                int final_decimal = (int) decimal_pound;
                                System.out.println(final_decimal);

                                picker.setValue(picker_pound);
                                decimal.setValue(final_decimal);
                            }
                        }
                    });


                    mBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("FINAL","onClick: "+String.valueOf(picker.getValue())+ "."+String.valueOf(decimal.getValue()));

                            if (decimal.getValue() <= 9) {
                                String s = "0"+ decimal.getValue();
                                weight = picker.getValue() + "." + s;
                                //addLaunch = addLaunch + gender_selected + "/" + height + "/" + weight;
                            }

                            weight = picker.getValue() + "." + decimal.getValue();
                            weight_chosen = Float.parseFloat(weight);

                        }
                    });
                    mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();

                }
            }
        });

        launch_button = (Button) findViewById(R.id.proceed_button);
        launch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(launchActivity.this,MainActivity.class);
//                startActivity(intent);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(launchActivity.this);
                mBuilder.setTitle("Final thing");
                mBuilder.setMessage("We need you to enable 'Physical activity', 'disable battery saver restrictions' and allow 'Auto-start' for the app to run properly");
                mBuilder.setNeutralButton("Go to Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",getPackageName(),null);
                        intent.setData(uri);

                        startActivity(intent);
                    }
                });
                mBuilder.setPositiveButton("Proceed to App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        String submitLaunch = addLaunch + gender_selected + "/" + height + "/" + weight + "/" + unit_decided;
                        information_launch_db = Volley.newRequestQueue(launchActivity.this);


                        final JsonArrayRequest idRequest = new JsonArrayRequest(Request.Method.GET, getId, null, new Response.Listener<JSONArray>() {
                            String id = "";
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i<response.length(); i++) {
                                    JSONObject o = null;
                                    try {
                                        o = response.getJSONObject(i);
                                        id += o.get("MAX(UserID)");
                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(launchActivity.this);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("ID", id);
                                        editor.apply();
                                        System.out.println(id);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(launchActivity.this, "Information not retrievable", Toast.LENGTH_LONG).show();
                            }
                        });

                        StringRequest submitRequest = new StringRequest(Request.Method.GET, submitLaunch, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                information_launch_db.add(idRequest);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(launchActivity.this,"Error adding to database. Please try in the user settings", Toast.LENGTH_LONG).show();
                            }
                        });

                        information_launch_db.add(submitRequest);

                        Intent intent = new Intent(launchActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                });
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

    }

}
