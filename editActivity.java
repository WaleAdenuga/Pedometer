package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Objects;

public class editActivity extends AppCompatActivity {

    String sendFeedback = "https://studev.groept.be/api/a19sd704/sendFeedback/";
    RequestQueue queue;
    EditText edit;
    RatingBar bar;
    Button save_button;
    String barValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editActivity.this.setTitle("FEEDBACK");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        save_button = (Button) findViewById(R.id.feedback_button);
        edit = (EditText) findViewById(R.id.feedback_edit);
        edit.setHint("Type here please");
        bar = (RatingBar) findViewById(R.id.feedback_rating);
        bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                System.out.println(ratingBar.getRating());
                barValue = String.valueOf(ratingBar.getRating());
                System.out.println(barValue);
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queue = Volley.newRequestQueue(editActivity.this);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(editActivity.this);
                String ID = prefs.getString("ID", null);
                String feedback = edit.getText().toString();
                String send_url = sendFeedback + feedback + "/" + barValue + "/" +  ID;

                StringRequest request = new StringRequest(Request.Method.GET, send_url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(editActivity.this,"Feedback sent and received", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(editActivity.this,"Couldn't access the database", Toast.LENGTH_LONG).show();
                    }
                });
                queue.add(request);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.finish();
        return super.onOptionsItemSelected(item);
    }
}
