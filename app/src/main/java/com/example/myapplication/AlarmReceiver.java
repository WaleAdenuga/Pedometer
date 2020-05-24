package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Alarm received",Toast.LENGTH_LONG).show();

        Intent intent2 = new Intent(context, MainActivity.class);
        PendingIntent notificationIntent = PendingIntent.getActivity(context,0,intent2,0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int notifyID = 1;
            String notification_ID = "reminderChannel";
            CharSequence name = "reminderActivity";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(notification_ID, name, importance);

            Notification notification = new NotificationCompat.Builder(context, notification_ID)
                    .setSmallIcon(R.drawable.ic_feet)
                    .setContentTitle("Check your Progress")
                    .setContentText("Are you hiking or are you one very fit human? Come see what you've accomplished")
                    .setContentIntent(notificationIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(12,notification);
        }
    }
}
