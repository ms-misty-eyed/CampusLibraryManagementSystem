package org.example.campuslibrarymanagementsystem.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.campuslibrarymanagementsystem.domain.Book;
import org.example.campuslibrarymanagementsystem.domain.Student;
import org.example.campuslibrarymanagementsystem.service.LibraryService;
import org.example.campuslibrarymanagementsystem.storage.StudentFileLog;

import java.nio.file.Paths;
import java.util.Optional;

public class DemoDataUI {
    private LibraryService service;
    private BorderPane root;
    private ListView<String> studentListView;
    private ListView<String> bookListView;
    private ListView<String> studentLogListView;
    private TextArea logsArea;  // Store reference to logs area
    private String currentStudentId;  // Track currently selected student

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
        grid.add(new Label("Year of study: "),0,3);
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

            // Refresh all student lists
            if (studentListView != null) {
                refreshStudentList(studentListView);
            }
            if (studentLogListView != null) {
                refreshStudentList(studentLogListView);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Student added successfully");
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
        grid.add(new Label("ISBN: "), 0,0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("Title: "),0,1);
        grid.add(titleField,1,1);
        grid.add(new Label("Total copies: "),0,2);
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

            // Refresh book list
            if (bookListView != null) {
                refreshBookList(bookListView);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Book was successfully added");
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
            System.out.println(b.getIsbn() + " " + b.getTitle() + " Available copies: " + (b.getTotalCopies() - b.getCheckedOut()) + "/" + b.getTotalCopies());
        }
    }

    private void rentBook(){
        TextInputDialog studentDialog = new TextInputDialog();
        studentDialog.setContentText("Student ID:");
        Optional<String> studentResult = studentDialog.showAndWait();
        if(!studentResult.isPresent()) return;

        TextInputDialog isbnDialog = new TextInputDialog();
        isbnDialog.setContentText("Book ISBN:");
        Optional<String> isbnResult = isbnDialog.showAndWait();
        if(!isbnResult.isPresent()) return;

        String studentId = studentResult.get();
        boolean success = service.rentBook(studentId, isbnResult.get());

        // Refresh book list if successful
        if (success && bookListView != null) {
            refreshBookList(bookListView);
        }

        // Refresh logs if this is the currently selected student
        if (success && currentStudentId != null && currentStudentId.equals(studentId) && logsArea != null) {
            showStudentLogs(currentStudentId, logsArea);
        }

        Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setContentText(success ? "Book rented successfully!" : "Failed to rent book");
        alert.showAndWait();
    }

    private void returnBook(){
        TextInputDialog studentDialog = new TextInputDialog();
        studentDialog.setContentText("Student ID:");
        Optional<String> studentResult = studentDialog.showAndWait();
        if(!studentResult.isPresent()) return;

        TextInputDialog isbnDialog = new TextInputDialog();
        isbnDialog.setContentText("Book ISBN:");
        Optional<String> isbnResult = isbnDialog.showAndWait();
        if(!isbnResult.isPresent()) return;

        String studentId = studentResult.get();
        boolean success = service.returnBook(studentId, isbnResult.get());

        // Refresh book list if successful
        if (success && bookListView != null) {
            refreshBookList(bookListView);
        }

        // Refresh logs if this is the currently selected student
        if (success && currentStudentId != null && currentStudentId.equals(studentId) && logsArea != null) {
            showStudentLogs(currentStudentId, logsArea);
        }

        Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setContentText(success ? "Book returned successfully!" : "Failed to return book");
        alert.showAndWait();
    }

