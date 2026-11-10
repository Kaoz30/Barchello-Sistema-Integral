package com.barchello.cliente.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "barchello_local.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USUARIO = "usuario";
    public static final String COLUMN_PASSWORD = "password";

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creamos la tabla de usuarios
        String createTable = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USUARIO + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createTable);

        // Insertamos los usuarios por defecto para que puedas seguir probando tu app
        db.execSQL("INSERT INTO " + TABLE_USUARIOS + " (" + COLUMN_USUARIO + ", " + COLUMN_PASSWORD + ") VALUES ('admin', '1234')");
        db.execSQL("INSERT INTO " + TABLE_USUARIOS + " (" + COLUMN_USUARIO + ", " + COLUMN_PASSWORD + ") VALUES ('juanperez', '1234')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }
}
