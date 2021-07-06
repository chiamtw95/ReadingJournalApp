package com.bignerdranch.android.readingjournal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bignerdranch.android.readingjournal.database.BookBaseHelper;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class BookFragment extends Fragment {
    private Book mBook;

    private File mPhotoFile;
    private EditText mTitleField, mSummaryField;
    private static final String ARG_BOOK_ID = "book_id", DIALOG_DATE = "DialogDate" , DIALOG_TIME= "DialogTime" ;
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_TIME = 3;
    private Button mDateButton, mTimeButton, mSendBookDetailsButton, mRecommendFriendButton , mDeleteButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private CheckBox mCheckbox;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID bookId = (UUID)getArguments().getSerializable(ARG_BOOK_ID);
        mBook = BookShelf.get(getActivity()).getBook(bookId);
        mPhotoFile = BookShelf.get(getActivity()).getPhotoFile(mBook);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == REQUEST_DATE || requestCode == REQUEST_TIME){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mBook.setDate(date);
            updateDate();
        }

        else if (requestCode == REQUEST_CONTACT && data != null){
            Uri contactURI = data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
        Cursor c = getActivity().getContentResolver().query(contactURI,queryFields,
                null,null,null);

        try {
            if (c.getCount() == 0)
                return;
            c.moveToFirst();
            String friend = c.getString(0);
            mBook.setFriend(friend);
            mRecommendFriendButton.setText(friend);
        }
        finally {
            c.close();
        }

        }
        else if (requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.readingjournal.fileprovider",mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }

    }

    private void updateDate() {
        mDateButton.setText(mBook.getDate().toString());
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book, container,false);
        final BookBaseHelper bookBaseHelper = new BookBaseHelper(getContext());
        mDateButton = (Button) v.findViewById(R.id.book_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mBook.getDate());
                dialog.setTargetFragment(BookFragment.this, REQUEST_DATE);
                dialog.show(manager,DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.book_time);
        updateDate();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FragmentManager fm = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mBook.getDate());
                dialog.setTargetFragment(BookFragment.this, REQUEST_TIME);
               dialog.show(fm, DIALOG_TIME);
            }
        });


        mCheckbox = (CheckBox) v.findViewById(R.id.book_completed);
        mCheckbox.setChecked(mBook.isCompleted());
        mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBook.setCompleted(isChecked);
            }
        });

        mTitleField = (EditText) v.findViewById(R.id.book_title);
        mTitleField.setText(mBook.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBook.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSummaryField = (EditText) v.findViewById(R.id.book_summary);
        mSummaryField.setText(mBook.getSummary());
        mSummaryField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBook.setSummary(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSendBookDetailsButton = (Button) v.findViewById(R.id.book_detailSummary_button);
        mSendBookDetailsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getBookDetailSummary());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.detailSummary_send));
                i = Intent.createChooser(i, getString(R.string.detailSummary_send));
                startActivity(i);
            }});

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mRecommendFriendButton = (Button) v.findViewById(R.id.book_friend);
        mRecommendFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null){
            mRecommendFriendButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.book_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.readingjournal.fileprovider"
                            ,mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.book_photo);
        updatePhotoView();

        mDeleteButton = (Button) v.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Delete Book");
                alert.setMessage("Are you sure you want to delete this book?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        bookBaseHelper.deleteBook(mBook.getUUID());
                        getActivity().onBackPressed();
                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        BookShelf.get(getActivity()).updateBook(mBook);
    }

    public static BookFragment newInstance(UUID bookId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_ID, bookId);
        BookFragment fragment = new BookFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String getBookDetailSummary(){
        String completedString = null;
        String dateFormat = "yyyy-MM-dd, HH:mm";
        String dateString = android.text.format.DateFormat.format(dateFormat,mBook.getDate()).toString();

        if (mBook.isCompleted()){
            completedString = getString(R.string.detailSummary_completed, dateString);
        }
        else{
            completedString = getString(R.string.detailSummary_notCompleted);
        }
        String friend = mBook.getFriend();
        if (friend == null){
            friend = getString(R.string.detailSummary_notRecommended);
        }
        else{
            friend = getString(R.string.detailSummary_recommended, friend);
        }

        String bookSummary = mBook.getSummary();
        if (bookSummary.isEmpty()){
            bookSummary = getString(R.string.detailSummary_noSummary);
        }
        String detailSummary = getString(R.string.detailSummary,
                                            mBook.getTitle(),
                                            bookSummary,
                                            completedString,
                                            friend);
        return detailSummary;
    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }
        else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

}
