package org.example.campuslibrarymanagementsystem.domain;

public class Book extends Item implements Rentable {
    private int totalCopies;
    private int checkedOut;

    public Book (String isbn, String title, int totalCopies){
        super(isbn, title);
        this.totalCopies = totalCopies;
    }

    public boolean isAvailable(){
        //LOGIC
        return true;
    }
    public void checkout(){

    }

    public void checkin(){

    }

    public int getTotalCopies(){
        return totalCopies;
    }

    public int getCheckedOut(){
        return checkedOut;
    }

}
