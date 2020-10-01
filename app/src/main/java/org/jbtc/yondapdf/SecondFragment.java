package org.jbtc.yondapdf;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionEntry;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;
import android.net.IpPrefix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.DialogCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.google.android.material.snackbar.Snackbar;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.databinding.DialogPageSpeechBinding;
import org.jbtc.yondapdf.databinding.FragmentFirstBinding;
import org.jbtc.yondapdf.databinding.FragmentSecondBinding;
import org.jbtc.yondapdf.dialog.DialogoPageSpeech;
import org.jbtc.yondapdf.entidad.Book;
import org.jbtc.yondapdf.services.ServiceTTS;
import org.jbtc.yondapdf.viewmodel.PageTagViewModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Locale;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class SecondFragment extends Fragment {

    private static final String TAG = "nHomef";
    private Disposable disposable;
    private RoomDatabaseBooksLN rdb;
    private FragmentSecondBinding binding;
    private Book book;
    private Uri uri;
    private PageTagViewModel pageTagViewModel;
    private BroadcastRecibido broadcastRecibido;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        binding = FragmentSecondBinding.inflate(inflater, container, false);

        rdb = Room.databaseBuilder(getContext(),
                RoomDatabaseBooksLN.class, Utils.dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .build();

        int id = getArguments().getInt("id");
        book = rdb.bookDAO().getBookById(id);
        uri = Uri.parse(book.getUri());

        broadcastRecibido = new BroadcastRecibido();

        setupPermisos();

        setupControlPlayer();

        setupForeground(id);

        initViewModels();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActionBarFromMainActivity().setTitle(book.getTitulo());
        //int page=book.getPageTagRead();
        //getMainActivity().getActivityMainBinding().tvMainPageText.setText(String.valueOf(book.getPageTagRead()));
        //binding.tvBookpdfPage.setText(String.valueOf(book.getPageTagRead()));
        binding.pdfView.fromUri(uri)
                .spacing(10)
                .defaultPage(book.getPageTag())
                .onTap(new OnTapListener() {
                    @Override
                    public boolean onTap(MotionEvent e) {
                        Log.i(TAG, "onTap: Entro a hablar");
                        if(book.setPageTagValidate(binding.pdfView.getCurrentPage())) {
                            rdb.bookDAO().updateBook(book);
                            //speakBook(book.getPageTag()+1);
                            updatesUIPageTag();
                        }
                        return true;

                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        binding.tvBookpdfPage.setText(String .valueOf(page+1));
                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "2do onError: "+t.getMessage());
                        Snackbar.make(binding.getRoot(),"Error al Cargar el Documento",Snackbar.LENGTH_LONG).show();
                    }
                })
                .onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {
                        Log.e(TAG, "onPageError: page: "+page+" msg: "+t.getMessage());
                    }
                })
                .load();
        //binding.pdfView.setKeepScreenOn(true);//tal vez
        binding.tvBookpdfPage.setText(String.valueOf(binding.pdfView.getCurrentPage()));//try this

        getMainActivity().binding.flMainPageicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogoPageSpeech dialogoPageSpeech = new DialogoPageSpeech(book);
                dialogoPageSpeech.show(getParentFragmentManager(),DialogoPageSpeech.tag);
                dialogoPageSpeech.setNoticeDialogListener(new DialogoPageSpeech.NoticeDialogListener() {
                    @Override
                    public void onDialogPositiveClick(int page) {
                        if(book.setPageTagValidate(page)){//tod0:validar que page este en el rango de paginas del book
                            if (rdb.bookDAO().updateBook(book)>0) {//ya esta validado en el dialog pero x si acaso lo dejo
                                updatesUIPageTag();
                            }
                        }
                    }
                });

            }
        });
        binding.tvBookpdfPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMainActivity().setTextSizeToolbar(20f,"2do");
            }
        });

        //getActivity().invalidateOptionsMenu();
        getMainActivity().getActivityMainBinding().flMainPageicon.setVisibility(View.VISIBLE);
        setHasOptionsMenu(true);
    }

    private void setupForeground(int id) {
        Log.i(TAG, "setupForeground: ");
        Intent serviceIntent = new Intent(getContext(), ServiceTTS.class);
        serviceIntent.setAction(Utils.ACTION_START);
        serviceIntent.putExtra("id", id);
        Log.i(TAG, "setupForeground: id: "+id);
        ContextCompat.startForegroundService(getContext(), serviceIntent);
    }

    private void updatesUIPageTag(){
        pageTagViewModel.setPageTag(book);
        setupForeground(book.getId());//algo pasa
    }

    public void updatesUIPageTagNoService(){
        Log.i(TAG, "updatesUIPageTagNoService: tag = "+book.getPageTag());
        book = rdb.bookDAO().getBookById(book.getId());
        Log.i(TAG, "updatesUIPageTagNoService: tag = "+book.getPageTag());
        pageTagViewModel.setPageTag(book);
    }

    public void initViewModels(){
        pageTagViewModel = new ViewModelProvider(this).get(PageTagViewModel.class);
        pageTagViewModel.getPageTag().observe(getViewLifecycleOwner(), new Observer<Book>() {
            @Override
            public void onChanged(Book book) {
                //binding.tvBookpdfPage.setText(String.valueOf(book.getPageTagRead()));
                getMainActivity().getActivityMainBinding().tvMainPageText.setText(String.valueOf(book.getPageTagRead()));
            }
        });
        pageTagViewModel.setPageTag(book);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_main, menu);
        //menu.findItem(R.id.action_botspeak).setVisible(false);
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        getMainActivity().setTextSizeToolbar(20f,"2do onPrepare");
        //menu.findItem(R.id.action_botspeak).setVisible(false);
    }




    private ActionBar getActionBarFromMainActivity() {
        if(getActivity() instanceof MainActivity){
            return ((MainActivity)getActivity()).getSupportActionBar();
        }else{return null;}
    }

    private MainActivity getMainActivity(){
        if(getActivity() instanceof MainActivity)
        return ((MainActivity)getActivity());
        else return null;
    }

    private void setupControlPlayer() {
        binding.btSecPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ServiceTTS.stateSpeak==Utils.STATE_NOT_INIT) {
                    Log.i(TAG, "onClick: setup control player entro a state_not_init");
                    setupForeground(book.getId());

                    Intent IPrevIntent = new Intent(getContext(), ServiceTTS.class);
                    IPrevIntent.setAction(Utils.ACTION_PREV);
                    ContextCompat.startForegroundService(getContext(), IPrevIntent);
                }else{
                    Log.i(TAG, "onClick: servicio ya iniciado player no entro a state_not_init");
                    Intent IPrevIntent = new Intent(getContext(), ServiceTTS.class);
                    IPrevIntent.setAction(Utils.ACTION_PREV);
                    ContextCompat.startForegroundService(getContext(), IPrevIntent);
                }
            }
        });
        binding.btSecPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: play status service:"+ServiceTTS.stateSpeak);
                if(ServiceTTS.stateSpeak==Utils.STATE_NOT_INIT) {
                    Log.i(TAG, "onClick: setup control player entro a state_not_init");
                    setupForeground(book.getId());

                    Intent IPlayIntent = new Intent(getContext(), ServiceTTS.class);
                    IPlayIntent.setAction(Utils.ACTION_PLAY);
                    ContextCompat.startForegroundService(getContext(), IPlayIntent);
                }else {
                    Log.i(TAG, "onClick: status services distinto de init");
                    Intent IPlayIntent = new Intent(getContext(), ServiceTTS.class);
                    IPlayIntent.setAction(Utils.ACTION_PLAY);
                    ContextCompat.startForegroundService(getContext(), IPlayIntent);
                }
            }
        });
        binding.btSecStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ServiceTTS.stateSpeak==Utils.STATE_NOT_INIT) {
                    Log.i(TAG, "onClick: setup control player entro a state_not_init");
                    setupForeground(book.getId());
                }else {
                    Intent IStopIntent = new Intent(getContext(), ServiceTTS.class);
                    IStopIntent.setAction(Utils.ACTION_STOP);
                    ContextCompat.startForegroundService(getContext(), IStopIntent);
                }
            }
        });
        binding.btSecNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ServiceTTS.stateSpeak==Utils.STATE_NOT_INIT) {
                    Log.i(TAG, "onClick: setup control player entro a state_not_init");
                    setupForeground(book.getId());

                    Intent INextIntent = new Intent(getContext(), ServiceTTS.class);
                    INextIntent.setAction(Utils.ACTION_NEX);
                    ContextCompat.startForegroundService(getContext(), INextIntent);
                }else {
                    Intent INextIntent = new Intent(getContext(), ServiceTTS.class);
                    INextIntent.setAction(Utils.ACTION_NEX);
                    ContextCompat.startForegroundService(getContext(), INextIntent);
                }
            }
        });

    }

    private void setupPermisos() {
        PDFBoxResourceLoader.init(getContext());
        // Need to ask for write permissions on SDK 23 and up, this is ignored on older versions
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("yondapdf.pagetag.changed");
        getContext().registerReceiver(broadcastRecibido, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(broadcastRecibido!=null) {//no es necesario
            getContext().unregisterReceiver(broadcastRecibido);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        //disposable.dispose();
    }

    private class BroadcastRecibido extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: ");
            String accion = intent.getAction();
            if(intent.getExtras().getString("todo")!=null) {
                Log.i(TAG, "onReceive: entro get estras");
                String todo = intent.getExtras().getString("todo");
                switch (todo) {
                    case "update": {
                        Log.i(TAG, "onReceive: update");
                        updatesUIPageTagNoService();
                        break;
                    }
                }
            }

        }
    }

}