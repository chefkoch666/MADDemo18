package com.example.chefk.maddemo18.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class LocalDataItemCRUDOperations implements IDataItemCRUDOperations {

    private SQLiteDatabase db;

    private static final String TABLE_DATAITEMS = "DATAITEMS";
    private static final String[] ALL_COLUMNS = new String[]{"ID", "NAME", "EXPIRY", "DONE"};

    private static final String CREATION_QUERY = "CREATE TABLE DATAITEMS (ID INTEGER PRIMARY KEY,NAME TEXT,EXPIRY INTEGER,DONE INTEGER)";

    public LocalDataItemCRUDOperations(Context ctx) {
        this.db = ctx.openOrCreateDatabase("mysqlitedb.sqlite",Context.MODE_PRIVATE,null);
        if (db.getVersion() == 0) {
            db.setVersion(1);
            db.execSQL(CREATION_QUERY);
        }
    }

    @Override
    public long createItem(DataItem item) {

        ContentValues values = new ContentValues();
        values.put("NAME",item.getName());
        values.put("EXPIRY",item.getExpiry());
        values.put("DONE",item.isDone() ? 1 : 0);

        long id = db.insert(TABLE_DATAITEMS,null,values);
        item.setId(id);

        return id;
    }

    @Override
    public List<DataItem> readAllItems() {

        List<DataItem> items = new ArrayList<>();

        Cursor cursor = db.query(TABLE_DATAITEMS,ALL_COLUMNS,null,null,null,null,"ID");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                items.add(createDataItemFromCursor(cursor));
            }
        }

        return items;
    }

    public DataItem createDataItemFromCursor(Cursor cursor) {
        DataItem item = new DataItem();
        item.setId(cursor.getLong(cursor.getColumnIndex("ID")));
        item.setName(cursor.getString(cursor.getColumnIndex("NAME")));
        item.setExpiry(cursor.getLong(cursor.getColumnIndex("EXPIRY")));
        item.setDone(cursor.getInt(cursor.getColumnIndex("DONE")) == 1);

        return item;
    }

    @Override
    public DataItem readItem(long id) {

        Cursor cursor = db.query(TABLE_DATAITEMS,ALL_COLUMNS,"ID=?",new String[]{String.valueOf(id)},null,null,null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return createDataItemFromCursor(cursor);
        }
        return null;
    }

    @Override
    public boolean updateItem(long id, DataItem item) {
        return false;
    }

    @Override
    public boolean deleteItem(long id) {
        return false;
    }
}
