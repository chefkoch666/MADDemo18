package com.example.chefk.maddemo18;

import android.app.Application;

import com.example.chefk.maddemo18.model.IDataItemCRUDOperations;
import com.example.chefk.maddemo18.model.SimpleDataItemCRUDOperations;

public class DataItemApplication extends Application {

    private IDataItemCRUDOperations crudOperations;

    @Override
    public void onCreate() {
        super.onCreate();
        this.crudOperations = new SimpleDataItemCRUDOperations();
    }

    public IDataItemCRUDOperations getCRUDOperations() {
        return crudOperations;
    }
}
