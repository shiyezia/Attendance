package com.example.attendance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DAO {
    private SQLiteDatabase db;
    public DbHelper DbHelper;

    public DAO(Context context) {
        if ( db == null || !db.isOpen() ){
            DbHelper= new DbHelper(context);
            db = DbHelper.getWritableDatabase();
        }
    }

    public void close() {
        db.close();
    }
    public Cursor login(String user){
        Cursor cursor = db.query(DbHelper.TABLE_STUDENT, null,
                "name =?", new String[]{user},
                null, null, null, null);
        return cursor;
    }
    public student insertS(student s){
        ContentValues cv = new ContentValues();
        cv.put("name",s.getName());
        cv.put("age",s.getAge());
        cv.put("Sno",s.getSno());
        cv.put("cls",s.getCls());
        cv.put("passward",s.getPassward());
        db.insert("students",null,cv);
        return s;
    }
}
