package org.example.campuslibrarymanagementsystem.storage;

import org.example.campuslibrarymanagementsystem.domain.Book;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class LibraryCatalog {
    private Map<String, Book> byIsbn;
    private Path snapshot;

    public LibraryCatalog(Path baseDir){

    }

    public void add(){

    }

    public Optional<Book> get(String isbn){

        return Optional.empty();
    }

    public Collection<Book> all(){

        return java.util.List.of();
    }

}
