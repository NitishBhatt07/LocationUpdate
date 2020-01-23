package com.nbbhatt.locationupdate;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.nbbhatt.locationupdate.App.CHANNEL_ID;

public class NotificationService extends Service {

  //  private NotificationManagerCompat notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String LatLong = intent.getStringExtra("LatLong");
        String address = intent.getStringExtra("Address");
        String text = intent.getStringExtra("Number");
        String SpeakText = intent.getStringExtra("SpeakText");

        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,notificationIntent,0);

        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.notification_collapsed);
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_expanded);

        collapsedView.setTextViewText(R.id.text_view_collapsed_1,"Location Address!");
        expandedView.setTextViewText(R.id.text_view_expanded_1,LatLong);
        expandedView.setTextViewText(R.id.text_view_expanded_2,address);
        expandedView.setTextViewText(R.id.text_view_expanded_3,text);
        expandedView.setTextViewText(R.id.text_view_expanded_4,SpeakText);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(pendingIntent)
                //  .setContentTitle("tittle")
                // .setContentText("this is notification")
                .build();

        startForeground(1,notification);
     //   notificationManager.notify(1,notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
