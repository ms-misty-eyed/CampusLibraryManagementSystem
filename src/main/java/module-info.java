module org.example.campuslibrarymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.campuslibrarymanagementsystem to javafx.fxml;
    exports org.example.campuslibrarymanagementsystem;
}