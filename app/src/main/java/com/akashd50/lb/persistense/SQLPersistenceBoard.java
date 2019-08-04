package com.akashd50.lb.persistense;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.akashd50.lb.objects.BoardData;
import com.akashd50.lb.objects.LogicBoard;
import com.akashd50.lb.objects.SimpleVector;

import java.util.ArrayList;

public class SQLPersistenceBoard {
    private SQLiteDatabase writableDB, readableDB;
    public SQLPersistenceBoard(DBHelper dbHelper){
        writableDB = dbHelper.getWritableDatabase();
        readableDB = dbHelper.getReadableDatabase();
    }

    public void insert(LogicBoard b){
       // writableDB.delete(DBContract.DBEntry.BOARD_TABLE_NAME, null, null);

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.DBEntry.C_BOARD_ID, b.getID());
        contentValues.put(DBContract.DBEntry.BOARD_NAME, b.getName());
        contentValues.put(DBContract.DBEntry.DIMENSIONS_X, b.getDimensions().x);
        contentValues.put(DBContract.DBEntry.DIMENSIONS_Y, b.getDimensions().y);
        writableDB.insert(DBContract.DBEntry.BOARD_TABLE_NAME, null, contentValues);
    }

    public boolean contains(LogicBoard b){
        String[] projection = {BaseColumns._ID,
                DBContract.DBEntry.C_BOARD_ID,
                DBContract.DBEntry.BOARD_NAME};
        String selection = DBContract.DBEntry.C_BOARD_ID+" = ?";
        String[] selectionArgs = {b.getID()+""};
        Cursor cursor = readableDB.query(DBContract.DBEntry.BOARD_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if(cursor.moveToNext()){
            cursor.close();
            return true;
        }else return false;
    }

    public LogicBoard getBoard(int id){
        String[] projection = {BaseColumns._ID,
                DBContract.DBEntry.C_BOARD_ID,
                DBContract.DBEntry.BOARD_NAME,
                DBContract.DBEntry.DIMENSIONS_X,
                DBContract.DBEntry.DIMENSIONS_Y};

        String selection = DBContract.DBEntry.C_BOARD_ID+" = ?";
        String[] selectionArgs = {id+""};
        Cursor cursor = readableDB.query(DBContract.DBEntry.BOARD_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if(cursor.moveToNext()) {
            int x = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.DBEntry.DIMENSIONS_X));
            int y = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.DBEntry.DIMENSIONS_Y));
            String boardName = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.DBEntry.BOARD_NAME));
            int bID = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.DBEntry.C_BOARD_ID));
            LogicBoard l = new LogicBoard(new SimpleVector(x,y,0), bID);
            l.setName(boardName);
            cursor.close();
            return l;
        }
        return null;
    }

    public ArrayList<LogicBoard> getAllBoards(){
        ArrayList<LogicBoard> boards = new ArrayList<>();
        String[] projection = {BaseColumns._ID,
                DBContract.DBEntry.C_BOARD_ID,
                DBContract.DBEntry.BOARD_NAME,
                DBContract.DBEntry.DIMENSIONS_X,
                DBContract.DBEntry.DIMENSIONS_Y};

        Cursor cursor = readableDB.query(DBContract.DBEntry.BOARD_TABLE_NAME, projection, null, null, null, null, null);
        while(cursor.moveToNext()) {
            int x = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.DBEntry.DIMENSIONS_X));
            int y = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.DBEntry.DIMENSIONS_Y));
            String boardName = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.DBEntry.BOARD_NAME));
            int bID = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.DBEntry.C_BOARD_ID));
            LogicBoard l = new LogicBoard(new SimpleVector(x,y,0), bID);
            l.setName(boardName);

            boards.add(l);
        }
        cursor.close();
        return boards;
    }

    public int getNextBoardID(){
        int id = -1;
        String[] projection = {BaseColumns._ID,
                DBContract.DBEntry.APP_VARS_BOARD_ID};

        Cursor cursor = readableDB.query(DBContract.DBEntry.APP_VARS_TABLE, projection, null, null, null, null, null);
        if(cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.DBEntry.APP_VARS_BOARD_ID));
            cursor.close();

            writableDB.delete(DBContract.DBEntry.APP_VARS_TABLE, null, null);
            int newID = id+1;

            ContentValues contentValues = new ContentValues();
            contentValues.put(DBContract.DBEntry.APP_VARS_BOARD_ID, newID);
            writableDB.insert(DBContract.DBEntry.APP_VARS_TABLE, null, contentValues);
        }
        return id;
    }

    public boolean isBoardIDInitialized(){
        String[] projection = {BaseColumns._ID,
                DBContract.DBEntry.APP_VARS_BOARD_ID};
        Cursor cursor = readableDB.query(DBContract.DBEntry.APP_VARS_TABLE, projection, null, null, null, null, null);
        if(cursor.moveToNext()){
            cursor.close();
            return true;
        }else return false;
    }
}
