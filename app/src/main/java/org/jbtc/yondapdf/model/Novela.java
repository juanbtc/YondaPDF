package org.jbtc.yondapdf.model;

public class Novela {
    int id;
    String
    url,
    titulo,
    cap;

    public Novela() {
    }

    public Novela(String url, String titulo, String cap) {
        this.url = url;
        this.titulo = titulo;
        this.cap = cap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }
}
