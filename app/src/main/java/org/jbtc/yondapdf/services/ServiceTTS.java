package org.jbtc.yondapdf.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.jbtc.yondapdf.MainActivity;
import org.jbtc.yondapdf.R;

public class ServiceTTS extends Service implements TextToSpeech.OnInitListener {
    private static final String CHANNEL_ID = "canaltts";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification;
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Titulo App")
                    .setContentText("Text")
                    //.setSmallIcon(R.drawable.icon)
                    .setContentIntent(pendingIntent)
                    .setTicker("Ticker")
                    .build();

        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {*/
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Example Service")
                    .setContentText("Texto")
                    .setSmallIcon(R.drawable.ic_botspeak___master)
                    .setContentIntent(pendingIntent)
                    .build();
        //}
        // Notification ID cannot be 0.
        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onInit(int i) {

    }
}
