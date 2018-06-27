package com.example.chefk.maddemo18.model;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class RemoteDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    public static interface TodoWebAPI {

        @POST("/api/todos")
        public Call<DataItem> createItem(@Body DataItem item);

        @GET("/api/todos")
        public Call<List<DataItem>> readAllDataItems();

        @GET("/api/todos/{id}")
        public Call<DataItem> readDataItem(@Path("id") long id);

        @PUT("/api/todos/{id}")
        public Call<DataItem> updateDataItem(@Path("id") long id, @Body DataItem item);

        @DELETE("/api/todos/{id}")
        public Call<Boolean> deleteItem(@Path("id") long id);

        @PUT("/api/users/auth")
        public Call<Boolean> authenticateUser(@Body User user);
    }

    private TodoWebAPI serviceProxy;

    public RemoteDataItemCRUDOperationsImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("http://10.0.2.2:8080") // also change in LoginActivity.java
                .baseUrl("http://172.16.42.58:8080") // also change in LoginActivity.java
                //.baseUrl("http://134.245.70.212:8080") // also change in LoginActivity.java
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        serviceProxy = retrofit.create(TodoWebAPI.class);
    }


    @Override
    public long createItem(DataItem item) {
        try {
            Call<DataItem> call = serviceProxy.createItem(item);
            Response<DataItem> response = call.execute();
            DataItem returnValue = response.body();

            return returnValue.getId();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
            //e.printStackTrace();
        }
    }

    @Override
    public List<DataItem> readAllItems() {
        try {
            return serviceProxy.readAllDataItems().execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
            //e.printStackTrace();
        }
    }

    @Override
    public DataItem readItem(long id) {
        try {
            return serviceProxy.readDataItem(id).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
            //e.printStackTrace();
        }
    }

    @Override
    public boolean updateItem(long id, DataItem item) {
        try {
            return serviceProxy.updateDataItem(id, item).execute().body() != null;
        } catch (IOException e) {
            throw new RuntimeException(e);
            //e.printStackTrace();
        }
    }

    @Override
    public boolean deleteItem(long id) {
        try {
            return serviceProxy.deleteItem(id).execute().body() != null;
        } catch (IOException e) {
            throw new RuntimeException(e);
            //e.printStackTrace();
        }
    }

    @Override
    public boolean authenticateUser(User user) {
        try {
            return serviceProxy.authenticateUser(user).execute().body() != null;
        } catch (IOException e) {
            throw new RuntimeException(e);
            //e.printStackTrace();
        } catch (NullPointerException e2) {
            throw new RuntimeException(e2);
        }
    }
}
