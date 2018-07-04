package com.example.chefk.maddemo18.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalDataItemCRUDOperations implements IDataItemCRUDOperations {

    private SQLiteDatabase db;

    private static final String TABLE_DATAITEMS = "DATAITEMS";
    private static final String[] ALL_COLUMNS = new String[]{"ID", "NAME", "DESCRIPTION", "EXPIRY", "DONE", "FAVORITE", "CONTACTS"};

    private static final String CREATION_QUERY = "CREATE TABLE DATAITEMS (ID INTEGER PRIMARY KEY,NAME TEXT,DESCRIPTION TEXT,EXPIRY INTEGER DEFAULT 0,DONE INTEGER,FAVORITE INTEGER,CONTACTS TEXT DEFAULT '0')";

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
        values.put("DESCRIPTION",item.getDescription());
        values.put("EXPIRY",item.getExpiry());
        values.put("DONE",item.isDone() ? 1 : 0);
        values.put("FAVORITE",item.isFavorite() ? 1 : 0);
        if (item.getContacts() != null) values.put("CONTACTS",android.text.TextUtils.join(",",item.getContacts()));

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
        item.setDescription(cursor.getString(cursor.getColumnIndex("DESCRIPTION")));
        item.setExpiry(cursor.getLong(cursor.getColumnIndex("EXPIRY")));
        item.setDone(cursor.getInt(cursor.getColumnIndex("DONE")) == 1);
        item.setFavorite(cursor.getInt(cursor.getColumnIndex("FAVORITE")) == 1);
        item.setContacts(new ArrayList<String>(Arrays.asList(cursor.getString(cursor.getColumnIndex("CONTACTS")).split("\\s*,\\s*"))));
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
        ContentValues values = new ContentValues();
        values.put("NAME",item.getName());
        values.put("DESCRIPTION",item.getDescription());
        values.put("EXPIRY",item.getExpiry());
        values.put("DONE",item.isDone());
        values.put("FAVORITE",item.isFavorite());
        if (item.getContacts() != null) values.put("CONTACTS",android.text.TextUtils.join(",",item.getContacts()));
        db.update(TABLE_DATAITEMS,values,"id=?", new String[]{String.valueOf(id)});
        return true;
    }

    @Override
    public boolean deleteItem(long id) {
        db.delete(TABLE_DATAITEMS,"id=?", new String[]{String.valueOf(id)});
        return true;
    }

    @Override
    public boolean deleteAllTodos() {
        db.execSQL("delete from "+ TABLE_DATAITEMS);
        return true;
    }

    @Override
    public boolean authenticateUser(User user) {
        return true;
    }
}
