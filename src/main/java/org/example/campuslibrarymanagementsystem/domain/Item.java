package org.example.campuslibrarymanagementsystem.domain;

public class Item {
    protected String isbn;
    protected String title;

    public Item(String isbn, String title){
        this.isbn = isbn;
        this.title = title;
    }

    public String getIsbn(){
        return isbn;
    }

    public String getTitle(){
        return title;
    }
}
