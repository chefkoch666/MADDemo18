package com.example.chefk.maddemo18.model;

import java.io.Serializable;
import java.util.Comparator;

public class DataItem implements Serializable {

    private long id = -1;
    private String name;
    private long expiry;
    private boolean done;

    public DataItem() {

    }

    public DataItem(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public  String toString() {
        return this.id + ":" + this.name;
    }

    public static Comparator<DataItem> SORT_BY_NAME = new Comparator<DataItem>() {
        @Override
        public int compare(DataItem item1, DataItem item2) {
            return String.valueOf(item1.getName()).toLowerCase().compareTo(String.valueOf(item2.getName()).toLowerCase());
        }
    };

    public static Comparator<DataItem> SORT_BY_ID = new Comparator<DataItem>() {
        @Override
        public int compare(DataItem item1, DataItem item2) {
            return (int)(item1.getId() - item2.getId());
        }
    };
}
