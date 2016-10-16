package com.example.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqliteUtil{
    //check if the searching-table exist
    public boolean isTableExists(SQLiteDatabase db, String tableName){
        String sql = "SELECT COUNT(*) as c from sqlite_master WHERE TYPE = 'table' and NAME = '" + tableName + "'";
        Cursor cr = db.rawQuery(sql, null);

        if(cr.moveToNext()){
            int count = cr.getInt(0);
            if(count > 0){
                return true;
            }
            else{
                return false;
            }
        }
        return false;
    }
}
