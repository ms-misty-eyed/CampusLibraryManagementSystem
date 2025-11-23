module org.example.campuslibrarymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.campuslibrarymanagementsystem to javafx.fxml;
    //exports org.example.campuslibrarymanagementsystem;
    exports org.example.campuslibrarymanagementsystem.domain;
    opens org.example.campuslibrarymanagementsystem.domain to javafx.fxml;
}