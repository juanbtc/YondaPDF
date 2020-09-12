package org.jbtc.yondapdf;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.databinding.FragmentFirstBinding;
import org.jbtc.yondapdf.databinding.FragmentSecondBinding;
import org.jbtc.yondapdf.entidad.Book;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Locale;

public class SecondFragment extends Fragment {
    private static final String PRIMARY = "primary";
    private static final String LOCAL_STORAGE = "/storage/emulated/0/";
    private static final String EXT_STORAGE = "/storage/7764-A034/";
    private static final String COLON = ":";

    private static final String TAG = "nHomef";
    private static final String dbName = "bookslightnovel";
    private TextToSpeech textToSpeech;
    private FragmentSecondBinding binding;
    private RoomDatabaseBooksLN rdb;
    private Book book;
    private Uri uri;
    enum StadoSpeak {playin,end,stoped}
    StadoSpeak stadoSpeak =StadoSpeak.end;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        binding = FragmentSecondBinding.inflate(inflater, container, false);

        rdb = Room.databaseBuilder(getContext(),
                RoomDatabaseBooksLN.class, dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .build();

        book = rdb.bookDAO().getBookById(getArguments().getInt("id"));
        uri = Uri.parse(book.getUri());

        textToSpeech  =new TextToSpeech(getContext(),new TextToSpeech.OnInitListener(){
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
                stadoSpeak=StadoSpeak.playin;
            }

            @Override
            public void onDone(String s) {
                if (stadoSpeak!=StadoSpeak.stoped) {
                    next();
                }
            }

            @Override
            public void onError(String s) {
                Log.i(TAG +" ini ","error:"+s);
            }
        });


        setup();

        setupControlPlayer();

        setHasOptionsMenu(true);
        View view = binding.getRoot();
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActionBarFromMainActivity().setTitle(book.getTitulo());

        binding.pdfView.fromUri(uri)
                .spacing(10)
                .defaultPage(book.getPageTag()-1)
                .onTap(new OnTapListener() {
                    @Override
                    public boolean onTap(MotionEvent e) {
                        book.setPageTag(binding.pdfView.getCurrentPage()+1);
                        rdb.bookDAO().updateBook(book);
                        speakBook(book.getPageTag());
                        return true;
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        binding.tvBookpdfPage.setText(String .valueOf(page+1));
                    }
                })
                .load();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_main, menu);
        //menu.findItem(R.id.action_botspeak).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_botspeak).setVisible(false);
    }

    private ActionBar getActionBarFromMainActivity() {
        if(getActivity() instanceof MainActivity){
            return ((MainActivity)getActivity()).getSupportActionBar();
        }else{return null;}
    }

    private void setupControlPlayer() {
        binding.btSecPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prev();
            }
        });
        binding.btSecPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakBook(book.getPageTag());
            }
        });
        binding.btSecPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSpeak();
            }
        });
        binding.btSecStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSpeak();
            }
        });
        binding.btSecNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

    }

    public void next(){
        book.incPageTag1();
        //todo:validar in<pages
        rdb.bookDAO().updateBook(book);
        speakBook(book.getPageTag());
        Log.i(TAG +" ini ","done");
    }

    public void prev(){
        book.decPageTag1();
        //todo:validar in<pages
        rdb.bookDAO().updateBook(book);
        speakBook(book.getPageTag());
        Log.i(TAG +" ini ","done");
    }

    private void speakBook(int numPage){
        Log.i(TAG, "speakBook: num: "+numPage);
        try {
            if(textToSpeech.isSpeaking()){
                stopSpeak();
            }
            InputStream i = getContext().getContentResolver().openInputStream(uri);
            String txt= stripText(i,numPage);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(txt,TextToSpeech.QUEUE_FLUSH,null,String.valueOf(numPage));
                stadoSpeak=StadoSpeak.playin;
            }
        }catch (Exception e){e.printStackTrace();}
    }

    public String stripText(InputStream  pdfInputStream,int page) {
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

    public void stopSpeak(){
        stadoSpeak=StadoSpeak.stoped;
        textToSpeech.stop();
    }

    private void setup() {
        PDFBoxResourceLoader.init(getContext());
        // Need to ask for write permissions on SDK 23 and up, this is ignored on older versions
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    /*
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Files. };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }*/

}