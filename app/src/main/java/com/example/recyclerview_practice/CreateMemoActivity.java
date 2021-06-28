package com.example.recyclerview_practice;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class CreateMemoActivity extends AppCompatActivity {

    RecyclerViewOpenHelper helper = null;
    boolean newFlag = false;
    String id = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_memo);
        Intent intent = this.getIntent();
        id = intent.getStringExtra("id");
        if(helper == null) helper = new RecyclerViewOpenHelper(CreateMemoActivity.this);
        if(id.equals("")) {
            newFlag = true;
        } else {
            SQLiteDatabase db = helper.getWritableDatabase();
            try {
                Cursor c = db.rawQuery("SELECT body FROM MemoTable WHERE id = '"+ id +"'", null);

                while (c.moveToNext()) {
                    String dispBody = c.getString(0);
                    EditText body = (EditText) findViewById(R.id.body);
                    body.setText(dispBody, TextView.BufferType.NORMAL);
                    c.moveToNext();
                }
            } finally { db.close(); }
        }
    }

    public void register(View v) {
        EditText body = findViewById(R.id.body);
        String bodyStr = body.getText().toString();
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            if(newFlag){
                db.execSQL("INSERT INTO MemoTable(date, body)" +
                        "VALUES(strftime('%Y/%m/%d', date('now', 'localtime')), '"+ bodyStr +"')");
            } else {
                db.execSQL("UPDATE MemoTable " +
                        "SET date = strftime('%Y/%m/%d', date('now', 'localtime')), " +
                        "body ='"+ bodyStr +"'" +
                        "WHERE id ='"+ id +"'");
            }
        } finally { db.close(); }
        Intent intent = new Intent(CreateMemoActivity.this,
                com.example.recyclerview_practice.MainActivity.class);
        startActivity(intent);
    }

    public void back(View v) {
        finish();
    }
}
