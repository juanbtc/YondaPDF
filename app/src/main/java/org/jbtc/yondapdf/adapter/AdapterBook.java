package org.jbtc.yondapdf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jbtc.yondapdf.R;
import org.jbtc.yondapdf.entidad.Book;

import java.util.List;

public class AdapterBook extends RecyclerView.Adapter<AdapterBook.ViewHolderBook> {
    List<Book> items;
    public AdapterBook(List<Book> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolderBook onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_book ,parent,false);
        ViewHolderBook viewHolderBook = new ViewHolderBook(v);
        return viewHolderBook;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBook holder, final int position) {
        holder.tvTitle.setText(items.get(position).getTitulo());
        holder.tvPath.setText(items.get(position).getUri());
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

    public void updateList(List<Book> books){
        items = books;
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolderBook extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvTitle;
        public TextView tvPath;
        View v;
        public ViewHolderBook(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tv_book_title);
            tvPath = v.findViewById(R.id.tv_book_path);
            this.v=v;
        }
    }

    public interface OnClickCardViewListener{
        public void OnClickCardView(Book book);
    }
    OnClickCardViewListener oCvListener;

    public void setoCvListener(OnClickCardViewListener oCvListener) {
        this.oCvListener = oCvListener;
    }
}
