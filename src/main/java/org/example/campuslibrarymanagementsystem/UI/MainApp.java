package org.example.campuslibrarymanagementsystem.UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.campuslibrarymanagementsystem.service.LibraryService;

import java.nio.file.Paths;
import java.util.Scanner;

public class MainApp extends Application {
    private LibraryService service;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.service = new LibraryService(Paths.get("data"));

        //DemoDataUI ui = new DemoDataUI(service);

        //Scene scene = new Scene();

    }

    public void stop() {
        if(service!= null){
            service.shutdown(); //cleans up the thread pool
        }
    }
}
