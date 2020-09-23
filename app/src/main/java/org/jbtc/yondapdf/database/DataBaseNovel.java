package org.jbtc.yondapdf.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.jbtc.yondapdf.model.Novela;

import java.util.ArrayList;
import java.util.List;

public class DataBaseNovel extends SQLiteOpenHelper {
    private static final int dbversion=1;
    private static final String dbdir="/sdcard/nl/";
    private static final String dbName="novelas.db";
    private Context contexto;

    public DataBaseNovel(Context contexto){
        super(contexto, dbdir+dbName, null, dbversion);
        this.contexto=contexto;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public List<Novela> getNovelas(){
        List<Novela> novelas=new ArrayList<Novela>();
        Novela novela=new Novela();
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("SELECT * FROM novelaligera;", null);
            if (c.moveToFirst()) {
                //recoremos el cursor hasta el ultimo registro
                do {
                    novela.setId(c.getInt(0));
                    novela.setUrl(c.getString(1));
                    novela.setTitulo(c.getString(2));
                    novela.setCap(c.getString(2));
                    novelas.add(novela);
                    novela = new Novela();
                } while (c.moveToNext());
            }
            c.close();
        }
        db.close();
        return novelas;
    }

    public Novela getNovela(int id){
        List<Novela> novelas=new ArrayList<Novela>();
        Novela novela=new Novela();
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("SELECT * FROM novelaligera where id="+id+";", null);
            if (c.moveToFirst()) {
                //recoremos el cursor hasta el ultimo registro
                do {
                    novela.setId(c.getInt(0));
                    novela.setUrl(c.getString(1));
                    novela.setTitulo(c.getString(2));
                    novela.setCap(c.getString(3));
                    novelas.add(novela);
                    novela = new Novela();
                } while (c.moveToNext());
            }
            c.close();
        }
        db.close();
        return novelas.get(0);
    }
}
