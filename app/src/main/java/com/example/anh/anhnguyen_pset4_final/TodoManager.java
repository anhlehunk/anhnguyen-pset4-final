package com.example.anh.anhnguyen_pset4_final;

/**
 * Created by Anh on 5-12-2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.PARENT;

public class TodoManager extends SQLiteOpenHelper {
    //singleton instance
    // making it possible to access from every activity
    private static TodoManager instanceSingleton;

    public static synchronized TodoManager getInstance(Context context) {
        if (instanceSingleton == null) {
            instanceSingleton = new TodoManager(context.getApplicationContext());
        }
        return instanceSingleton;
    }

    // items
    public static final String CHECKER = "checker";
    public static final String PARENT = "parent";
    public static final String _ID = "_id";
    public static final String TASK = "task";


    // database
    static final String NAME = "ToDoLists.db";
    static final int VERSION = 2;

    //strings
    public static final String LIST = "LIST";
    public static final String ITEM = "ITEM";

    // list content
    public static final String CONTENT = "content";


    // table
    private static final String CREATE_LIST = "create table " + LIST + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CONTENT + " text not null);";
    private static final String CREATE_TODO = "create table " + ITEM + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CHECKER + " integer not null, " + TASK + " text not null, " + PARENT + " integer default 0);";


    public TodoManager(Context context) {
        super(context, NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LIST);
        db.execSQL(CREATE_TODO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LIST);
        db.execSQL("DROP TABLE IF EXISTS " + ITEM);
        onCreate(db);
    }



    public TodoItem makeItem (int checker, String task, long parent){
        // store content
        ContentValues databaseValues = new ContentValues();
        databaseValues.put(CHECKER, checker);
        databaseValues.put(PARENT, parent);
        databaseValues.put(TASK, task);

        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(ITEM, null, databaseValues);

        TodoItem item = new TodoItem(task, checker, parent);
        return item;
    }


    //  cursor functions
    public Cursor getListRow (long _id){
        String[] rowData = new String[] {_ID, CONTENT};
        String dataCompare = _ID + "=" + _id;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(true, LIST, rowData, dataCompare,
                null, null, null, null, null);
        if (cursor != null){cursor.moveToFirst();}
        return cursor;
    }

    // the row with data of items
    public Cursor getItemRow (long _id){
        String[] dataItem = new String[]{_ID, CHECKER, TASK};
        String dataCompare = _ID + "=" + _id;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(true, ITEM, dataItem, dataCompare,
                null, null, null, null, null);

        if (cursor != null){cursor.moveToFirst();}
        return cursor;
    }
    // get items out of database
    public Cursor getItems (long parent_id){
        List<TodoItem> oversight = new ArrayList<>();
        String select = "SELECT * FROM " + ITEM;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(select, null);

        if (cursor.moveToFirst()){
            do {
                TodoItem itemDB = new TodoItem();
                itemDB.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
                itemDB.setChecker(cursor.getInt(cursor.getColumnIndex(CHECKER)));
                itemDB.setParent(cursor.getLong(cursor.getColumnIndex(PARENT)));
                itemDB.setTask(cursor.getString(cursor.getColumnIndex(TASK)));
                oversight.add(itemDB);
            }while (cursor.moveToNext());
        }

        String[] data = new String[]{_ID, CHECKER, TASK};
        String dataCompare = PARENT + "=" + parent_id;
        Cursor newCursor = database.query(true, ITEM, data, dataCompare,
                null, null, null, null, null);

        if (newCursor != null) {newCursor.moveToFirst();}
        return newCursor;
    }


    //delete stuff
    public void delete_all (long _id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(ITEM, PARENT + "=" + _id, null);
    }
    public void list_delete (long _id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(LIST, _ID + "=" + _id, null);
    }

    public void item_delete (long _id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(ITEM, _ID + "=" + _id, null);
    }

    public void close(){
        SQLiteDatabase database = this.getReadableDatabase();
        if (database.isOpen() && database != null){database.close();}
    }

    // update database
    public boolean update(long _id, int checker, String task) {
        ContentValues valuesDB = new ContentValues();
        valuesDB.put(CHECKER, checker);
        valuesDB.put(TASK, task);

        // change new stuff in database
        SQLiteDatabase database = this.getWritableDatabase();
        String dataCompare = _ID + "=" + _id;
        return database.update(ITEM, valuesDB, dataCompare, null) != 0;
    }

}