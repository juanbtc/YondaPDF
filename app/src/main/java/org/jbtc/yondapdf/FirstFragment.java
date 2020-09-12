package org.jbtc.yondapdf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import org.jbtc.yondapdf.adapter.AdapterBook;
import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.databinding.FragmentFirstBinding;
import org.jbtc.yondapdf.entidad.Book;

import java.io.InputStream;
import java.util.List;

public class FirstFragment extends Fragment {

    private View view;
    private static final String dbName = "bookslightnovel";

    //recicler
    //private RecyclerView recyclerView;
    private AdapterBook mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FragmentFirstBinding binding;
    private RoomDatabaseBooksLN rdb;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //view = inflater.inflate(R.layout.fragment_first, container, false);
        rdb = Room.databaseBuilder(getContext(),
                RoomDatabaseBooksLN.class, dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .build();
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        view = binding.getRoot();

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

        return view;
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                int pages=0;
                try {
                    InputStream i = getContext().getContentResolver().openInputStream(uri);
                    PDDocument pdf = PDDocument.load(i);
                    pages = pdf.getNumberOfPages();
                }catch (Exception e){e.printStackTrace();}
                String name = uri.getLastPathSegment();
                if(name.contains(":")){
                    String [] nm = name.split(":");
                    name = nm[nm.length-1];
                }if(name.contains("/")){
                    String [] nm = name.split("/");
                    name = nm[nm.length-1];
                }
                Book b = new Book(uri.toString(),name,0,pages);
                rdb.bookDAO().insertBook(b);
                mAdapter.updateList(rdb.bookDAO().getAll());
                //mAdapter.notifyDataSetChanged();
                //binding.rvListBook.upda;
            }
        }
    }

    private ActionBar getActionBarFromMainActivity() {
        if(getActivity() instanceof MainActivity){
            return ((MainActivity)getActivity()).getSupportActionBar();
        }else{return null;}
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