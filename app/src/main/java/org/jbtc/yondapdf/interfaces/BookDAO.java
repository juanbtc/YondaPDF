package org.jbtc.yondapdf.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.jbtc.yondapdf.entidad.Book;

import java.util.List;

@Dao
public interface BookDAO {
    @Query("SELECT * FROM book")
    List<Book> getAll();

    @Query("SELECT * FROM Book WHERE id IN (:userIds)")
    List<Book> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM Book WHERE id = :userIds")
    List<Book> loadAllById(int userIds);

    @Query("SELECT * FROM Book WHERE id = :userId")
    Book getBookById(int userId);

    @Insert
    void insertAll(Book... books);

    @Insert
    void insertBook(Book book);

    @Update
    int updateBook(Book book);

    @Delete
    void delete(Book book);

    @Delete
    int deleteAll(List<Book> books);



}
