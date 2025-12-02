package org.example.campuslibrarymanagementsystem.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.campuslibrarymanagementsystem.service.LibraryService;

import java.nio.file.Paths;

public class MainApp extends Application {
    private LibraryService service;

    @Override
    public void start(Stage stage) throws Exception {
        this.service = new LibraryService(Paths.get("data"));

        DemoDataUI ui = new DemoDataUI(service);
        Scene scene = new Scene(ui.getRoot(), 800, 600);

        stage.setTitle("Campus Library Management System");
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() {
        if(service!= null){
            service.shutdown(); //cleans up the thread pool
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
