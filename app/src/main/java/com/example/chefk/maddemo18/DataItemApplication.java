package com.example.chefk.maddemo18;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.example.chefk.maddemo18.model.DataItem;
import com.example.chefk.maddemo18.model.IDataItemCRUDOperations;
import com.example.chefk.maddemo18.model.IDataItemCRUDOperationsAsync;
import com.example.chefk.maddemo18.model.LocalDataItemCRUDOperations;
import com.example.chefk.maddemo18.model.RemoteDataItemCRUDOperationsImpl;
import com.example.chefk.maddemo18.model.SimpleDataItemCRUDOperationsImpl;

import java.util.List;

public class DataItemApplication extends Application implements IDataItemCRUDOperationsAsync {

    private IDataItemCRUDOperations crudOperations;

    @Override
    public void onCreate() {
        super.onCreate();
        this.crudOperations = /*new SimpleDataItemCRUDOperationsImpl()*/
                 new LocalDataItemCRUDOperations(this);
                /* new RemoteDataItemCRUDOperationsImpl(); */
    }

    public IDataItemCRUDOperationsAsync getCRUDOperations() {
        return this;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void createItem(DataItem item, final ResultCallback<Long> onresult) {
        new AsyncTask<DataItem,Void,Long>() {

            @Override
            protected Long doInBackground(DataItem... dataItems) {
                return crudOperations.createItem(dataItems[0]);
            }

            @Override
            protected void onPostExecute(Long id) {
                onresult.onresult(id);
            }
        }.execute(item);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void readAllItems(final ResultCallback<List<DataItem>> onresult) {
        new AsyncTask<Void,Void,List<DataItem>>() {
            @Override
            protected List<DataItem> doInBackground(Void... voids) {
                return crudOperations.readAllItems();
            }

            @Override
            protected void onPostExecute(List<DataItem> dataItems) {
                onresult.onresult(dataItems);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void readItem(long id, final ResultCallback<DataItem> onresult) {
        new AsyncTask<Long,Void,DataItem>() {

            @Override
            protected DataItem doInBackground(Long... longs) {
                return crudOperations.readItem(longs[0]);
            }

            @Override
            protected void onPostExecute(DataItem dataItem) {
                onresult.onresult(dataItem);
            }
        }.execute(id);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void updateItem(final long id, final DataItem item, final ResultCallback<Boolean> onresult) {
        new AsyncTask<Void,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                return crudOperations.updateItem(id, item);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                onresult.onresult(aBoolean);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void deleteItem(final long id, final ResultCallback<Boolean> onresult) {
        new AsyncTask<Long,Void,Boolean>() {
            @Override
            protected Boolean doInBackground(Long... longs) {
                return crudOperations.deleteItem(id);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                onresult.onresult(aBoolean);
            }
        }.execute();
    }
}
