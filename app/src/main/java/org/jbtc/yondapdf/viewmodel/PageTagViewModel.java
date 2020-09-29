package org.jbtc.yondapdf.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.jbtc.yondapdf.entidad.Book;

public class PageTagViewModel extends ViewModel {

    private MutableLiveData<Book> bookMLD;

    public LiveData<Book> getPageTag(){
        if(bookMLD==null)
            bookMLD=new MutableLiveData<Book>();
        return bookMLD;
    }

    public void setPageTag(Book book){
            bookMLD.setValue(book);
    }

}