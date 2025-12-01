package org.example.campuslibrarymanagementsystem.service;

import org.example.campuslibrarymanagementsystem.domain.Book;
import org.example.campuslibrarymanagementsystem.domain.Student;
import org.example.campuslibrarymanagementsystem.storage.BinaryStudentRegistry;
import org.example.campuslibrarymanagementsystem.storage.LibraryCatalog;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.*;

public class LibraryService {
    private BinaryStudentRegistry registry;
    private LibraryCatalog catalog;
    private Path logsDir;
    private ExecutorService pool;
    private Locks locks;

    public LibraryService(Path dataDir) {
        this.logsDir = dataDir.resolve("logs");
        this.registry = new BinaryStudentRegistry(dataDir);
        this.catalog = new LibraryCatalog(dataDir);
        this.pool = Executors.newFixedThreadPool(4);
        this.locks = new Locks();
    }

    public BinaryStudentRegistry getRegistry(){
        return registry;
    }

    public LibraryCatalog getCatalog(){
        return catalog;
    }

    public void shutdown(){
        pool.shutdown();
    }

    public boolean rentBook(String studentId, String isbn){
        locks.catalogue().writeLock().lock();
        try {
            //find the student using a thread running in the background
            RecordMatcher matcher = new RecordMatcher(registry, studentId);
            Future<Optional<Student>> future = pool.submit(matcher);
            Optional<Student> studentOpt = future.get();

            if(!studentOpt.isPresent()){
                System.out.println("Student was not found");
                return false;
            }
            //find the book in the catalogue
            Optional<Book> bookOpt = catalog.get(isbn);
            if(!bookOpt.isPresent()){
                System.out.println("Book not fonud");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error renting the book: " + e.getMessage());
        }finally{
            locks.catalogue().writeLock().unlock();
        }
        return false;
    }

}