    public void setUpTabs(){
        TabPane tabPane = new TabPane();

        //Tab for student records
        Tab studentTab = new Tab("Student Records");
        studentTab.setClosable(false);
        studentListView = new ListView<>();
        refreshStudentList(studentListView);

        Button addStudentBtn = new Button("Add Student");
        addStudentBtn.setOnAction(e -> addStudent());

        Button refreshStudentsBtn = new Button("Refresh");
        refreshStudentsBtn.setOnAction(e -> refreshStudentList(studentListView));

        // Center buttons at the bottom
        HBox studentButtons = new HBox(10, addStudentBtn, refreshStudentsBtn);
        studentButtons.setAlignment(javafx.geometry.Pos.CENTER);
        VBox studentBox = new VBox(10, studentListView, studentButtons);
        studentTab.setContent(studentBox);

        //Tab for book catalog with TableView
        Tab bookTab = new Tab("Book Catalog");
        bookTab.setClosable(false);

        // Create TableView for books
        TableView<Book> bookTableView = new TableView<>();
        bookTableView.setTableMenuButtonVisible(false);
        bookTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ISBN Column
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));
        isbnCol.setPrefWidth(150);

        // Title Column
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        titleCol.setPrefWidth(250);

        // Total Copies Column
        TableColumn<Book, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTotalCopies())));
        totalCol.setPrefWidth(80);

        // Checked Out Column
        TableColumn<Book, String> checkedOutCol = new TableColumn<>("Checked Out");
        checkedOutCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCheckedOut())));
        checkedOutCol.setPrefWidth(100);

        // Available Column
        TableColumn<Book, String> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(data -> {
            Book book = data.getValue();
            int available = book.getTotalCopies() - book.getCheckedOut();
            return new SimpleStringProperty(available + "/" + book.getTotalCopies());
        });
        availableCol.setPrefWidth(100);

        bookTableView.getColumns().addAll(isbnCol, titleCol, totalCol, checkedOutCol, availableCol);
        refreshBookTable(bookTableView);

        Button addBookBtn = new Button("Add Book");
        addBookBtn.setOnAction(e -> {
            addBook();
            refreshBookTable(bookTableView);
        });

        Button refreshBooksBtn = new Button("Refresh");
        refreshBooksBtn.setOnAction(e -> refreshBookTable(bookTableView));

        // Center buttons at the bottom
        HBox bookButtons = new javafx.scene.layout.HBox(10, addBookBtn, refreshBooksBtn);
        bookButtons.setAlignment(javafx.geometry.Pos.CENTER);
        VBox bookBox = new VBox(10, bookTableView, bookButtons);
        bookTab.setContent(bookBox);

        //Student Logs tab
        Tab logTab = new Tab("Student Logs");
        logTab.setClosable(false);

        studentLogListView = new ListView<>();
        refreshStudentList(studentLogListView);

        logsArea = new TextArea();
        logsArea.setEditable(false);
        logsArea.setPromptText("Select a student to view their rental logs");

        studentLogListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String studentId = newVal.split(" - ")[0];
                currentStudentId = studentId;
                showStudentLogs(studentId, logsArea);
            }
        });

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(studentLogListView, logsArea);
        splitPane.setDividerPositions(0.3);
        logTab.setContent(splitPane);

        tabPane.getTabs().addAll(studentTab, bookTab, logTab);
        root.setCenter(tabPane);
    }

    private void refreshStudentList(ListView<String> listView) {
        listView.getItems().clear();
        java.util.List<Student> students = service.getRegistry().listAll();
        for (Student s : students) {
            listView.getItems().add(s.getId() + " - " + s.getName() +
                    " (" + s.getProgram() + ", Year " + s.getYear() + ")");
        }
    }

    private void refreshBookList(ListView<String> listView) {
        listView.getItems().clear();
        java.util.Collection<Book> books = service.getCatalog().all();
        for (Book b : books) {
            int available = b.getTotalCopies() - b.getCheckedOut();
            listView.getItems().add(b.getIsbn() + " - " + b.getTitle() +
                    " (Available: " + available + "/" + b.getTotalCopies() + ")");
        }
    }
    private void refreshBookTable(TableView<Book> tableView) {
        tableView.getItems().clear();
        java.util.Collection<Book> books = service.getCatalog().all();
        tableView.getItems().addAll(books);
    }

    private void showStudentLogs(String studentId, TextArea logsArea) {
        try {
            StudentFileLog log = new StudentFileLog(Paths.get("data/logs"), studentId);
            java.util.List<String> logs = log.readAllPretty();

            if (logs.isEmpty()) {
                logsArea.setText("No rental history for this student.");
            } else {
                logsArea.setText(String.join("\n", logs));
            }
        } catch (Exception e) {
            logsArea.setText("No logs found for this student.");
        }
    }
}