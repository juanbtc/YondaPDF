package org.jbtc.yondapdf.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.jbtc.yondapdf.Utils;
import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.entidad.Book;

import java.util.ArrayList;
import java.util.List;

public class PageTagViewModel extends ViewModel {
    private RoomDatabaseBooksLN rdb;
    private MutableLiveData<List<Book>> listaBookMLD;
    private List<Book> listaBook;

    public PageTagViewModel(Context context) {
        rdb = Room.databaseBuilder(context,
                RoomDatabaseBooksLN.class, Utils.dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .build();
    }

    public LiveData<List<Book>> getPageTag(){
        if(listaBookMLD==null){
            loadDataListaBook();
        }
        return listaBookMLD;
    }

    public void setPageTag(List<Book> book){
        if(rdb.bookDAO().updateBook(book.get(0))>0) {
            listaBookMLD.setValue(book);
        }
    }

    private void loadDataListaBook(){
        listaBookMLD=new MutableLiveData<List<Book>>();
        listaBook=rdb.bookDAO().getAll();
        listaBookMLD.setValue(listaBook);
    }
}