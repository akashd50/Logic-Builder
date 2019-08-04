package com.akashd50.lb.persistense;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.akashd50.lb.objects.BoardData;
import com.akashd50.lb.objects.Gate;
import com.akashd50.lb.objects.IO_Device;
import com.akashd50.lb.objects.LogicObject;
import com.akashd50.lb.objects.Wire;

import java.util.ArrayList;

public class SQLPersistenceBoardData {
    private SQLiteDatabase writableDB, readableDB;
    public SQLPersistenceBoardData(DBHelper dbHelper){
        writableDB = dbHelper.getWritableDatabase();
        readableDB = dbHelper.getReadableDatabase();
    }

    public void insert(ArrayList<BoardData> boardData, int boardID){
        String selection = DBContract.DBEntry.C_BOARD_ID + " = ?";
        String[] selectionArgs = {boardID+""};
        writableDB.delete(DBContract.DBEntry.TABLE_NAME, selection, selectionArgs);

        for(BoardData data: boardData){
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBContract.DBEntry.C_BOARD_ID, boardID);
            contentValues.put(DBContract.DBEntry.C_INDEX_X, data.x);
            contentValues.put(DBContract.DBEntry.C_INDEX_Y, data.y);
            contentValues.put(DBContract.DBEntry.C_TYPE, data.logicObject.getType());
            contentValues.put(DBContract.DBEntry.C_STYLE, data.logicObject.getStyle());
            writableDB.insert(DBContract.DBEntry.TABLE_NAME, null, contentValues);
        }
    }

    public ArrayList<BoardData> getAll(int boardID){
        ArrayList<BoardData> boardData = new ArrayList<>();
        String[] projection = {BaseColumns._ID,
                DBContract.DBEntry.C_INDEX_X,
                DBContract.DBEntry.C_INDEX_Y,
                DBContract.DBEntry.C_TYPE,
                DBContract.DBEntry.C_STYLE};

        String selection = DBContract.DBEntry.C_BOARD_ID + " = ?";
        String[] selectionArgs = {boardID+""};
        Cursor cursor = readableDB.query(DBContract.DBEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        while(cursor.moveToNext()){
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.DBEntry.C_TYPE));
            int style = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.DBEntry.C_STYLE));
            int x = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.DBEntry.C_INDEX_X));
            int y = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.DBEntry.C_INDEX_Y));
            if(type == LogicObject.GATE){
                Gate g = new Gate(style);
                boardData.add(new BoardData(g, x, y));
            }else if(type == LogicObject.WIRE){
                Wire w = new Wire(style);
                boardData.add(new BoardData(w, x, y));
            }else if(type == LogicObject.IO_DEVICE){
                IO_Device io = new IO_Device(style);
                boardData.add(new BoardData(io, x, y));
            }
        }
        cursor.close();
        return boardData;
    }


}
