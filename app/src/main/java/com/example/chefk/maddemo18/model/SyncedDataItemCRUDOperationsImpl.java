package com.example.chefk.maddemo18.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SyncedDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {
    private static final WebserviceURL webserviceURLString = new WebserviceURL(); // see/change value in model WebserviceURL.java

    private IDataItemCRUDOperations localCRUD;
    private IDataItemCRUDOperations remoteCRUD;

    private boolean remoteAvailable = false; // assume OFFLINE until url has been checked

    public SyncedDataItemCRUDOperationsImpl(Context context) {
        this.localCRUD = new LocalDataItemCRUDOperations(context);
        this.remoteCRUD = new RemoteDataItemCRUDOperationsImpl();
        initialiseCRUDOperations(new IDataItemCRUDOperationsAsync.ResultCallback<Boolean>() {
            @Override
            public void onresult(Boolean result) {
                remoteAvailable = result;
            }
        });
    }

    @Override
    public long createItem(DataItem item) {
        long id = localCRUD.createItem(item);
        if (remoteAvailable) remoteCRUD.createItem(item);
        return id;
    }

    @Override
    public List<DataItem> readAllItems() {
        return localCRUD.readAllItems(); // local is master
    }

    @Override
    public DataItem readItem(long id) {
        return localCRUD.readItem(id);
    } // local is master

    @Override
    public boolean updateItem(long id, DataItem item) {
        boolean updated = localCRUD.updateItem(id, item);
        if (updated && remoteAvailable) remoteCRUD.updateItem(id, item);
        return updated;
    }

    @Override
    public boolean deleteItem(long id) {
        boolean deleted = this.deleteItem(id);
        if (deleted && remoteAvailable) {
            this.deleteItem(id); // 2x gleiche Funktion??
        }
        return deleted;
    }

    @Override
    public boolean deleteAllTodos() { // liegen lokale Todos vor, loesche alle auf webservice
        if (remoteAvailable) {
            return remoteCRUD.deleteAllTodos();
        }
        return false;
    }

    @Override
    public boolean authenticateUser(User user) {
        if (remoteAvailable) return remoteCRUD.authenticateUser(user);
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    public void initialiseCRUDOperations(final IDataItemCRUDOperationsAsync.ResultCallback<Boolean> oninitialised) {
        new AsyncTask<Void, Void, Boolean>() { // check if webservice is reachable on Thread
            @Override
            protected Boolean doInBackground(Void... voids) {
                remoteAvailable = false; // assume OFFLINE until url has been checked
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(webserviceURLString.getUrl()); // see very top
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(1000);
                    connection.setReadTimeout(1000);
                    int code = connection.getResponseCode();
                    if (code == 200) remoteAvailable = true;
                    return remoteAvailable;
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return remoteAvailable;  // return safely with OFFLINE
                } finally {
                    if (connection != null) connection.disconnect();
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                oninitialised.onresult(aBoolean);
            }
        }.execute();
    }
}
