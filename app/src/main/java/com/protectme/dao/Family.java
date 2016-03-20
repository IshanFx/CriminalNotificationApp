package com.protectme.dao;

import io.realm.RealmObject;

/**
 * Created by IshanFx on 3/20/2016.
 */
public class Family extends RealmObject {

    private String name;
    private String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
