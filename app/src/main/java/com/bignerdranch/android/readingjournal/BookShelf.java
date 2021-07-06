package com.bignerdranch.android.readingjournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.readingjournal.database.BookBaseHelper;
import com.bignerdranch.android.readingjournal.database.BookCursorWrapper;
import com.bignerdranch.android.readingjournal.database.BookDbSchema;
import com.bignerdranch.android.readingjournal.database.BookDbSchema.BookTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookShelf {
    private static BookShelf sBookShelf;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static BookShelf get(Context context){
        if (sBookShelf == null){
            sBookShelf = new BookShelf(context);
        }
        return sBookShelf;
    }

    private BookShelf (Context context){
        mContext = context.getApplicationContext();
        mDatabase = new BookBaseHelper(mContext).getWritableDatabase();

    }

    public List<Book> getBooks(){
        List<Book> books = new ArrayList<>();
        BookCursorWrapper cursor = queryBooks(null, null);
        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        }
        finally {
                cursor.close();
            }
        return books;
    }

    public Book getBook(UUID uuid){
        BookCursorWrapper cursor = queryBooks(
                BookTable.cols.UUID + " = ? ",
                new String[]{uuid.toString()}
        );
        try{
            if (cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getBook();
        }
        finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Book book){
        ContentValues values = new ContentValues();
        values.put(BookTable.cols.UUID, book.getUUID().toString());
        values.put(BookTable.cols.TITLE, book.getTitle());
        values.put(BookTable.cols.SUMMARY, book.getSummary());
        values.put(BookTable.cols.DATE, book.getDate().getTime());
        values.put(BookTable.cols.COMPLETED, book.isCompleted() ? 1:0);
        values.put(BookTable.cols.FRIEND, book.getFriend());
//        values.put(BookTable.cols.TIME, book.getTime());

        return values;
    }
    public void addBook(Book b){
        ContentValues values = getContentValues(b);
        mDatabase.insert(BookTable.NAME,null,values);
    }

//    public static void deleteBook (Book b){
//
//        mDatabase.delete(BookTable.NAME, BookTable.cols.UUID + "=?",
//                            new String[]{String.valueOf(b.getUUID())});
//    }
    public void updateBook(Book book){
        String uuidString = book.getUUID().toString();
        ContentValues values = getContentValues(book);

        mDatabase.update(BookTable.NAME, values,
                BookTable.cols.UUID + " = ?",
                new String[] {uuidString});
    }

    private BookCursorWrapper queryBooks(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                BookTable.NAME,
                null,   //columns
                whereClause,
                whereArgs,
                null, //groupby
                null,   //having
                null    //orderby
        );
        return new BookCursorWrapper(cursor);
    }

    public File getPhotoFile(Book book){
        File filesDir = mContext.getFilesDir();
        return  new File(filesDir,book.getPhotoFileName());

    }

}


