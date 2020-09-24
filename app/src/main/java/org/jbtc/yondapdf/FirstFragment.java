package org.jbtc.yondapdf;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import org.jbtc.yondapdf.adapter.AdapterBook;
import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.databinding.FragmentFirstBinding;
import org.jbtc.yondapdf.entidad.Book;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FirstFragment extends Fragment {

    private static final String dbName = "bookslightnovel";
    private static final String TAG = "iFirstf";
    //recicler
    //private RecyclerView recyclerView;
    private AdapterBook mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FragmentFirstBinding binding;
    private RoomDatabaseBooksLN rdb;
    private Disposable disposable;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,

                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //view = inflater.inflate(R.layout.fragment_first, container, false);
        rdb = Room.databaseBuilder(getContext(),
                RoomDatabaseBooksLN.class, dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .build();
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent, 1);
            }
        });

        getActionBarFromMainActivity().setTitle(getResources().getString(R.string.app_name));
        //getActionBarFromMainActivity().;

        setupRecycler();

        getMainActivity().setTextSizeToolbar(30f);

        getMainActivity().getActivityMainBinding().flMainPageicon.setVisibility(View.GONE);

        return binding.getRoot();
    }

    private void setupRecycler() {
        //recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        binding.rvListBook.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        binding.rvListBook.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        List<Book> items = rdb.bookDAO().getAll();
        mAdapter = new AdapterBook(items);
        mAdapter.setoCvListener(new AdapterBook.OnClickCardViewListener() {
            @Override
            public void OnClickCardView(Book book) {
                Bundle b = new Bundle();
                b.putInt("id",book.getId());
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment,b);
            }
        });
        binding.rvListBook.setAdapter(mAdapter);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
        */
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //menu.findItem(R.id.action_botspeak).setVisible(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //menu.findItem(R.id.action_botspeak).setVisible(false);
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings: {
                return true;
            }
            case R.id.action_novelas: {
                Toast.makeText(getContext(),"accion completada desde fragment",Toast.LENGTH_LONG).show();
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            switch (requestCode){
                case 1:{
                    if (resultData != null) {
                        /*
                        Thread t=new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.pbFirstWait.setVisibility(View.VISIBLE);
                                    }
                                });

                                Book b=createBookFromUri(resultData.getData());

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.pbFirstWait.setVisibility(View.GONE);
                                        rdb.bookDAO().insertBook(b);
                                        mAdapter.updateList(rdb.bookDAO().getAll());

                                    }
                                });
                            }
                        });
                        t.start();
                        */
                        Observable.create(new ObservableOnSubscribe<Book>() {
                            @Override
                            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Book> emitter) throws Throwable {
                                try {
                                    emitter.onNext(createBookFromUri(resultData.getData()));
                                    emitter.onComplete();
                                }catch (Exception e){emitter.onError(e);e.printStackTrace();}
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getObserver());
                    }
                    break;
                }
            }

        }
    }

    private Book createBookFromUri(Uri uri){
        Log.i(TAG, "createBookFromUri: Hilo: "+Thread.currentThread().getName());
        int pages=0;
        String name = uri.getLastPathSegment();
        if(name.contains(":")){
            String [] nm = name.split(":");
            name = nm[nm.length-1];
        }if(name.contains("/")){
            String [] nm = name.split("/");
            name = nm[nm.length-1];
        }
        //String bitmap="";

        String pathDir = getActivity().getFilesDir()+"/png/";
        String pathFile=pathDir+name.trim().replace(" ","_")+".png";
        File dir = new File(pathDir);if(!dir.exists())dir.mkdir();
        String path="";
        try {
            InputStream i = getContext().getContentResolver().openInputStream(uri);
            PDDocument pdf = PDDocument.load(i);
            pages = pdf.getNumberOfPages();
            PDFRenderer renderer = new PDFRenderer(pdf);
            Bitmap bitmap = renderer.renderImage(0);

            File file = new File(pathFile);
            OutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            path = file.getAbsolutePath();
        }catch (Exception e){e.printStackTrace();}

        Log.i(TAG, "onActivityResult: file.getAbsolutePath()"+path);
        Book b = new Book(uri.toString(),name,0,pages,path);
        return b;
    }

    private Observer getObserver(){
        return new Observer<Book>() {
            private Book mbook;
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                disposable=d;
                binding.pbFirstWait.setVisibility(View.VISIBLE);
                Log.i(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Book book) {
                Log.i(TAG, "onNext: ");
                mbook=book;
                //binding.pbFirstWait.animate();
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                binding.pbFirstWait.setVisibility(View.GONE);
                rdb.bookDAO().insertBook(mbook);
                mAdapter.updateList(rdb.bookDAO().getAll());
            }
        };
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

    private String bitmapToBase64(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 10, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable!=null)disposable.dispose();
        Log.i(TAG, "onDestroy: destruido");
    }

    /*
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.findItem(R.id.action_botspeak).setVisible(false);
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    */
}