package org.jbtc.yondapdf.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jbtc.yondapdf.R;
import org.jbtc.yondapdf.entidad.Book;

import java.io.File;
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
        holder.tvName.setText(items.get(position).getTitulo());
        holder.tvPages.setText(String.valueOf(items.get(position).getPages()));
        holder.tvTag.setText(String.valueOf(items.get(position).getPageTagRead()));
            Bitmap bitmap = BitmapFactory.decodeFile(items.get(position).getBitmap());
        holder.ivBitmap.setImageBitmap(bitmap);
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
        private TextView tvName;
        private TextView tvPages;
        private TextView tvTag;
        private ImageView ivBitmap;
        View v;
        public ViewHolderBook(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_book_name);
            tvPages = v.findViewById(R.id.tv_book_pages);
            tvTag = v.findViewById(R.id.tv_book_tag);
            ivBitmap= v.findViewById(R.id.iv_card_page1);
            this.v=v;
        }
    }

    private Bitmap base64ToBitmap(String base64){
        byte[] bytarray = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytarray, 0, bytarray.length);
    }

    public interface OnClickCardViewListener{
        public void OnClickCardView(Book book);
    }
    OnClickCardViewListener oCvListener;

    public void setoCvListener(OnClickCardViewListener oCvListener) {
        this.oCvListener = oCvListener;
    }
}
