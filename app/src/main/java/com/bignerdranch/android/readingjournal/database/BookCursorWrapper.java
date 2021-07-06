package com.bignerdranch.android.readingjournal.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.readingjournal.Book;
import com.bignerdranch.android.readingjournal.database.BookDbSchema.BookTable;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

import static java.lang.Long.valueOf;

public class BookCursorWrapper extends CursorWrapper {

    public BookCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Book getBook(){
        String uuidString = getString(getColumnIndex(BookTable.cols.UUID));
        String title = getString(getColumnIndex(BookTable.cols.TITLE));
        String summary = getString(getColumnIndex(BookTable.cols.SUMMARY));
        long date = getLong(getColumnIndex(BookTable.cols.DATE));
        int isCompleted = getInt(getColumnIndex(BookTable.cols.COMPLETED));
        String friend = getString(getColumnIndex(BookTable.cols.FRIEND));

        Book book = new Book(UUID.fromString(uuidString));
        book.setTitle(title);
        book.setSummary(summary);
        book.setDate(new Date(date));
        book.setCompleted(isCompleted != 0);
        book.setFriend(friend);

        return book;
    }
}
