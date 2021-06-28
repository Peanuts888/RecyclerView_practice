package com.example.recyclerview_practice;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {

    private RecyclerViewOpenHelper helper = null;
    private List<Columns> myDataset = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.ItemDecoration itemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.my_recycler_view);

        if(helper == null) helper = new RecyclerViewOpenHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            Cursor c = db.rawQuery("SELECT id, body, date FROM MemoTable ORDER BY id", null);
            boolean next = c.moveToFirst();
            while (next) {
                Columns columns = new Columns();
                long id = Long.parseLong(c.getString(0));
                String body = c.getString(1);
                String date = c.getString(2);
                if(body.length() > 9) body = body.substring(0, 10) + "...";
                columns.setBody(body);
                columns.setDate(date);
                columns.setId(id);
                myDataset.add(columns);
                next = c.moveToNext();
            }
        } finally { db.close(); }

        layoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerViewAdapter(myDataset);
        itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(itemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
            new SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {
                    final int fromPos = viewHolder.getAdapterPosition();
                    final int toPos = target.getAdapterPosition();
                    int fPlus = fromPos +1;
                    int tPlus = toPos +1;
                    SQLiteDatabase db = helper.getWritableDatabase();
                    try {
                        Cursor c = db.rawQuery("SELECT body, date FROM MemoTable ORDER BY id", null);
                        c.moveToPosition(fromPos);
                        String bodyF = c.getString(0);
                        String dateF = c.getString(1);
                        if (fromPos < toPos) {
                            c.moveToNext();
                            String body = c.getString(0);
                            String date = c.getString(1);
                            db.execSQL("UPDATE MemoTable " +
                                        "SET body = '"+ body +"', date = '"+ date +"'" +
                                        "WHERE id = '"+ fPlus +"'");
//                            int i = 0;
//                            int repTime = fromPos;
//                            while (repTime < toPos) {
//                                Columns columns = new Columns();
//                                String body = c.getString(0);
//                                String date = c.getString(1);
//                                columns.setBody(body);
//                                columns.setDate(date);
//                                turn.add(columns);
//                                db.execSQL("UPDATE MemoTable " +
//                                        "SET body = '"+ turn.get(i).getBody() +"', date = '"+ turn.get(i).getDate() +"'" +
//                                        "WHERE id = '"+ fPlus +"'");
//                                c.moveToNext();
//                                repTime ++;
//                                fPlus ++;
//                                i ++;
//                            }
                        } else {
                            c.moveToPrevious();
                            String body = c.getString(0);
                            String date = c.getString(1);
                            db.execSQL("UPDATE MemoTable " +
                                    "SET body = '"+ body +"', date = '"+ date +"'" +
                                    "WHERE id = '"+ fPlus +"'");
//                            int i = 0;
//                            int repTime = toPos;
//                            while (repTime < fromPos) {
//                                Columns columns = new Columns();
//                                String body = c.getString(0);
//                                String date = c.getString(1);
//                                columns.setBody(body);
//                                columns.setDate(date);
//                                turn.add(columns);
//                                db.execSQL("UPDATE MemoTable " +
//                                        "SET body = '"+ turn.get(i).getBody() +"', date = '"+ turn.get(i).getDate() +"'" +
//                                        "WHERE id = '"+ fPlus +"'");
//                                c.moveToPrevious();
//                                repTime ++;
//                                fPlus --;
//                                i ++;
//                            }
                        }
                        db.execSQL("UPDATE MemoTable SET body = '"+ bodyF +"', date = '"+ dateF +"'WHERE id = "+ tPlus +"");
                    } finally { db.close(); }
                    myDataset.add(toPos, myDataset.remove(fromPos));
                    mAdapter.notifyItemMoved(fromPos, toPos);
                    return true;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    final int swiPos = viewHolder.getAdapterPosition();
                    int sPlus = swiPos +1;
                    SQLiteDatabase db = helper.getWritableDatabase();
                    try {
                        db.delete("MemoTable", "id = "+ sPlus +"", null);
                        Cursor c = db.rawQuery("SELECT id FROM MemoTable ORDER BY id", null);
                        boolean next = c.moveToPosition(swiPos);
                        int wId = sPlus +1;
                        while (next) {
                            db.execSQL("UPDATE MemoTable SET id = "+ sPlus +" WHERE id = "+ wId +"");
                            sPlus ++;
                            wId ++;
                            next = c.moveToNext();
                        }
                    } finally { db.close(); }
                    myDataset.remove(swiPos);
                    mAdapter.notifyItemRemoved(swiPos);
                }
            }
        );
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void create(View v) {
        Intent intent = new Intent(MainActivity.this, CreateMemoActivity.class);
        intent.putExtra("id", "");
        startActivity(intent);
    }
}
