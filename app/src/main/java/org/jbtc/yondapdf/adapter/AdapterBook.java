package org.jbtc.yondapdf.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import org.jbtc.yondapdf.FirstFragment;
import org.jbtc.yondapdf.R;
import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.entidad.Book;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdapterBook extends Adapter<AdapterBook.ViewHolderBook> implements Filterable {

    private static final String TAG = "iAdapterBook";
    private List<Book> items;
    private List<Book> itemsSelected;
    private Fragment f;

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

        if(getFirstFragment().isActionModeActive()){
            holder.cbSelect.setChecked(items.get(position).isChecked());
            holder.cbSelect.setVisibility(View.VISIBLE);
        }else{
            holder.cbSelect.setVisibility(View.GONE);
        }

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFirstFragment().isActionModeActive()) {
                    setChecked(holder,position);
                    if(oCvListener!=null)oCvListener.OnClickCardViewCountSelected(itemsSelected.size());
                }else {
                    if (oCvListener != null) oCvListener.OnClickCardView(items.get(position));
                }
            }
        });
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!getFirstFragment().isActionModeActive()) {
                    if (oCvListener != null) {
                        setChecked(holder, position);
                        oCvListener.OnLongClickCardView(itemsSelected.size());
                        return true;
                    }
                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void deleteSelection(RoomDatabaseBooksLN rdb){
        for(Book b:itemsSelected){
            String dirFile = b.getBitmap();
            if(rdb.bookDAO().delete(b)>0){
                items.remove(b);
                File png = new File(dirFile);
                if(png.exists())
                    png.delete();
            }
        }
        notifyDataSetChanged();
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
        Log.i(TAG, "cleanItemsSelected: Limpiando");
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
        Log.i(TAG, "updateList: ");
        items = books;
        notifyDataSetChanged();
    }

    public void updateListNoRefresh(List<Book> books){
        Log.i(TAG, "updateListNoRefresh: ");
        items = books;
    }
    
    @Override
    public Filter getFilter(){
        return filtro;
    }

    private Filter filtro = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence query) {
            List<Book> itemResult=new ArrayList<Book>();
            if(query==null||query.length()<1){
                itemResult.addAll(items);
            }else{
                String consulta = query.toString().toLowerCase().trim();
                for (Book b:items){
                    if(b.getTitulo().toLowerCase().trim().contains(consulta)){
                        itemResult.add(b);
                    }
                }
            }
            FilterResults filtroResultado = new FilterResults();
            filtroResultado.values = itemResult;
            return filtroResultado;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            items.clear();
            items.addAll( (List<Book>)filterResults.values );
            notifyDataSetChanged();
        }
    };

    public class ViewHolderBook extends ViewHolder {
        // each data item is just a string in this case
        private TextView tvName;
        private TextView tvPages;
        private TextView tvTag;
        private ImageView ivBitmap;
        private CheckBox cbSelect;
        private View v;

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
        public void OnClickCardViewCountSelected(int countSelected);
        public void OnLongClickCardView(int countSelected);
    }
    OnClickCardViewListener oCvListener;
    public void setOnClickVListener(OnClickCardViewListener oCvListener) {
        this.oCvListener = oCvListener;
    }

}
