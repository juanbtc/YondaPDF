package org.jbtc.yondapdf;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.gms.ads.AdRequest;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import org.jbtc.yondapdf.adapter.AdapterBook;
import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.databinding.FragmentFirstBinding;
import org.jbtc.yondapdf.entidad.Book;

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

import androidx.appcompat.view.ActionMode;

public class FirstFragment extends Fragment {
    //region properties
    private static final String dbName = "bookslightnovel";
    private static final String TAG = "iFirstf";
    private RecyclerView.LayoutManager layoutManager;
    private FragmentFirstBinding binding;
    private RoomDatabaseBooksLN rdb;
    private AdapterBook mAdapter;
    private Disposable disposable;
    private ActionMode actionMode;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rdb = Room.databaseBuilder(getContext(),
                RoomDatabaseBooksLN.class, dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .build();
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(intent, 1);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        getMainActivity().setActionBarTille(getResources().getString(R.string.app_name));

        setupRecycler();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().setVisibilityIconTagPage(View.GONE);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.i(TAG, "onCreateOptionsMenu: ");

        inflater.inflate(R.menu.menu_main, menu);
        getMainActivity().setTextSizeToolbar(30f);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        searchViewItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                Log.i(TAG, "onMenuItemActionExpand: ");
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                Log.i(TAG, "onMenuItemActionCollapse: ");
                mAdapter.updateList(rdb.bookDAO().getAll());
                return true;
            }
        });

        SearchView searchView = (SearchView)searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "onQueryTextSubmit: ");
                mAdapter.updateListNoRefresh(rdb.bookDAO().getAll());
                mAdapter.getFilter().filter(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "onQueryTextChange: ");
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected: ");
        int id = item.getItemId();
        switch (id){
            case R.id.action_selection: {
                Log.i(TAG, "onOptionsItemSelected: action_novelas completada desde fragment");
                activeActionMode();
                setActionModeCountSelected(0);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void activeActionMode(){
        actionMode = getMainActivity().startSupportActionMode(actionModeCallback);
        mAdapter.notifyDataSetChanged();
    }

    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        binding.rvListBook.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        binding.rvListBook.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        List<Book> items = rdb.bookDAO().getAll();
        mAdapter = new AdapterBook(items,this);
        mAdapter.setOnClickVListener(new AdapterBook.OnClickCardViewListener() {
            @Override
            public void OnClickCardView(Book book) {
                    Bundle b = new Bundle();
                    b.putInt("id", book.getId());
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment, b);
            }

            @Override
            public void OnClickCardViewCountSelected(int countSelected) {
                setActionModeCountSelected(countSelected);
            }

            @Override
            public void OnLongClickCardView(int countSelected) {
                activeActionMode();
                setActionModeCountSelected(countSelected);
            }
        });
        binding.rvListBook.setAdapter(mAdapter);
    }

    private void setActionModeCountSelected(int countSelected){
        if(countSelected==1)actionMode.setTitle("1 Elemento seleccionado");
        else if(countSelected>=0)actionMode.setTitle(countSelected+" Elementos seleccionados");
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

                        Observable.create(new ObservableOnSubscribe<Book>() {
                            @Override
                            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Book> emitter) throws Throwable {
                                try {
                                    Uri uri = resultData.getData();
                                    Log.i(TAG, "subscribe: URI: "+uri.getPath()+" : "+uri.getEncodedPath()+" : "+uri.getPathSegments());
                                    final int takeFlags = resultData.getFlags()
                                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        getActivity().getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                    }
                                    emitter.onNext( createBookFromUri(uri) );
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
        File lk = new File(uri.toString());
        Log.i(TAG, "createBookFromUri: Uri print path: "+lk.getAbsolutePath());
        int pages=0;
        String name = uri.getLastPathSegment();
        if(name.contains(":")){
            String [] nm = name.split(":");
            name = nm[nm.length-1];
        }if(name.contains("/")){//obtego solo el nombre
            String [] nm = name.split("/");
            name = nm[nm.length-1];
        }

        String pathDir = Utils.getPathSDcard()+"/png/";
        String pathFile=pathDir+name.trim().replace(" ","_")+".png";
        File dir = new File(pathDir);if(!dir.exists())dir.mkdir();
        String path="";
        try {//creando imagen png del book
            InputStream i = getContext().getContentResolver().openInputStream(uri);
            PDDocument pdf = PDDocument.load(i);
            pages = pdf.getNumberOfPages();
            PDFRenderer renderer = new PDFRenderer(pdf);
            Bitmap bitmap = renderer.renderImage(0);

            File file = new File(pathFile);
            OutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, out);
            out.flush();
            out.close();
            path = file.getAbsolutePath();
        }catch (Exception e){e.printStackTrace();}

        Log.i(TAG, "onActivityResult: file.getAbsolutePath() "+path);
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

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_select_book, menu);

            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            Log.i(TAG, "onPrepareActionMode:  notifi changed");
            return true; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete: {
                    mAdapter.deleteSelection(rdb);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                }
            }
            return false;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            mAdapter.cleanItemsSelected();
            mAdapter.notifyDataSetChanged();
        }
    };

    public boolean isActionModeActive(){
        return actionMode!=null;
    }

    private MainActivity getMainActivity(){
        if(getActivity() instanceof MainActivity)
            return ((MainActivity)getActivity());
        else return null;
    }

    @Override
    public void onDestroy() {
        if (disposable!=null)disposable.dispose();
        Log.i(TAG, "onDestroy: destruido");
        super.onDestroy();
    }

}