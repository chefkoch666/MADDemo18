package com.example.chefk.maddemo18.model;

import java.io.Serializable;

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
}
