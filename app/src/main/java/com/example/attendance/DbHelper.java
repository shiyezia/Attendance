package com.example.attendance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "database.db";

    public static final int DB_VERSION = 3;

    public static final String TABLE_STUDENT = "students";
    public static final String TABLE_ATTENDANCE="attendance";
    public static final String TABLE_CLASS="course";
    public static final String TABLE_elective="elective";

    private static final String STUDENTS_CREATE_TABLE_SQL = "create table " + TABLE_STUDENT + "("
            + "id integer primary key autoincrement,"
            + "name varchar(20) not null,"
            + "age varchar(11) not null,"
            + "Sno integer  not null,"
            + "cls varchar(20) not null,"
            + "passward varchar(16) not null"
            + ");";

    public static final String CREATE_TABLE_ATTEANDANCE= "create table " + TABLE_ATTENDANCE + "("
            + "id integer primary key autoincrement,"
            + "Sno integer not null,"
            + "course varchar(20) not null,"
            + "datetime integer not null"
            + ");";

    public static final String CREATE_TABLE_CLASS= "create table " + TABLE_CLASS + "("
            + "id integer primary key autoincrement,"
            + "course varchar(20) not null,"
            + "teacher varchar(20) not null,"
            + "address varchar(20) not null"
            + ");";

    public static final String CREATE_TABLE_ELECTIVE= "create table " + TABLE_elective + "("
            + "id integer primary key autoincrement,"
            + "Sno integer not null,"
            + "course varchar(20) not null"
            + ");";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STUDENTS_CREATE_TABLE_SQL);
        db.execSQL(CREATE_TABLE_ATTEANDANCE);
        db.execSQL(CREATE_TABLE_CLASS);
        db.execSQL(CREATE_TABLE_ELECTIVE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_elective);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        onCreate(db);
    }


}
