package com.example.recyclerview_practice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecyclerViewOpenHelper extends SQLiteOpenHelper {

    private static final String DataBase = "DB";
    private static final int VERSION = 1;

    public RecyclerViewOpenHelper(Context context) {
        super(context, DataBase, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MemoTable (" +
                "id INTEGER PRIMARY KEY, " +
                "body TEXT, " +
                "date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS MemoTable");// MEMO_TABLEが存在している場合のみテーブルを削除する。
        onCreate(db);// IF EXISTSがない場合、テーブルが存在していないとエラーになる。
    }
}
