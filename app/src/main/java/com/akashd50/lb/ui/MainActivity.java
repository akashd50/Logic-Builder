package com.akashd50.lb.ui;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.BaseColumns;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.akashd50.lb.R;
import com.akashd50.lb.objects.LogicBoard;
import com.akashd50.lb.objects.LogicObject;
import com.akashd50.lb.persistense.DBContract;
import com.akashd50.lb.persistense.DBHelper;
import com.akashd50.lb.persistense.SQLPersistenceBoard;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button play, settings;
    private View.OnClickListener listener;
    private Dialog onPlayClickedDialog, newBoardDialog;
    private SQLPersistenceBoard sqlPersistenceBoard;
    private ArrayAdapter<LogicBoard> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper dbHelper = new DBHelper(this);
        sqlPersistenceBoard = new SQLPersistenceBoard(dbHelper);

        play = findViewById(R.id.play_button_main);
        settings = findViewById(R.id.settings_button_main);



        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.play_button_main:
                        onPlayClickedDialog.show();
                        break;
                    case R.id.settings_button_main:

                        break;
                    case R.id.new_board_button:
                        /*Intent i = new Intent(MainActivity.this, MainGameActivity.class);
                        i.putExtra("bid",-1);
                        startActivity(i);*/
                        onPlayClickedDialog.dismiss();
                        newBoardDialog.show();
                        break;
                }
            }
        };

        initializeonNewBoardClicked();
        initializeonPlayButtonClicked();

        play.setOnClickListener(listener);
        settings.setOnClickListener(listener);
    }

    public void initializeonNewBoardClicked(){
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.new_board, null);

        final EditText nameBox = v.findViewById(R.id.new_board_name);
        final EditText xBox = v.findViewById(R.id.board_len);
        final EditText yBox = v.findViewById(R.id.board_hi);

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setView(v);
        ab.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(MainActivity.this, MainGameActivity.class);
                i.putExtra("bid",-1);
                i.putExtra("name",nameBox.getText().toString());
                i.putExtra("bx",Integer.parseInt(xBox.getText().toString()));
                i.putExtra("by",Integer.parseInt(yBox.getText().toString()));
                startActivity(i);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        ab.setCancelable(false);
        newBoardDialog = ab.create();
        newBoardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void initializeonPlayButtonClicked(){
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.play_selection_dialog, null);

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setView(v);
        onPlayClickedDialog = ab.create();
        onPlayClickedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ArrayList<LogicBoard> temporaryList = sqlPersistenceBoard.getAllBoards();
        final ListView listView = v.findViewById(R.id.existing_boards_list);
        adapter = new ArrayAdapter<>(this,
                R.layout.list_view_item,R.id.list_view_item_text, temporaryList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogicBoard logicBoard = adapter.getItem(position);
                Intent i = new Intent(MainActivity.this, MainGameActivity.class);
                i.putExtra("bid",logicBoard.getID());
                startActivity(i);
            }
        });

        final Button newBoard = v.findViewById(R.id.new_board_button);
        newBoard.setOnClickListener(listener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.clear();
        adapter.addAll(sqlPersistenceBoard.getAllBoards());
        adapter.notifyDataSetChanged();
    }
}
