package com.bignerdranch.android.readingjournal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class BookPagerActivity extends AppCompatActivity{
    private static final String EXTRA_BOOK_ID =
            "com.bignerdranch.android.readingjournal.book_id";
    private ViewPager mViewPager;
    private List <Book> mBooks;

    public static Intent newIntent(Context packageContext, UUID bookId){
        Intent intent = new Intent(packageContext, BookPagerActivity.class);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_pager);

        mViewPager= (ViewPager) findViewById(R.id.book_view_pager);
        mBooks = BookShelf.get(this).getBooks();
        FragmentManager fragmentManager =getSupportFragmentManager();
        mViewPager.setAdapter((new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Book book = mBooks.get(position);
                return BookFragment.newInstance(book.getUUID());
            }

            @Override
            public int getCount() {
                return mBooks.size();
            }
        }));
        UUID bookId = (UUID) getIntent().getSerializableExtra(EXTRA_BOOK_ID);

        for (int i=0 ; i<mBooks.size(); i++){
            if (mBooks.get(i).getUUID().equals(bookId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }
}
