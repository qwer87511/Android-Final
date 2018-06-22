package com.example.coffee.android_final;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AccountDataContentProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.AccountDataContentProvider";
    private static final String DB_FILE = "accountDataBase.db", DB_TABLE = "accountDataBase";
    private static final int URI_ROOT = 0, DB_TABLE_CONTACT = 1;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_TABLE);
    private static final UriMatcher sUriMatcher = new UriMatcher(URI_ROOT);
    static {
        sUriMatcher.addURI(AUTHORITY, DB_TABLE, DB_TABLE_CONTACT);
    }
    private SQLiteDatabase mContactDb;

    public AccountDataContentProvider() {
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mContactDb.delete(DB_TABLE, selection, selectionArgs);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, @Nullable ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        if(sUriMatcher.match(uri) != DB_TABLE_CONTACT) {
            throw new IllegalArgumentException("Unknown URI" + uri);
        }

        long rowId = mContactDb.insert(DB_TABLE, null, values);
        // 把id加進ContentValues
        values.put("_id", (int)rowId);
        Uri insertedRowUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(insertedRowUri, null);
        return insertedRowUri;
    }

    @Override
    public boolean onCreate() {
        DbOpenHelper DbOpenHelper = new DbOpenHelper(getContext(), DB_FILE, null, 1);
        mContactDb = DbOpenHelper.getWritableDatabase();

        // 檢查資料表是否存在，若不存在就建立一個
        Cursor cursor = mContactDb.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DB_TABLE + "'", null);

        if(cursor != null) {
            if (cursor.getCount() == 0) {
                // 建立資料表
                // TEXT 命名須和 ContentValue 一樣
                mContactDb.execSQL("CREATE TABLE " + DB_TABLE + " (" +
                        "_id INTEGER PRIMARY KEY," +
                        "year INTEGER," +
                        "month INTEGER," +
                        "day INTEGER," +
                        "method TEXT," +
                        "item TEXT," +
                        "comment TEXT," +
                        "amount INTEGER);");
            }
            cursor.close();
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if(sUriMatcher.match(uri) != DB_TABLE_CONTACT) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor cursor = mContactDb.query(true, DB_TABLE, projection, selection, null, null, null, null, null);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return mContactDb.update(DB_TABLE, values, selection, selectionArgs);
    }

    private class DbOpenHelper extends SQLiteOpenHelper {

        public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
