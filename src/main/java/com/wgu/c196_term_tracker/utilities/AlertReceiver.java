package com.wgu.c196_term_tracker.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.BroadcastReceiver;
import android.util.Log;

import com.wgu.c196_term_tracker.R;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        int notificationId = intent.getIntExtra("notificationId", 0);

        Log.d("AlertReceiver", "Received alarm: " + title + " - " + message);

        NotificationUtils.showNotification(context, title, message, notificationId);
    }
}