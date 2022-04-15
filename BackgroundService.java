package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.ui.today.TodayFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BackgroundService extends Service {


    TodayFragment today;
    String notif_data = "https://studev.groept.be/api/a19sd704/getSameDayData/";
    String goal_data = "https://studev.groept.be/api/a19sd704/getPreference/";
    String steps = "0";
    String calories = "0.0";
    RequestQueue queue;
    Context context;
    String actual_goal;
    int progress;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        createNotification();
        return Service.START_STICKY;
    }

    public void showData() {

        queue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String ID = prefs.getString("ID", null);
        System.out.println(ID);
        Date current = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String system_date = sdf.format(current);
        System.out.println(system_date);
        String get_url = notif_data + ID +"/" + system_date;
        final JsonArrayRequest animate = new JsonArrayRequest(Request.Method.GET, get_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i<response.length(); i++) {
                    JSONObject o = null;
                    try {
                        o = response.getJSONObject(i);
                        steps = o.getString("Steps");
                        calories = o.getString("Calories");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Couldn't connect to database",Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(animate);

        String goal_url = goal_data + ID;
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, goal_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i<response.length(); i++) {
                    JSONObject o = null;
                    try {
                        o = response.getJSONObject(i);
                        actual_goal = o.getString("Step Goal");
                        progress = Math.round((Integer.parseInt(steps) / Integer.parseInt(actual_goal)));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Couldn't retrieve information", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonArrayRequest);

    }

    public void getGoal() {

    }

    public void createNotification() {
        showData();
        RemoteViews notificationLayout = new RemoteViews(this.getPackageName(),R.layout.activity_notification);
        notificationLayout.setTextViewText(R.id.notification_steps,steps);
        notificationLayout.setTextViewText(R.id.notification_calories,calories);
        notificationLayout.setImageViewResource(R.id.imageView2,R.drawable.ic_feet);
        notificationLayout.setImageViewResource(R.id.imageView3,R.drawable.ic_fire);
        notificationLayout.setImageViewResource(R.id.imageView4,R.drawable.ic_navigate_next_black_24dp);
        //notificationLayout.setProgressBar(R.id.progressBar,100,progress,false);
        notificationLayout.setImageViewResource(R.id.imageView5,R.attr.dividerVertical);

        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent notificationIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int notifyID = 7;
            String notif_ID = "Data_Channel";
            CharSequence name = "Low_Importance_Just_Updating_User";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(notif_ID, name, importance);

            Notification notification = new NotificationCompat.Builder(this,notif_ID)
                    //.setChannelId(notif_ID)
                    .setSmallIcon(R.drawable.ic_feet)
                    //.setContentTitle("shit")
                    //.setContentText("shit again")
                    //.setCustomBigContentView(notificationLayout)
                    //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    //.setContent(notificationLayout)
                    //.setCustomBigContentView(notificationLayout)
                    .setCustomContentView(notificationLayout)
                    .setOngoing(true)
                    .setContentIntent(notificationIntent)
                    .setOnlyAlertOnce(true)
                    .setWhen(System.currentTimeMillis())
                    .build();

            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
            //mNotificationManager.notify(12,notification);
            startForeground(15,notification);
        }
    }
}
