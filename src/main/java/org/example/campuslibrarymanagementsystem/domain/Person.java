package org.example.campuslibrarymanagementsystem.domain;

import java.io.Serializable;

public abstract class Person implements Serializable{
    private String id;
    private String name;
    public Person(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

}
