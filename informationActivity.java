package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.ui.profile.ProfileFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;


public class informationActivity extends AppCompatActivity {
    ListView casing;
    ArrayAdapter<String> adapter;
    TextView point;
    String[] list_topics = {"Gender", "Height", "Weight", "Metric & Imperial Unit", /*"First day of week"*/};
    String[] gender_dialog;
    String[] unit_dialog;
    //String[] week_days;
    NumberPicker numberPicker;
    NumberPicker decimalPicker;
    int updated_picker = 0;
    int updated_decimal = 0;
    float updated_number = 0.0f;

    TextView divider;
    TextView gender_choice;
    TextView unit_choice;
    TextView height_choice;
    TextView weight_choice;

    int checked;
    String initial_gender;
    String initial_height;
    String initial_weight;
    String unit_chosen = "Metric";

    String launch_url = "https://studev.groept.be/api/a19sd704/getLaunchData/";
    String update_gender = "https://studev.groept.be/api/a19sd704/updateGender/";
    String update_height = "https://studev.groept.be/api/a19sd704/updateHeight/";
    String update_weight = "https://studev.groept.be/api/a19sd704/updateWeight/";
    String update_unit = "https://studev.groept.be/api/a19sd704/updateUnit/";
    String deviceID;
    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        informationActivity.this.setTitle("PERSONAL INFORMATION");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        queue = Volley.newRequestQueue(informationActivity.this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(informationActivity.this);
        final String ID = prefs.getString("ID",null);
        String requestURL = launch_url + ID;
        StringRequest submitRequest = new StringRequest(Request.Method.GET, requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //System.out.println(response);

                try {

                    JSONArray userArray = new JSONArray(response);
                    String Gender_db = "";
                    String Height_db = "";
                    String Weight_db = "";
                    String Unit_db = "";

                    for (int i = 0; i<userArray.length(); i++) {
                        JSONObject o = userArray.getJSONObject(i);
                        Gender_db += o.getString("Gender");
                        Height_db += o.getString("Height");
                        Weight_db += o.getString("Weight");
                        Unit_db += o.getString("Unit");
                    }

                    String h = Height_db;
                    String w = Weight_db;

                    if (Unit_db.equals("Metric")) {
                        Height_db = Height_db + " m";
                        Weight_db = Weight_db + " kg";
                    } else if (Unit_db.equals("Imperial")) {
                        Height_db = Height_db + " ft.in";
                        Weight_db = Weight_db + " lbs";
                    }

                    gender_choice.setText(Gender_db);
                    initial_gender = Gender_db;
                    height_choice.setText(Height_db);
                    initial_height = h;
                    weight_choice.setText(Weight_db);
                    initial_weight = w;
                    unit_choice.setText(Unit_db);
                    unit_chosen = Unit_db;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(informationActivity.this,"Couldn't retrieve information from database",Toast.LENGTH_LONG).show();
            }
        });
        queue.add(submitRequest);

        divider = (TextView) findViewById(R.id.decimal_text);
        gender_choice = (TextView) findViewById(R.id.final_gender_summary);
        unit_choice = (TextView) findViewById(R.id.final_unit_summary);
        unit_choice.setText(unit_chosen);
        height_choice = (TextView) findViewById(R.id.final_height_summary);
        weight_choice = (TextView) findViewById(R.id.final_weight_summary);

        numberPicker = (NumberPicker) findViewById(R.id.dialog_number_picker);
        decimalPicker = (NumberPicker) findViewById(R.id.point_number_dialog);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        casing = (ListView) findViewById(R.id.dynamic_list);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_topics);
        casing.setAdapter(adapter);
        casing.setDividerHeight(4);
        casing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                if (adapter.getAdapter().getItem(position).toString().equals("Gender")) {
                    if (initial_gender.equals("Male")) {
                        checked = 0;
                    } else if (initial_gender.equals("Female")) {
                        checked = 1;
                    }

                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(informationActivity.this);
                    mBuilder.setTitle("Gender");
                    //mBuilder.setMessage("We need this to calculate stride length");
                    gender_dialog = new String[]{"Male", "Female"};
                    mBuilder.setSingleChoiceItems(gender_dialog, checked, new DialogInterface.OnClickListener() {
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
                                String gender = String.valueOf(checked);
                                gender_choice.setText(gender);
                                initial_gender = gender;

                                updateGender(initial_gender);
                            }
                        }
                    });
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }

