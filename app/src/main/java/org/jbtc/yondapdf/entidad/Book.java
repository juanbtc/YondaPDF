package org.jbtc.yondapdf.entidad;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Query;

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

    public Book(String uri, String titulo, int pageTag, int pages, String bitmap) {
        this.uri = uri;
        this.titulo = titulo;
        this.pageTag = pageTag;
        this.pages = pages;
        this.bitmap = bitmap;
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
    public void incPageTag1(){
        this.pageTag++;
    }

    @Ignore
    public int getPageTagRead(){return pageTag+1;}

    public int getPageTag() {
        return pageTag;
    }

    public void setPageTag(int pageTag) {
        this.pageTag = pageTag;
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

    public boolean isUnfinished(){
        return pageTag<pages;
    }

    @Ignore
    public void decPageTag1() {
        this.pageTag--;
    }
}
