package org.example.campuslibrarymanagementsystem.domain;

public class Book extends Item implements Rentable {
    private int totalCopies;
    private int checkedOut;

    public Book (String isbn, String title, int totalCopies){
        super(isbn, title);
        this.totalCopies = totalCopies;
        this.checkedOut = 0;
    }

    public boolean isAvailable(){
        if(checkedOut < totalCopies){ return true;}
        else{return false;}

    }
    public void checkout(){
        //Check if there are copies remaining
        if(this.isAvailable()){
            //Add one copy to the checked out
            checkedOut++;
        }
        //Otherwise, indicate no more copies are available
        else{
            System.out.println("No more available copies!");
        }
    }

    public void checkin(){
        //Checks if there are copies left to be returned
        if(checkedOut > 0){
            //Remove one copy from the checked out
            checkedOut--;
        }
        else{System.out.println("no copies to return!");}
    }

    public int getTotalCopies(){
        return totalCopies;
    }

    public int getCheckedOut(){
        return checkedOut;
    }

}
