package com.barchello.cliente.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.barchello.cliente.database.SQLiteHelper;

public class UsuarioDAO {
    private SQLiteHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    // Método para insertar un usuario (lo usaremos para crear el usuario de prueba)
    public boolean insertarUsuario(String usuario, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_USUARIO, usuario);
        values.put(SQLiteHelper.COLUMN_PASSWORD, password);

        long result = db.insert(SQLiteHelper.TABLE_USUARIOS, null, values);
        db.close();
        return result != -1; // Devuelve true si se insertó correctamente
    }

    // Método para validar el usuario (La consulta que pide la guía)
    public boolean validarUsuario(String usuario, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Consulta parametrizada para evitar inyecciones SQL
        String query = "SELECT * FROM " + SQLiteHelper.TABLE_USUARIOS +
                " WHERE " + SQLiteHelper.COLUMN_USUARIO + " = ? AND " +
                SQLiteHelper.COLUMN_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{usuario, password});
        boolean isValid = cursor.getCount() > 0; // Si hay al menos un registro, es válido
        cursor.close();
        db.close();
        return isValid;
    }
}