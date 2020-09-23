package org.jbtc.yondapdf;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jbtc.yondapdf.adapter.AdapterBook;
import org.jbtc.yondapdf.adapter.AdapterNovel;
import org.jbtc.yondapdf.database.DataBaseNovel;
import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.databinding.FragmentFirstBinding;
import org.jbtc.yondapdf.databinding.FragmentNovelBinding;
import org.jbtc.yondapdf.entidad.Book;
import org.jbtc.yondapdf.model.Novela;
import org.jbtc.yondapdf.services.ServiceTTS;

import java.util.List;

public class NovelFragment extends Fragment {

    FragmentNovelBinding binding;

    private AdapterNovel mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public static DataBaseNovel dbnovel;

    public NovelFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNovelBinding.inflate(inflater,container,false);
        dbnovel = new DataBaseNovel(getContext());
        setupRecycler();
        return binding.getRoot();
    }

    private void setupRecycler() {
        //recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        binding.rvListNovels.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        binding.rvListNovels.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        List<Novela> items = dbnovel.getNovelas();
        mAdapter = new AdapterNovel(items);
        mAdapter.setoCvListener(new AdapterNovel.OnClickCardViewListener() {
            @Override
            public void OnClickCardView(Novela novela) {
                setupForeground(novela.getId());
            }
        });
        binding.rvListNovels.setAdapter(mAdapter);
    }

    private void setupForeground(int id) {
        //String input = "input";
        Intent serviceIntent = new Intent(getContext(), ServiceTTS.class);
        serviceIntent.setAction(Utils.ACTION_PLAY_NOVEL);
        serviceIntent.putExtra("id", id);
        ContextCompat.startForegroundService(getContext(), serviceIntent);
    }
}