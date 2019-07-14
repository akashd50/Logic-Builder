package com.akashd50.lb.ui;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.akashd50.lb.R;
import com.akashd50.lb.persistense.DBContract;
import com.akashd50.lb.persistense.DBHelper;

public class MainActivity extends AppCompatActivity {
    private Button play, settings;
    private View.OnClickListener listener;
    private DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = findViewById(R.id.play_button_main);
        settings = findViewById(R.id.settings_button_main);

        dbHelper = new DBHelper(this);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final SQLiteDatabase readableDB = dbHelper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.DBEntry.COLUMN_NAME_TITLE, "qwerty");
        contentValues.put(DBContract.DBEntry.COLUMN_NAME_SUBTITLE, "qwerty2");
        long newRowId = database.insert(DBContract.DBEntry.TABLE_NAME, null, contentValues);


        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.play_button_main:
                        startActivity(new Intent(MainActivity.this, MainGameActivity.class));
                        break;
                    case R.id.settings_button_main:
                        String[] projection = {BaseColumns._ID, DBContract.DBEntry.COLUMN_NAME_TITLE, DBContract.DBEntry.COLUMN_NAME_SUBTITLE};
                        String selection = DBContract.DBEntry.COLUMN_NAME_TITLE + "=?";
                        String[] selectionArgs = {"qwerty"};
                        String sortOrder = DBContract.DBEntry.COLUMN_NAME_SUBTITLE + " DESC";

                        Cursor cursor = readableDB.query(DBContract.DBEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                        cursor.moveToNext();
                        String value = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.DBEntry.COLUMN_NAME_SUBTITLE));
                        cursor.close();
                        Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        play.setOnClickListener(listener);
        settings.setOnClickListener(listener);
    }




}
