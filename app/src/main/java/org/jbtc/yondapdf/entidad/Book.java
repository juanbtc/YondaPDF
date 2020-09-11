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


    public Book(String uri, String titulo, int pageTag) {
        this.uri = uri;
        this.titulo = titulo;
        this.pageTag = pageTag;
    }

    @Ignore
    public Book(int id, String uri, String titulo, int pageTag) {
        this.id = id;
        this.uri = uri;
        this.titulo = titulo;
        this.pageTag = pageTag;
    }

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
}
