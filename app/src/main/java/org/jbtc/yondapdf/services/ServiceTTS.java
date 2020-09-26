package org.jbtc.yondapdf.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import org.jbtc.yondapdf.MainActivity;
import org.jbtc.yondapdf.R;
import org.jbtc.yondapdf.Utils;
import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.entidad.Book;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ServiceTTS extends Service {

    private static final String CHANNEL_ID = "canaltts";
    private static final String TAG = "sTTS";
    private TextToSpeech textToSpeech;
    private RoomDatabaseBooksLN rdb;
    private Book book;
    private Uri uri;
    private byte stateSpeak = Utils.STATE_NOT_INIT;
    private Disposable disposable;
    private String mAccion=Utils.ACTION_CLOSE;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: eco");
        rdb = Room.databaseBuilder(getApplicationContext(),
                RoomDatabaseBooksLN.class, Utils.dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .build();
        textToSpeech  =new TextToSpeech(getApplicationContext(),getOnIntListener());
        textToSpeech.setOnUtteranceProgressListener(getUtteranceProgressListener());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: eco");
        if (intent == null) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }
        String accion=intent.getAction();
        if(book==null) {
            book = rdb.bookDAO().getBookById(intent.getExtras().getInt("id"));
            uri = Uri.parse(book.getUri());
        }
        tomarAccion(accion,intent.getExtras());
        return START_NOT_STICKY;
    }

    private void tomarAccion(String accion, Bundle bundle) {
        Log.i(TAG, "tomarAccion: Inicio");
        if(accion!=null&&!accion.equals(""))
        switch (accion){
            case Utils.ACTION_START:{
                Log.i(TAG, "ACTION_START Received start Intent ");
                //mStateService = Statics.STATE_SERVICE.PREPARE;
                startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion(accion));
                //destroyPlayer();
                //initPlayer();
                break;
            }
            case Utils.ACTION_PREV:{
                startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion(accion));
                prev();
                break;
            }
            case Utils.ACTION_PLAY:{
                //startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion(accion));//enviado a speakBook
                speakBook(book.getPageTagRead());
                break;
            }
            case Utils.ACTION_PLAYING:{
                //startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion(accion));//solo para atualizar el progres bar
                //speakBook(book.getPageTagRead());//#que hacer?
                Log.i(TAG, "tomarAccion: ACTION_PLAYING echo");
                break;
            }
            case Utils.ACTION_STOP:{
                startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion(accion));
                stopSpeak();
                break;
            }
            case Utils.ACTION_NEX:{
                startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion(accion));
                next();
                break;
            }
            case Utils.ACTION_CLOSE:{
                //startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion());
                stopSpeak();
                textToSpeech.shutdown();//todo:agregar esto a stopSpeak
                stopForeground(true);
                stopSelf();
                break;
            }
            default:{
                break;
            }
        }
    }

    //region controles
    private void speakBook(int numPage) {
        Log.i(TAG, "speakBook: num: " + numPage);
        Log.i(TAG, "speakBook: hilo: "+Thread.currentThread().getName());
        startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE, notificacion(Utils.ACTION_PLAY));
        try {
            if (textToSpeech.isSpeaking()) {
                stopSpeak();
            }
            InputStream i = getContentResolver().openInputStream(uri);
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                    try{
                        emitter.onNext(stripText(i, numPage));
                        emitter.onComplete();
                    }catch (Exception e){e.printStackTrace();}
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<String>() {
                String txt="";
                @Override public void onSubscribe(@NonNull Disposable d){disposable=d;}
                @Override public void onNext(String s) { txt=s; }
                @Override public void onError(@NonNull Throwable e) {}
                @Override public void onComplete() {
                    Log.i(TAG, "onComplete: hilo "+Thread.currentThread().getName());
                    speakTxtNumPage(txt,numPage);
                }
            });
        } catch (Exception e) {e.printStackTrace();}
    }

    private void speakTxtNumPage(String txt, int numPage){
        if(txt.trim().equals("")) txt ="pagina "+numPage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int r=textToSpeech.speak(txt, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(numPage));
            Log.i(TAG, "speakTxtNumPage: resultado speak "+r);
            //if( r==0 )stateSpeak = Utils.STATE_PLAYING; boorar xq ya lo ago en onStart
        }else {
            //todo:agregar esto para version previas a lolipop
            int r=textToSpeech.speak(txt, TextToSpeech.QUEUE_FLUSH, null);
            Log.i(TAG, "speakTxtNumPage: resultado speak deprecated "+r);
            //if ( r==0 )stateSpeak = Utils.STATE_PLAYING;
        }

        //Intent IPlayIntent = new Intent(getContext(), ServiceTTS.class);
        //IPlayIntent.setAction(Utils.ACTION_PLAY);
        //PendingIntent IPendingPlayIntent = PendingIntent.getService(getContext(), 0, IPlayIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //ContextCompat.startForegroundService(getContext(), IPlayIntent);
        startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE,notificacion(Utils.ACTION_PLAYING));
    }

    public void prev(){
         book.decPageTag1();
        //todo:validar in<pages
        rdb.bookDAO().updateBook(book);
        if(book.getPageTag()>=0) {
            speakBook(book.getPageTagRead());
            //startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE,notificacion(Utils.ACTION_PLAY));
        }
        Log.i(TAG," ini prev done");
    }

    public void next(){
        book.incPageTag1();
        rdb.bookDAO().updateBook(book);
        if(book.isUnfinished()) {
            Log.i(TAG, "xxx next done: if entro");
            speakBook(book.getPageTagRead());
            //startForeground(Utils.NOTIFICATION_ID_FOREGROUND_SERVICE,notificacion(Utils.ACTION_PLAYING));
        }
        Log.i(TAG,"xxx ini next done");
    }

    private void stopSpeak(){
        Log.i(TAG, "stopSpeak: aver si esto es de tu talla");
        //**stadoSpeak= StadoSpeak.stoped;
        stateSpeak = Utils.STATE_STOPED;
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }else{
            textToSpeech.shutdown();//stop();
        }
    }
    //endregion

    private Notification notificacion(String accion){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent IPrevIntent = new Intent(this, ServiceTTS.class);
        IPrevIntent.setAction(Utils.ACTION_PREV);
        PendingIntent IPendingPrevIntent = PendingIntent.getService(this, 0, IPrevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent IPlayIntent = new Intent(this, ServiceTTS.class);
        IPlayIntent.setAction(Utils.ACTION_PLAY);
        PendingIntent IPendingPlayIntent = PendingIntent.getService(this, 0, IPlayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent IPlayingIntent = new Intent(this, ServiceTTS.class);
        IPlayingIntent.setAction(Utils.ACTION_PLAYING);
        PendingIntent IPendingPlayingIntent = PendingIntent.getService(this, 0, IPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent IStopIntent = new Intent(this, ServiceTTS.class);
        IStopIntent.setAction(Utils.ACTION_STOP);
        PendingIntent IPendingStopIntent = PendingIntent.getService(this, 0, IStopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent INextIntent = new Intent(this, ServiceTTS.class);
        INextIntent.setAction(Utils.ACTION_NEX);
        PendingIntent IPendingNextIntent = PendingIntent.getService(this, 0, INextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent ICloseIntent = new Intent(this, ServiceTTS.class);
        ICloseIntent.setAction(Utils.ACTION_CLOSE);
        PendingIntent IPendingCloseIntent = PendingIntent.getService(this, 0, ICloseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews_NotificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);

        //remoteViews_NotificationLayout.setTextColor(R.id.tv_notif_page_text, Color.argb(0,255,0,0));
        remoteViews_NotificationLayout.setTextViewText(R.id.tv_notif_page_text, String.valueOf(book.getPageTagRead()));
        remoteViews_NotificationLayout.setTextViewText(R.id.tv_notif_titulo, book.getTitulo());
        remoteViews_NotificationLayout.setOnClickPendingIntent(R.id.bt_notif_prev, IPendingPrevIntent);
        if(accion.equals(Utils.ACTION_PLAY)) {
            Log.i(TAG, "notificacion: es play: visible");
            remoteViews_NotificationLayout.setViewVisibility(R.id.pb_notif_wait, View.VISIBLE);
        }else{
            Log.i(TAG, "notificacion: No es play: Gone");
            remoteViews_NotificationLayout.setViewVisibility(R.id.pb_notif_wait, View.GONE); }
        remoteViews_NotificationLayout.setOnClickPendingIntent(R.id.bt_notif_play, IPendingPlayIntent);
        //remoteViews_NotificationLayout.setOnClickPendingIntent(R.id.bt_notif_play, IPendingPlayingIntent);//#esto debe ser al hacer play
        remoteViews_NotificationLayout.setOnClickPendingIntent(R.id.bt_notif_stop, IPendingStopIntent);
        remoteViews_NotificationLayout.setOnClickPendingIntent(R.id.bt_notif_next, IPendingNextIntent);
        remoteViews_NotificationLayout.setOnClickPendingIntent(R.id.bt_notif_close, IPendingCloseIntent);

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

    private String stripText(InputStream pdfInputStream, int page) {
        Log.i(TAG, "stripText hilo: "+Thread.currentThread().getName());
        String textParsed = "";
        PDDocument document = null;
        //stateSpeak=Utils.S
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
            catch (IOException e) {
                Log.e("PdfBox-Android-Sample", "Exception thrown while closing document", e);
            }
        }
        return textParsed;
    }

    private TextToSpeech.OnInitListener getOnIntListener(){
        return new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int i) {
                Log.i(TAG," onInit i = "+i);
                textToSpeech.setLanguage(Locale.getDefault());
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.i(TAG, "onInit: "+textToSpeech.getVoices().toString());
                }*/
            }
            @Override
            protected void finalize() throws Throwable {
                super.finalize();//cuando le das shutdown creo
                Log.i(TAG, " Finalizo textToSpeech");
            }
        };
    }

    private UtteranceProgressListener getUtteranceProgressListener(){
        return new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                Log.i(TAG," onStart");
                stateSpeak = Utils.STATE_PLAYING;
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
                Log.i(TAG, "onDone: ");
                if (stateSpeak!= Utils.STATE_STOPED) {
                    next();
                }
            }
            @Override
            public void onError(String s) {
                Log.i(TAG," onError: "+s);
            }
        };
    }

    public byte getStateSpeak() {
        return stateSpeak;
    }

    @Override
    public void onDestroy() {
        if(disposable!=null)disposable.dispose();
        super.onDestroy();
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: echo");
        return null;
    }

}
