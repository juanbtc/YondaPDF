package org.jbtc.yondapdf;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.Locale;

public class SecondFragment extends Fragment implements View.OnClickListener {
    private static final String PRIMARY = "primary";
    private static final String LOCAL_STORAGE = "/storage/emulated/0/";
    private static final String EXT_STORAGE = "/storage/7764-A034/";
    private static final String COLON = ":";

    private View view;
    private static final String TAG = "nHomef";
    private static final String dbName = "bookslightnovel";
    private TextToSpeech textToSpeech;
    private FragmentSecondBinding binding;
    private RoomDatabaseBooksLN rdb;
    //private PDFView pdf;
    private Book book;
    private Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        binding = FragmentSecondBinding.inflate(inflater, container, false);

        rdb = Room.databaseBuilder(getContext(),
                RoomDatabaseBooksLN.class, dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .build();

        Log.i(TAG, "onCreateView: bundle id: "+getArguments().getInt("id"));
        book = rdb.bookDAO().getBookById(getArguments().getInt("id"));
        Log.i(TAG, "onCreateView: id: "+book.getId());
        uri = Uri.parse(book.getUri());

        textToSpeech  =new TextToSpeech(getContext(),new TextToSpeech.OnInitListener(){
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                Log.i(TAG +" ini ","Finalizo cargar pdf");
            }

            @Override
            public void onInit(int i) {
                Log.i(TAG +" ini ","i = "+i);
                textToSpeech.setLanguage(Locale.getDefault());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.i(TAG, "onInit: "+textToSpeech.getVoices().toString());
                }
            }
        } );

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                Log.i(TAG +" ini ","Start");
            }

            @Override
            public void onDone(String s) {
                //if(s.equals("id"))Log.i("T_utt_compl" ,"yes id");
                Log.i(TAG +" ini ","done");
            }

            @Override
            public void onError(String s) {
                Log.i(TAG +" ini ","error:"+s);
            }
        });


        //pdf = view.findViewById(R.id.pdfView);
        setup();
        binding.pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pa=binding.pdfView.getCurrentPage();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    PdfDocument p = new PdfDocument();
                }
                PDDocument document = new PDDocument();
                //PDFTextStripper sd;
                //com.shockwave.pdfium.PdfDocument a = new com.shockwave.pdfium.PdfDocument();

                //String txt= stripText(getPdfTmpPath(uri),pa);
                File f=null;
                String p="";
                try {
                    //p=UtilsURI.getPathFromUri(getContext(),uri);
                    //p="/storage/C43E-1DE8/ExtractedApks/07-Re Zero Volumen - 05.pdf";
                    //p=getRealPathFromURI(getContext(),uri);

                    Log.i(TAG, "onClick: utils "+p);
                    f = new File(p);
                    InputStream i = getContext().getContentResolver().openInputStream(uri);
                    //File g = new File(new URI(uri.getPath()));
                    Log.i(TAG, "onClick: pa: "+pa);
                    String txt= stripText(i,pa+1);
                    Log.i(TAG, "onClick: texto : "+txt);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textToSpeech.speak(txt,TextToSpeech.QUEUE_FLUSH,null,"id");
                        //textToSpeech.
                    }
                }catch (Exception e){e.printStackTrace();}
                Log.i(TAG, "onClick: "+f.getAbsolutePath());
                //Toast.makeText(getContext(),"page: "+pa,Toast.LENGTH_LONG).show();
            }
        });
        view = binding.getRoot();
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //pdf.fitToWidth();
        //pdf.setMinimumWidth(0);
        binding.pdfView.fromUri(uri)
                .spacing(10)
                .onTap(new OnTapListener() {
                    @Override
                    public boolean onTap(MotionEvent e) {
                        return false;
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        binding.tvBookpdfPage.setText(String .valueOf(page+1));
                    }
                })
                .load();

        binding.btSecPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_FirstFragment);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            if (resultData != null) {
                final Uri uri = resultData.getData();
                //Toast.makeText(this, uri.getPath(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "my uri: "+uri.getPath());

                //readPdfFile(uri);
                Toast.makeText(getActivity(),"asdasf",Toast.LENGTH_LONG).show();
                //NavHostFragment.findNavController()  .navigate(R.id.action_FirstFragment_to_SecondFragment);
                //navController.navigate(R.id.SecondFragment);

                String fullPath;
                //convert from uri to full path
                if(uri.getPath().contains("primary")) {
                    fullPath = LOCAL_STORAGE + uri.getPath().split(COLON)[1];
                }
                else {
                    fullPath = EXT_STORAGE + uri.getPath().split(COLON)[1];
                }

                final PDFView pdf=view.findViewById(R.id.pdfView);
                pdf.fitToWidth();
                pdf.setMinimumWidth(0);
                pdf.fromUri(uri)
                        //.spacing(10)
                        .onTap(new OnTapListener() {
                            @Override
                            public boolean onTap(MotionEvent e) {
                                return false;
                            }
                        })
                        .onPageChange(new OnPageChangeListener() {
                            @Override
                            public void onPageChanged(int page, int pageCount) {
                                //Toast.makeText(getContext(),""+page,Toast.LENGTH_LONG).show();
                            }
                        })
                        .load();
                //pdf.fitToWidth();
                //getPdfTmpPath(uri);
                String fullFilePath="";
                try {
                    fullFilePath=Util2A.getPath(getContext(), uri);
                }catch (Exception e){}

                Log.i(TAG, "onClick: "+fullFilePath);
                //File s = new File(uri.getPath());
                //Log.i(tag, "here onActivityResult: "+s.getAbsolutePath());

                final String finalFullFilePath = fullFilePath;


            }
        }
    }

    public String stripText(InputStream  pathPDF,int page) {
        String parsedText = null;
        PDDocument document = null;
        try {
            //document = PDDocument.load(assetManager.open(PathPDF));
            //document = PDDocument.load(assetManager.open("/sdcard/07-Re Zero Volumen - 05.pdf"));
            //document = PDDocument.load(assetManager.open("/storage/self/primary/07-Re Zero Volumen - 05.pdf"));

            document = PDDocument.load(pathPDF);
            PDFRenderer pdfRender=new PDFRenderer(document);
            Bitmap image = pdfRender.renderImage(0);
        } catch(IOException e) {
            //Log.e("PdfBox-Android-Sample", "Exception thrown while loading document to strip", e);
            e.printStackTrace();
        }

        try {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(page);
            pdfStripper.setEndPage(page);
            parsedText = "Parsed text: " + pdfStripper.getText(document);
        }
        catch (IOException e)
        {
            //Log.e("PdfBox-Android-Sample", "Exception thrown while stripping text", e);
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
        return parsedText;
    }

    private void setup() {
        // Enable Android-style asset loading (highly recommended)
        PDFBoxResourceLoader.init(getContext());
        // Find the root of the external storage.
        //root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // Need to ask for write permissions on SDK 23 and up, this is ignored on older versions
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onClick(View view) {

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