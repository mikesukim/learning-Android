package edu.csce4623.ahnelson.todomvp3.todolistactivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import edu.csce4623.ahnelson.todomvp3.R;
import edu.csce4623.ahnelson.todomvp3.data.ToDoItem;

public class AlarmNotification extends BroadcastReceiver {
    String ALARM_CHANNEL_ID = "alarm_channel";
    ToDoItem item;

    @Override
    public void onReceive(Context context, Intent intent) {

//        // get ToDoItem
        String todoTitle = intent.getStringExtra("ToDoTitle");
        String todoContent = intent.getStringExtra("ToDoContent");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder alarm_builder = new NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(todoTitle)
                .setContentText(todoContent)
                .setChannelId(ALARM_CHANNEL_ID);

        Intent resultIntent = new Intent(context, ToDoListActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ToDoListActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        alarm_builder.setContentIntent(resultPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelID = ALARM_CHANNEL_ID;
            NotificationChannel channel = new NotificationChannel(
                    channelID,
                    "AlarmNotifications",
                    notificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            alarm_builder.setChannelId(channelID);
        }

        notificationManager.notify(0,alarm_builder.build());
    }

}

