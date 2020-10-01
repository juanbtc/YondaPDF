package org.jbtc.yondapdf.entidad;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "book")
public class Book {
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "uri")
    String uri;
    @ColumnInfo(name = "titulo")
    String titulo;
    @ColumnInfo(name = "pagetag")
    int pageTag;
    @ColumnInfo(name = "pages")
    int pages;
    @ColumnInfo(name = "bitmap")
    String bitmap;
    @Ignore
    boolean checked;

    public Book() {
        this.id=0;
        this.uri = "";
        this.titulo = "";
        this.pageTag = -1;
        this.pages = 0;
        this.bitmap = "";
        checked=false;
    }

    public Book(String uri, String titulo, int pageTag, int pages, String bitmap) {
        this.uri = uri;
        this.titulo = titulo;
        this.pageTag = pageTag;
        this.pages = pages;
        this.bitmap = bitmap;
        checked=false;
    }

    @Ignore
    public Book(int id, String uri, String titulo, int pageTag, int pages, String bitmap) {
        this.id = id;
        this.uri = uri;
        this.titulo = titulo;
        this.pageTag = pageTag;
        this.pages = pages;
        this.bitmap = bitmap;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    @Ignore
    public boolean isPageTagInc1Unfinished(){
        int n=pageTag+1;
        if(n<pages){
            pageTag=n;
            return true;
        }else{
            return false;
        }
    }

    @Ignore
    public int getPageTagRead(){return pageTag+1;}

    public int getPageTag() {
        return pageTag;
    }

    public void setPageTag(int pageTag) {
        //if(0<=pageTag&&pageTag<pages) {
            this.pageTag = pageTag;
        //}
    }

    public boolean setPageTagValidate(int pageTag) {
        if(0<=pageTag&&pageTag<pages) {
            this.pageTag = pageTag;
            return true;
        }else{
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /*
    public boolean isUnfinished(){
        return pageTag<pages;
    }*/

    @Ignore
    public void decPageTag1() {
        int n=pageTag-1;
        if (n>=0){
            pageTag=n;
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
