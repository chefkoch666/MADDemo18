package com.example.chefk.maddemo18.model;

import android.service.carrier.CarrierMessagingService;

import java.util.List;

import javax.xml.transform.Result;

public interface IDataItemCRUDOperationsAsync {

    public static interface ResultCallback<T> {

        public void onresult(T result);

    }

    public void createItem(DataItem item, ResultCallback<Long> onresult);

    public void readAllItems(ResultCallback<List<DataItem>> onresult);

    public void readItem(long id, ResultCallback<DataItem> onresult);

    public void updateItem(long id, DataItem item, ResultCallback<Boolean> onresult);

    public void deleteItem(long id, ResultCallback<Boolean> onresult);

    public void authenticateUser(User user, ResultCallback<Boolean> onresult);

}
