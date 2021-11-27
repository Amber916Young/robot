package com.yang.robot.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_ROBOT ="CREATE TABLE robot (\n" +
            "id INTEGER PRIMARY KEY autoincrement,\n" +
            "robot_name VARCHAR ( 30 ) NOT NULL,\n" +
            "robot_img VARCHAR ( 300 ) ,\n" +
            "registerTime VARCHAR(50),\n" +
            "activeTime VARCHAR(50),\n" +
            "lastTime VARCHAR(50),\n" +
            "  state INTEGER ( 2 ) )";
    private Context context;

    public MyDatabaseHelper(@Nullable Context context, @Nullable String name,
                            @Nullable SQLiteDatabase.CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ROBOT);
        Toast.makeText(context,"create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists Book");
    }
}
