package org.example.campuslibrarymanagementsystem.UI;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.example.campuslibrarymanagementsystem.domain.Book;
import org.example.campuslibrarymanagementsystem.domain.Student;
import org.example.campuslibrarymanagementsystem.service.LibraryService;
import java.util.Optional;

public class DemoDataUI {
    private LibraryService service;
    private BorderPane root;

    public DemoDataUI(LibraryService service){
        this.service = service;
        this.root = new BorderPane();

        //Build the graphic interface:
        setUpMenuBar();
        setUpTabs();
    }

    public BorderPane getRoot() {
        return root;
    }

    public void setUpMenuBar(){
        MenuBar menuBar = new MenuBar();

        //File Menu
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e-> System.exit(0));
        fileMenu.getItems().add(exitItem);

        //Student menu
        Menu studentMenu = new Menu("Student");
        MenuItem addStudentItem = new MenuItem("Add a student");
        addStudentItem.setOnAction(e-> addStudent());
        MenuItem listStudentsItem = new MenuItem("List students");
        listStudentsItem.setOnAction(e-> listStudents());
        studentMenu.getItems().addAll(addStudentItem, listStudentsItem);

        //Book menu
        Menu bookMenu = new Menu("Book");
        MenuItem addBookItem = new MenuItem("Add a book");
        addBookItem.setOnAction(e->addBook());
        MenuItem listBooksItem = new MenuItem("List books");
        listBooksItem.setOnAction(e-> listBooks());
        bookMenu.getItems().addAll(addBookItem, listBooksItem);

        //Rental menu
        Menu rentalMenu = new Menu("Rental");
        MenuItem rentBookItem = new MenuItem("Rent a book");
        rentBookItem.setOnAction(e-> rentBook());
        MenuItem returnBookItem = new MenuItem("Return a book");
        returnBookItem.setOnAction(e-> returnBook());
        rentalMenu.getItems().addAll(rentBookItem,returnBookItem);

