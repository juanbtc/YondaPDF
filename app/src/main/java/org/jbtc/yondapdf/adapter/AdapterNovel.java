package org.jbtc.yondapdf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jbtc.yondapdf.R;
import org.jbtc.yondapdf.entidad.Book;
import org.jbtc.yondapdf.model.Novela;

import java.util.List;

public class AdapterNovel extends RecyclerView.Adapter<AdapterNovel.ViewHolderNovel> {
    List<Novela> items;
    public interface OnClickCardViewListener{
        public void OnClickCardView(Novela novela);
    }
    OnClickCardViewListener oCvListener;

    public void setoCvListener(OnClickCardViewListener oCvListener) {
        this.oCvListener = oCvListener;
    }
    public AdapterNovel(List<Novela> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolderNovel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_novel ,parent,false);
        AdapterNovel.ViewHolderNovel viewHolderNovel = new AdapterNovel.ViewHolderNovel(v);
        return viewHolderNovel;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNovel holder, int position) {
        holder.titulo.setText(items.get(position).getTitulo());
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(oCvListener!=null)oCvListener.OnClickCardView(items.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolderNovel extends RecyclerView.ViewHolder{
        TextView titulo;
        View v;
        public ViewHolderNovel(@NonNull View itemView) {
            super(itemView);
            v=itemView;
            titulo = itemView.findViewById(R.id.tv_cardview_titulo);
        }
    }
}
