package org.example.campuslibrarymanagementsystem.storage;

import org.example.campuslibrarymanagementsystem.domain.Book;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LibraryCatalog {
    private Map<String, Book> byIsbn;
    private Path snapshot;

    public LibraryCatalog(Path baseDir){
        this.byIsbn = new HashMap<>();
        this.snapshot = baseDir.resolve("catalog.dat");
        load();
    }

    public void add(Book book){
        String isbn = book.getIsbn();
        this.byIsbn.put(isbn, book);
        save();
    }

    public Optional<Book> get(String isbn){
        Book book = this.byIsbn.get(isbn);

        if(book != null){
            return Optional.of(book);
        }
        else{
            return Optional.empty();
        }
    }

    public Collection<Book> all(){
        return this.byIsbn.values();
    }

    public void load(){
        //Look if the file exists
        if(Files.exists(this.snapshot)){
            try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(this.snapshot.toFile()))){
                this.byIsbn = (Map<String, Book>) in.readObject();

            }catch(Exception e){
                System.out.println("Error while loading the catalogue.dat file: " + e.getMessage());
            }
        }
    }

    public void save(){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(this.snapshot.toFile()))){
            //Save the hashmap
            out.writeObject(this.byIsbn);
        }catch(Exception e){
            System.out.println("Error trying to save: " + e.getMessage());
        }
    }

}
