package com.bignerdranch.android.readingjournal.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.bignerdranch.android.readingjournal.Book;
import com.bignerdranch.android.readingjournal.database.BookDbSchema.BookTable;

import java.util.UUID;

import static com.bignerdranch.android.readingjournal.database.BookDbSchema.BookTable.NAME;

public class BookBaseHelper extends SQLiteOpenHelper{

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "bookBase.db";

    public BookBaseHelper (Context context){

        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + NAME + "(" +
                " _id integer primary key autoincrement, " +
                BookTable.cols.UUID + ", " +
                BookTable.cols.TITLE + ", " +
                BookTable.cols.SUMMARY + ", " +
                BookTable.cols.DATE + ", " +
                BookTable.cols.COMPLETED + ", " +
                BookTable.cols.FRIEND +
                ")"
        );
    }

    public void deleteBook(UUID uuid) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(BookTable.NAME, BookTable.cols.UUID+"=?", new String[]{uuid.toString()});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
