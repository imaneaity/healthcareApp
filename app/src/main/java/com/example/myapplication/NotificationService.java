package com.example.myapplication;


import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationService extends Application {
    public static final String CHANNEL_ID_Service = "NotificationService";
    public static final String CHANNEL_ID_Alert = "AlertService";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel NotificatioService = new NotificationChannel(
                    CHANNEL_ID_Service,
                    "Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(NotificatioService);

            NotificationChannel AlertService = new NotificationChannel(
                    CHANNEL_ID_Alert,
                    "Alert Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(AlertService);

        }
    }
}