package com.bignerdranch.android.readingjournal.database;

public class BookDbSchema {
    public static final class BookTable{
        public static final String NAME = "books";

        public static final class cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String SUMMARY = "summary";
            public static final String DATE= "date";
            public static final String COMPLETED = "completed";
            public static final String FRIEND = "friend";
        }
    }
}
