package com.example.chefk.maddemo18;

import android.app.Application;

import com.example.chefk.maddemo18.model.IDataItemCRUDOperations;
import com.example.chefk.maddemo18.model.LocalDataItemCRUDOperations;
import com.example.chefk.maddemo18.model.RemoteDataItemCRUDOperationsImpl;
import com.example.chefk.maddemo18.model.SimpleDataItemCRUDOperationsImpl;

public class DataItemApplication extends Application {

    private IDataItemCRUDOperations crudOperations;

    @Override
    public void onCreate() {
        super.onCreate();
        this.crudOperations = /*new SimpleDataItemCRUDOperationsImpl()*/ /*new LocalDataItemCRUDOperations(this);*/ new RemoteDataItemCRUDOperationsImpl();
    }

    public IDataItemCRUDOperations getCRUDOperations() {
        return crudOperations;
    }
}
