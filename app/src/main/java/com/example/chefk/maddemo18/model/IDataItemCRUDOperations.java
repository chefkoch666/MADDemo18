package com.example.chefk.maddemo18.model;

import java.util.List;

public interface IDataItemCRUDOperations {

    public long createItem(DataItem item);

    public List<DataItem> readAllItems();

    public DataItem readItem(long id);

    public boolean updateItem(long id, DataItem item);

    public boolean deleteItem(long id);

    public boolean authenticateUser(User user);
}