/*                if (adapter.getAdapter().getItem(position).toString().equals("First day of week")) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(informationActivity.this);
                    mBuilder.setTitle("First day of week");
                    //mBuilder.setMessage("We need this also for proper calculations");
                    week_days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
                    mBuilder.setSingleChoiceItems(week_days, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
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
                }*/



                if(adapter.getAdapter().getItem(position).toString().equals("Weight")) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(informationActivity.this);
                    mBuilder.setTitle("Weight");
                    mBuilder.setMessage("Please follow your choice for Imperial Unit");
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.activity_dialog,null);
                    mBuilder.setView(dialogView);
                    final NumberPicker picker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
                    picker.setMaxValue(600);
                    picker.setMinValue(40);
                    picker.setWrapSelectorWheel(false);
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
                                unit_chosen = "Metric";
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
                                unit_chosen = "Imperial";
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
                            Log.d("TAG","onClick: "); //decimal.getValue());
                            initial_weight = "";
                            if(unit.getValue() == 0) {
                                initial_weight = picker.getValue() + "." + decimal.getValue() + " ";
                                weight_choice.setText(initial_weight);
                            } else if (unit.getValue() == 1) {
                                initial_weight = picker.getValue() + "." + decimal.getValue() + " ";
                                weight_choice.setText(initial_weight);
                            }
                            updateWeight(initial_weight);
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

                if(adapter.getAdapter().getItem(position).toString().equals("Height")) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(informationActivity.this);
                    mBuilder.setTitle("Height");
                    mBuilder.setMessage("Please follow your choice for Standard Unit");
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.activity_dialog, null);
                    mBuilder.setView(dialogView);
                    final NumberPicker picker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
                    picker.setMaxValue(400);
                    picker.setMinValue(0);
                    picker.setWrapSelectorWheel(false);
                    picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Log.d("Tag","onValueChange: ");
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
                    if (unit_chosen.equals("Imperial")) {
                        decimal.setMaxValue(12);
                        decimal.setMinValue(0);
                    }
                    final NumberPicker unit = (NumberPicker) dialogView.findViewById(R.id.unit_string_dialog);
                    String[] unit_values = {"m", "ft'in"};
                    unit.setMinValue(0);
                    unit.setMaxValue(1);
                    unit.setWrapSelectorWheel(false);
                    unit.setDisplayedValues(unit_values);
                    unit.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker25, int oldVal, int newVal) {
                            if (unit.getValue() == 1) {
                                double feet_picker =  (picker.getValue() * 3.3);
                                double inches_decimal = (double) (decimal.getValue()) / 100;
                                double pick = inches_decimal * 3.3;
                                double feet_number = feet_picker + pick;
                                BigDecimal number = new BigDecimal(String.valueOf(feet_number));
                                int feet = number.intValue();
                                double actual_feet = feet_number - feet;
                                double inches = (Math.round(actual_feet*100d)/100d) * 12;
                                int final_inches = (int) inches;
                                picker.setValue(feet);
                                decimal.setValue(final_inches);
                                decimal.setMaxValue(12);
                                decimal.setMinValue(0);
                                picker.setMaxValue(10);
                                unit_chosen = "Imperial";
                            } else {
                                unit_chosen = "Metric";
                                decimal.setMaxValue(99);
                                decimal.setMinValue(0);
                                picker.setMaxValue(10);
                                double metre_picker = (picker.getValue() / 3.3);
                                updated_decimal = (decimal.getValue());
                                double pick = updated_decimal * 0.025;
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
                            Log.d("TAG","onClick: ");
                            initial_height = "";

                            if(unit.getValue() == 0) {
                                if (decimal.getValue() < 10) {
                                    initial_height = picker.getValue() + ".0" + decimal.getValue() + "  ";
                                    height_choice.setText(initial_height);
                                    updateHeight(initial_height);
                                } else {
                                    initial_height = picker.getValue() + "." + decimal.getValue() + "  ";
                                    height_choice.setText(initial_height);
                                    updateHeight(initial_height);
                                }
                            }
                            if (unit.getValue() == 1) {
                                double number = (Math.round((decimal.getValue() * 0.08) * 100d)/100d) + picker.getValue();
                                initial_height = String.valueOf(number);
                                height_choice.setText(initial_height);
                                updateHeight(initial_height);
                            }
                            /*height_choice.setText(initial_height);
                            updateHeight(initial_height);*/

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

                if (adapter.getAdapter().getItem(position).toString().equals("Metric & Imperial Unit")) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(informationActivity.this);
                    mBuilder.setTitle("Metric & Imperial Unit");
                    //mBuilder.setMessage("We need this just for cool service");
                    unit_dialog = new String[]{"Metric", "Imperial"};
                    if (unit_chosen.equals("Metric")) {
                        checked = 0;
                    } else if (unit_chosen.equals("Imperial")) {
                        checked = 1;
                    }
                    mBuilder.setSingleChoiceItems(unit_dialog, checked, new DialogInterface.OnClickListener() {
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
                                String unit = String.valueOf(checked);
                                unit_choice.setText(unit);

                                if (unit_chosen.equals("Metric") && unit.equals("Imperial")) {
                                    unit_chosen = "Imperial";
                                    updateUnit(unit_chosen);
                                    String vacate = String.valueOf(convertKG(Double.parseDouble(initial_weight)));
                                    weight_choice.setText(vacate);
                                    updateWeight(vacate);
                                    initial_weight = vacate;
                                    String vacation = String.valueOf(convertMetres(Double.parseDouble(initial_height)));
                                    height_choice.setText(vacation);
                                    updateHeight(vacation);
                                    initial_height = vacation;
                                }

                                if (unit_chosen.equals("Imperial") && unit.equals("Metric")) {
                                    unit_chosen = "Metric";
                                    updateUnit(unit_chosen);
                                    String vacate = String.valueOf(convertLBS(Double.parseDouble(initial_weight)));
                                    initial_weight = vacate;
                                    weight_choice.setText(vacate);
                                    updateWeight(vacate);
                                    String vacation = String.valueOf(convertFT(Double.parseDouble(initial_height)));
                                    initial_height = vacation;
                                    height_choice.setText(vacation);
                                    updateHeight(vacation);
                                }
//                                updateUnit(unit_chosen);
                            }
                        }
                    });
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }


            }
        });
    }

    public double convertKG(double convert) {
        convert = convert * 2.205;
        convert = Math.round(convert * 10d)/10d;
        return convert;
    }

    public double convertLBS(double convert) {
        convert = convert * 0.4535;
        convert = Math.round(convert * 10d)/10d;
        return convert;
    }

    public void updateWeight(String addition) {
        queue = Volley.newRequestQueue(informationActivity.this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(informationActivity.this);
        final String ID = prefs.getString("ID",null);
        String weight_url = update_weight + addition + "/" + ID;
        StringRequest submit = new StringRequest(Request.Method.GET, weight_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(informationActivity.this,"Updated",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(informationActivity.this,"Failed to update database",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(submit);
    }

    public void updateHeight(String addition) {
        queue = Volley.newRequestQueue(informationActivity.this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(informationActivity.this);
        final String ID = prefs.getString("ID",null);
        String height_url = update_height + addition + "/" + ID;
        StringRequest submit = new StringRequest(Request.Method.GET, height_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(informationActivity.this,"Updated",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(informationActivity.this,"Failed to update database",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(submit);
    }

    public double convertFT(double convert) {
        convert = Math.round((convert / 3.28)*100d) / 100d;
        return convert;
    }

    public double convertMetres(double convert) {
        convert = Math.round((convert * 3.28)*100d) / 100d;
        return convert;
    }

    public void updateGender(String addition) {
        queue = Volley.newRequestQueue(informationActivity.this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(informationActivity.this);
        final String ID = prefs.getString("ID",null);
        String gend_url = update_gender + addition + "/" + ID;
        StringRequest submit = new StringRequest(Request.Method.GET, gend_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(informationActivity.this,"Updated",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(informationActivity.this,"Failed to update database",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(submit);
    }

    public void updateUnit(String addition) {
        queue = Volley.newRequestQueue(informationActivity.this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(informationActivity.this);
        final String ID = prefs.getString("ID",null);
        String unit_url = update_unit + addition + "/" + ID;
        StringRequest submit = new StringRequest(Request.Method.GET, unit_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(informationActivity.this,"Updated",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(informationActivity.this,"Failed to update database",Toast.LENGTH_SHORT).show();
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
