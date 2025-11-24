package org.example.campuslibrarymanagementsystem.domain;

public interface Rentable {
    boolean isAvailable();
    void checkout();
    void checkin();
}
