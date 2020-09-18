package org.jbtc.yondapdf.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import org.jbtc.yondapdf.MainActivity;
import org.jbtc.yondapdf.R;
import org.jbtc.yondapdf.SecondFragment;
import org.jbtc.yondapdf.Utils;
import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.entidad.Book;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import io.reactivex.rxjava3.core.Observable;

public class ServiceTTS extends Service implements TextToSpeech.OnInitListener {

    private static final String CHANNEL_ID = "canaltts";
    private static final String TAG = "sTTS";
    private static final String dbName = "bookslightnovel";
    private TextToSpeech textToSpeech;
    private RoomDatabaseBooksLN rdb;
    private Book book;
    private Uri uri;
    private enum StadoSpeak {playin,end,stoped}
    private StadoSpeak stadoSpeak = StadoSpeak.end;
    private int stateSpeak = Utils.STATE_NOT_INIT;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: eco");
        rdb = Room.databaseBuilder(getApplicationContext(),
                RoomDatabaseBooksLN.class, dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .build();

        textToSpeech  =new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener(){
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                Log.i(TAG +"ini ","Finalizo cargar pdf");
            }

            @Override
            public void onInit(int i) {
                Log.i(TAG +"ini ","i = "+i);
                textToSpeech.setLanguage(Locale.getDefault());
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.i(TAG, "onInit: "+textToSpeech.getVoices().toString());
                }*/
            }
        } );

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                Log.i(TAG +" ini ","Start");
                stadoSpeak= StadoSpeak.playin;
                /*todo:actualizar UI
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getMainActivity().getActivityMainBinding().btAppbarBotpage.setText(String.valueOf(book.getPageTag()+1));

                    }
                });*/
            }

            @Override
            public void onDone(String s) {
                if (stadoSpeak!= StadoSpeak.stoped) {
                    next();
                }
            }

            @Override
            public void onError(String s) {
                Log.i(TAG +" ini ","error:"+s);
            }
        });

        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: eco");
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

        if(book==null) {
            book = rdb.bookDAO().getBookById(intent.getExtras().getInt("id"));
            uri = Uri.parse(book.getUri());
        }

        tomarAccion(intent.getAction());
        //startForeground(1, notificacion());

        return START_NOT_STICKY;
    }

    private void tomarAccion(String accion) {
        if(accion!=null&&!accion.equals(""))
        switch (accion){
            case Utils.ACTION_START:{
                Log.i(TAG, "Received start Intent ");
                //mStateService = Statics.STATE_SERVICE.PREPARE;
                startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion());
                //destroyPlayer();
                //initPlayer();
                break;
            }
            case Utils.ACTION_NEX:{
                startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion());
                next();
                break;
            }
            case Utils.ACTION_PLAY:{
                startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion());
                speakBook(book.getPageTag()+1);
                break;
            }
            case Utils.ACTION_PAUSE:{
                startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion());
                stopSpeak();
                break;
            }
            case Utils.ACTION_STOP:{
                startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion());
                stopSpeak();
                break;
            }
            default:{
                break;
            }
        }
    }

    @Override
    public void onInit(int i) {

    }

    private void speakBook(int numPage) {

        Log.i(TAG, "speakBook: num: " + numPage);
        try {
            if (textToSpeech.isSpeaking()) {
                stopSpeak();
            }
            InputStream i = getContentResolver().openInputStream(uri);
            String txt = stripText(i, numPage);
            if(txt.trim().equals(""))txt="pagina "+numPage;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(txt, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(numPage));
                stadoSpeak = StadoSpeak.playin;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prev(){
        book.decPageTag1();
        //todo:validar in<pages
        rdb.bookDAO().updateBook(book);
        speakBook(book.getPageTag()+1);
        Log.i(TAG +" ini ","done");
    }

    public void next(){
        book.incPageTag1();
        //todo:validar in<pages
        rdb.bookDAO().updateBook(book);
        speakBook(book.getPageTag()+1);
        Log.i(TAG +" ini ","done");
    }

    public void stopSpeak(){
        Log.i(TAG, "stopSpeak: aver si esto es de tu talla");
        stadoSpeak= StadoSpeak.stoped;
        textToSpeech.stop();
        //textToSpeech.shutdown();//stop();
    }

    public Notification notificacion(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else {
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent IPlayIntent = new Intent(this, ServiceTTS.class);
        IPlayIntent.setAction(Utils.ACTION_PLAY);
        PendingIntent IPendingPlayIntent = PendingIntent.getService(this, 0, IPlayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent IPauseIntent = new Intent(this, ServiceTTS.class);
        IPauseIntent.setAction(Utils.ACTION_PAUSE);
        PendingIntent IPendingPauseIntent = PendingIntent.getService(this, 0, IPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent IStopIntent = new Intent(this, ServiceTTS.class);
        IStopIntent.setAction(Utils.ACTION_STOP);
        PendingIntent IPendingStopIntent = PendingIntent.getService(this, 0, IStopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews_NotificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);

        remoteViews_NotificationLayout.setOnClickPendingIntent(R.id.bt_notif_play, IPendingPlayIntent);
        remoteViews_NotificationLayout.setOnClickPendingIntent(R.id.bt_notif_pause, IPendingPauseIntent);
        remoteViews_NotificationLayout.setOnClickPendingIntent(R.id.bt_notif_stop, IPendingStopIntent);

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
                .setCustomContentView(remoteViews_NotificationLayout)
                .build();
        //notificationLayout.setOnClickPendingIntent();

        //}
        // Notification ID cannot be 0.
        return notification;
    }


    public String stripText(InputStream pdfInputStream, int page) {
        String textParsed = "";
        PDDocument document = null;
        try {
            document = PDDocument.load(pdfInputStream);
            PDFRenderer pdfRender=new PDFRenderer(document);
            //Bitmap image = pdfRender.renderImage(0);

            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(page);
            pdfStripper.setEndPage(page);
            textParsed =  pdfStripper.getText(document);
            textParsed=textParsed.replace("\n","");
            Log.i(TAG, "stripText: "+textParsed);
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (document != null) document.close();
            }
            catch (IOException e)
            {
                Log.e("PdfBox-Android-Sample", "Exception thrown while closing document", e);
            }
        }
        return textParsed;
    }

    public static int getState() {
        //return mStateService;
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
