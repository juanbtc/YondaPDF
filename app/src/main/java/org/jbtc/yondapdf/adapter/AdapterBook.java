package org.jbtc.yondapdf.adapter;

import android.content.Context;
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
                    Log.i(TAG, "onClick: 7854 card checked: "+items.get(position).isChecked());
                    //holder.cbSelect.setVisibility(View.VISIBLE);
                    setChecked(holder,position);
                }else {
                    //holder.cbSelect.setVisibility(View.GONE);
                    if (oCvListener != null) oCvListener.OnClickCardView(items.get(position));//habre el PDF
                }
            }
        });
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!getFirstFragment().isActionModeActive()) {
                    if (oCvListener != null) {
                        Log.i(TAG, "onClick: 7855 card long checked: " + items.get(position).isChecked());
                        oCvListener.OnLongClickCardView();//activa el actionMode
                        //holder.cbSelect.setVisibility(View.VISIBLE);
                        setChecked(holder, position);
                        //notifyDataSetChanged();
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
        if( rdb.bookDAO().deleteAll(itemsSelected)>0 ) {
            items.removeAll(itemsSelected);
            notifyDataSetChanged();
        }
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
            //List<Book> itemsCopy = new ArrayList<>(items);
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
