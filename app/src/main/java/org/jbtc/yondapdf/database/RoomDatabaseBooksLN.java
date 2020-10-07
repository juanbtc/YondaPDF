package org.jbtc.yondapdf.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import org.jbtc.yondapdf.entidad.Book;
import org.jbtc.yondapdf.interfaces.BookDAO;

@Database(entities = {Book.class}, version = 1)
public abstract class RoomDatabaseBooksLN extends RoomDatabase{

        public abstract BookDAO bookDAO();

}
