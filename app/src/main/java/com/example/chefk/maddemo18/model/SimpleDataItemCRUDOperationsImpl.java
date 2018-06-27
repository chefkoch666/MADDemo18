package com.example.chefk.maddemo18.model;

import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    private static final DataItem[] mockitems = new DataItem[]{new DataItem("lorem"), new DataItem("ipsum"), new DataItem("dolor"), new DataItem("sit"), new DataItem("lirem")};

    private List<DataItem> items = new ArrayList<>();
    private long idcount = 0;

    public SimpleDataItemCRUDOperationsImpl() {
        for (DataItem item : mockitems) {
            createItem(item);
        }
    }

    @Override
    public long createItem(DataItem item) {
        item.setId(++idcount);
        items.add(item);

        return item.getId();
    }

    @Override
    public List<DataItem> readAllItems() {
        return items;
    }

    @Override
    public DataItem readItem(long id) {
        for (DataItem item : items) {
            if (item.getId() == id) {
                return item;
            }
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

    @Override
    public boolean authenticateUser(User user) {
        return true;
    }
}
