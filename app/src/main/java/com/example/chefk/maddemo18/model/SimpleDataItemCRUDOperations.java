package com.example.chefk.maddemo18.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleDataItemCRUDOperations implements IDataItemCRUDOperations {

    private static final DataItem[] items = new DataItem[]{new DataItem("lorem"), new DataItem("ipsum"), new DataItem("dolor"), new DataItem("sit"), new DataItem("lirem")};

    @Override
    public long createItem(DataItem item) {
        return 0;
    }

    @Override
    public List<DataItem> readAllItems() {
        return new ArrayList(Arrays.asList(items));
    }

    @Override
    public DataItem readItem(long id) {
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
