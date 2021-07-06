package com.bignerdranch.android.readingjournal;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;


import java.util.UUID;

public class BookActivity extends SingleFragmentActivity {
    private static final String EXTRA_BOOK_ID = "com.bignerdranch.android.readingjournal.book_id";
    @Override
    protected Fragment createFragment() {
        UUID bookId = (UUID) getIntent().getSerializableExtra(EXTRA_BOOK_ID);
        return BookFragment.newInstance(bookId);
    }


    public static Intent newIntent(Context packageContext, UUID bookId){
        Intent intent = new Intent(packageContext, BookActivity.class);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        return intent;
    }


}
