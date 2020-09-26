package org.jbtc.yondapdf.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import org.jbtc.yondapdf.FirstFragment;
import org.jbtc.yondapdf.R;
import org.jbtc.yondapdf.entidad.Book;

import java.util.ArrayList;
import java.util.List;

public class AdapterBook extends Adapter<AdapterBook.ViewHolderBook> {

    private static final String TAG = "iAdapterBook";
    List<Book> items;
    List<Book> itemsSelected;
    Fragment f;
    public AdapterBook(List<Book> items, Fragment f) {
        this.items = items;
        this.itemsSelected = new ArrayList<>();
        this.f=f;
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

        if (getFirstFragment().isActionModeActive()) {
            holder.cbSelect.setVisibility(View.VISIBLE);
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: 7854 card checked: "+items.get(position).isChecked());
                    holder.cbSelect.setVisibility(View.VISIBLE);
                    setChecked(holder,position);
                }
            });
        } else {
            holder.cbSelect.setVisibility(View.GONE);
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (oCvListener != null) oCvListener.OnClickCardView(items.get(position));
                }
            });
            holder.v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (oCvListener != null) {
                        holder.cbSelect.setVisibility(View.VISIBLE);
                        oCvListener.OnLongClickCardView();
                        Log.i(TAG, "onClick: 7855 card long checked: "+items.get(position).isChecked());
                        setChecked(holder,position);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void setChecked(ViewHolderBook holder,int position){
        if(items.get(position).isChecked()){
            items.get(position).setChecked(false);
            holder.cbSelect.setChecked(items.get(position).isChecked());
            itemsSelected.remove(items.get(position));
        }else{
            items.get(position).setChecked(true);
            holder.cbSelect.setChecked(items.get(position).isChecked());
            itemsSelected.add(items.get(position));
        }
    }

    public void cleanItemsSelected(){
        Log.i(TAG, "cleanItemsSelected: Limpiado");
        for(Book book:items) {
            book.setChecked(false);
        }
        itemsSelected.clear();
    }

    private FirstFragment getFirstFragment(){
        if( f instanceof FirstFragment)
            return (FirstFragment)f;
        else
            return null;
    }

    public void updateList(List<Book> books){
        items = books;
        notifyDataSetChanged();
    }

    public class ViewHolderBook extends ViewHolder {
        // each data item is just a string in this case
        private TextView tvName;
        private TextView tvPages;
        private TextView tvTag;
        private ImageView ivBitmap;
        private CheckBox cbSelect;
        View v;
        public ViewHolderBook(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_book_name);
            tvPages = v.findViewById(R.id.tv_book_pages);
            tvTag = v.findViewById(R.id.tv_book_tag);
            ivBitmap = v.findViewById(R.id.iv_card_page1);
            cbSelect = v.findViewById(R.id.cb_card_select);
            this.v=v;
        }
    }

    public interface OnClickCardViewListener{
        public void OnClickCardView(Book book);
        public void OnLongClickCardView();
    }
    OnClickCardViewListener oCvListener;
    public void setOnClickVListener(OnClickCardViewListener oCvListener) {
        this.oCvListener = oCvListener;
    }
}
