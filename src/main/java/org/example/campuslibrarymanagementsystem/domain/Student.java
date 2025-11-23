package org.example.campuslibrarymanagementsystem.domain;

public class Student extends Person {
    private String program;
    private int year;

    public Student(String id, String name, String program, int year){
        super(id, name);
        this.program = program;
        this.year = year;
    }

    public String getProgram() {
        return program;
    }

    public int getYear(){
        return year;
    }

    public void setProgram(String program){
        this.program = program;
    }

    public void setYear(int year){
        this.year = year;
    }
}
