package org.jbtc.yondapdf.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.jbtc.yondapdf.MainActivity;
import org.jbtc.yondapdf.R;
import org.jbtc.yondapdf.Utils;
import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;

public class ServiceTTS extends Service implements TextToSpeech.OnInitListener {

    private static final String CHANNEL_ID = "canaltts";
    private static final String TAG = "nHomef";
    private static final String dbName = "bookslightnovel";
    private TextToSpeech textToSpeech;
    private RoomDatabaseBooksLN rdb;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*@Override
    public void onReceive(Context context, Intent intent) {

    }*/


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

        tomarAccion(intent.getAction());
        startForeground(1, notificacion());

        return START_NOT_STICKY;
    }

    private void tomarAccion(String accion) {
        switch (accion){
            case Utils.ACTION_NEX:{
                break;
            }
            case Utils.ACTION_PAUSE:{
                break;
            }
            default:{
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onInit(int i) {

    }

    public Notification notificacion(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

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
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText("Texto")
                .setSmallIcon(R.drawable.ic_botspeak___master)
                .setContentIntent(pendingIntent)
                .setCustomContentView(notificationLayout)
                .build();
        //notificationLayout.setOnClickPendingIntent();

        //}
        // Notification ID cannot be 0.
        return notification;
    }
}
