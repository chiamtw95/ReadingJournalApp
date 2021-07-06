package com.bignerdranch.android.readingjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.zip.Inflater;

public class BookListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mBookRecyclerView;
    private BookAdapter mAdapter;
    private TextView mTitleTextView, mDateTextView;
    private boolean mSubtitleVisible;

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_book:
                Book book = new Book();
                BookShelf.get(getActivity()).addBook(book);
                Intent intent = BookPagerActivity.newIntent(getActivity(), book.getUUID());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        BookShelf bookShelf = BookShelf.get(getActivity());
        int bookCount = bookShelf.getBooks().size();
        String subtitle = getString(R.string.subtitle_format, bookCount);

        if (!mSubtitleVisible)
            subtitle = null;

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_book_list,menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible)
            subtitleItem.setTitle(R.string.hide_subtitle);
        else
            subtitleItem.setTitle(R.string.show_subtitle);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container ,false);
        mBookRecyclerView = (RecyclerView) view.findViewById(R.id.book_recycler_view);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();
        return view;
    }

    private void updateUI() {
        BookShelf bookShelf = BookShelf.get(getActivity());
        List<Book> books = bookShelf.getBooks();

        if (mAdapter==null){
            mAdapter = new BookAdapter(books);
            mBookRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.setBooks(books);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Book mBook;
        private TextView mTitleTextView, mDateTextView;
        private ImageView mCompletedImageView;


        public BookHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_book,parent,false));
            mTitleTextView = (TextView) itemView.findViewById(R.id.book_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.book_completion_date);
            mCompletedImageView = (ImageView) itemView.findViewById(R.id.book_completed);
            itemView.setOnClickListener(this);

        }
        public void bind(Book book){
            mBook = book;
            mTitleTextView.setText(mBook.getTitle());
            mDateTextView.setText(mBook.getDate().toString());
            mCompletedImageView.setVisibility(book.isCompleted() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            Intent intent = BookPagerActivity.newIntent(getActivity(), mBook.getUUID());
            startActivity(intent);
        }
    }


    private class BookAdapter extends RecyclerView.Adapter<BookHolder>{
        private List <Book> mBooks;

        public BookAdapter(List<Book> books){
            mBooks = books;
        }

        public void setBooks(List<Book> books){
            mBooks = books;
        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new BookHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(BookHolder holder, int position) {
            Book book = mBooks.get(position);
            holder.bind(book);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }
    }

}
