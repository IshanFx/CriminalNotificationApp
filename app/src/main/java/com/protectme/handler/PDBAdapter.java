package com.protectme.handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by IshanFx on 1/15/2016.
 */
public class PDBAdapter {
    private static final String DATABASE_NAME="protectmeapp.db";
    private  static final String DATABASE_TABLE = "maintable";
    private static final int DATABASE_VIRSION=1;

    public static final String KEY_ID="_id";
    public static final String KEY_NAME="name";
    public static final int NAME_COLUMN=1;

    private static final String DATABASE_CREATE =
            "create table "+DATABASE_TABLE +"("+KEY_ID+" INTEGER PRIMARY KEY autoincrement, "+
                    KEY_NAME+" text not null);";

    private SQLiteDatabase db;
    private final Context context;
    private myDbHelper dbHelper;

    public PDBAdapter(Context _context){
        context = _context;

        dbHelper = new myDbHelper(context,DATABASE_NAME,null,DATABASE_VIRSION);

    }

    public void open() {
        try {
            db = dbHelper.getWritableDatabase();
        }
        catch (Exception ex){
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close(){
        db.close();
    }

    public long insertEntry(ContentValues _values){
        return db.insert(DATABASE_TABLE,null,_values);
    }

    public boolean removeEntry(long _rowIndex){
        return db.delete(DATABASE_TABLE,KEY_ID+"="+_rowIndex,null)>0;
    }

    public Cursor getAllEntries(){

        return db.query(DATABASE_TABLE,new String[]{KEY_ID,KEY_NAME},null,null,null,null,null);
    }

    public int updateEntry(long _rowIndex,ContentValues _values){

        return db.update(DATABASE_TABLE,_values,KEY_ID+"="+_rowIndex,null);
    }

    private static class myDbHelper extends SQLiteOpenHelper {
        public myDbHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
            super(context,name,factory,version);
        }
        public void onCreate(SQLiteDatabase _db){
            _db.execSQL(DATABASE_CREATE);
        }
        public void onUpgrade(SQLiteDatabase _db,int _oldVersion,int _newVersion){
            Log.w("TaskDBAdapter", "Upgrade from version " + _oldVersion + " to " + _newVersion + ",which will distroy all old data");
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(_db);
        }
    }


}