        menuBar.getMenus().addAll(fileMenu, studentMenu, bookMenu, rentalMenu);
        root.setTop(menuBar);
    }

    private void addStudent(){
        Dialog<Student> dialogue = new Dialog<>();
        dialogue.setTitle("Add a student");
        dialogue.setHeaderText("Enter the student's information");

        //Create the fields for the information
        TextField idTextField = new TextField();
        idTextField.setPromptText("Student id");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField programTextField = new TextField();
        programTextField.setPromptText("Program");
        TextField yearOfStudyTextField = new TextField();
        yearOfStudyTextField.setPromptText("Year of study");

        //Layout the grid:
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(new Label("Student id: "),0,0);
        grid.add(idTextField, 1,0);
        grid.add(new Label("Name: "),0,1);
        grid.add(nameField, 1,1);
        grid.add(new Label("Program: "),0,2);
        grid.add(programTextField, 1,2);
        grid.add(new Label("Year of study:: "),0,3);
        grid.add(yearOfStudyTextField, 1,3);

        dialogue.getDialogPane().setContent(grid);

        //Add ok button and cancel
        ButtonType okButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialogue.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        //Convert result for when the ok button is clicked
        dialogue.setResultConverter(button->{
            try{
                if(button == okButton){
                    String id = idTextField.getText();
                    String name = nameField.getText();
                    String program = programTextField.getText();
                    int yearOfStudy = Integer.parseInt(yearOfStudyTextField.getText());

                    return new Student(id, name, program, yearOfStudy);
                }
            }catch(Exception e){
                return null;
            }
            return null;
        });

        //Show dialogue and result
        Optional<Student> result = dialogue.showAndWait();
        if(result.isPresent() && result.get() != null){
            service.getRegistry().addOrUpdateStudent(result.get());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Student added sucessfully");
            alert.showAndWait();
        }
    }

    private void addBook(){
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add Book");
        dialog.setHeaderText("Enter Book Information");

        TextField isbnField = new TextField();
        isbnField.setPromptText("isbn");
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField copiesField = new TextField();
        copiesField.setPromptText("Total copies");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("isbn: "), 0,0 );
        grid.add(isbnField, 1, 0);
        grid.add(new Label("Title: "),0,1);
        grid.add(titleField,1,1);
        grid.add(new Label("Total copies"),0,2);
        grid.add(copiesField,1,2);

        dialog.getDialogPane().setContent(grid);

        ButtonType okButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        dialog.setResultConverter(button ->{
            if(button == okButton){
                try{
                    String isbn = isbnField.getText();
                    String title = titleField.getText();
                    int totalCopies = Integer.parseInt(copiesField.getText());
                    return new Book(isbn, title, totalCopies);
                }catch(Exception e){
                    return null;
                }
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();
        if(result.isPresent() && result.get() != null){
            service.getCatalog().add(result.get());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Book was sucesfully added");
            alert.showAndWait();
        }
    }

    private void listStudents(){
        java.util.List<Student> students = service.getRegistry().listAll();
        System.out.println("-------All Students-------");
        for(Student s: students){
            System.out.println(s.getId() + " " + s.getName() + " " + s.getProgram() + " " + s.getYear());
        }
    }

    private void listBooks(){
        java.util.Collection<Book> books = service.getCatalog().all();
        System.out.println("-----All Books -----");
        for(Book b : books){
            System.out.println(b.getIsbn() + " " + b.getTitle() + "Available copies: " + (b.getTotalCopies() - b.getCheckedOut()) + "/" + b.getTotalCopies());
        }
    }

    private void rentBook(){
        TextInputDialog studentDialog = new TextInputDialog();
        studentDialog.setContentText("Student id: ");
        Optional<String> studentResult = studentDialog.showAndWait();
        if(!studentResult.isPresent()) return;

        TextInputDialog isbnDialog = new TextInputDialog();
        isbnDialog.setContentText("Book isbn: ");
        Optional<String> isbnResult = isbnDialog.showAndWait();
        if(!isbnResult.isPresent()) return;

        boolean sucess = service.rentBook(studentResult.get(), isbnResult.get());

        Alert alert = new Alert(sucess? Alert.AlertType.INFORMATION: Alert.AlertType.ERROR);
        alert.setContentText(sucess? "Book rented sucessfully": "Book was not rented (error)");
        alert.showAndWait();
    }

    private void returnBook(){
        TextInputDialog studentDialog = new TextInputDialog();
        studentDialog.setContentText("Student id: ");
        Optional<String> studentResult = studentDialog.showAndWait();
        if(!studentResult.isPresent()) return;

        TextInputDialog isbnDialog = new TextInputDialog();
        isbnDialog.setContentText("Book isbn: ");
        Optional<String> isbnResult = isbnDialog.showAndWait();
        if(!isbnResult.isPresent()) return;

        boolean sucess = service.returnBook(studentResult.get(), isbnResult.get());

        Alert alert = new Alert(sucess? Alert.AlertType.INFORMATION: Alert.AlertType.ERROR);
        alert.setContentText(sucess? "Book returned sucessfully": "Book was not returned (error)");
        alert.showAndWait();
    }

    public void setUpTabs(){
        TabPane tabPane = new TabPane();

        //Tab for student record
        Tab studentTab= new Tab("Student Records");
        studentTab.setClosable(false);
        TextArea studentArea = new TextArea();
        studentArea.setEditable(false);
        studentTab.setContent(studentArea);

        //Tab for book catalog
        Tab bookTab = new Tab("Book Catalog");
        bookTab.setClosable(false);
        TextArea bookArea = new TextArea();
        bookArea.setEditable(false);
        bookTab.setContent(bookArea);

        //Student Logs
        Tab logTab = new Tab("Student Logs");
        logTab.setClosable(false);
        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logTab.setContent(logArea);

        tabPane.getTabs().addAll(studentTab,bookTab,logTab);
        root.setCenter(tabPane);

    }
}
