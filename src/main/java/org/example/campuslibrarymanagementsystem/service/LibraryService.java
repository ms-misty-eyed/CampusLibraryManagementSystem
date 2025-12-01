package org.example.campuslibrarymanagementsystem.service;

import org.example.campuslibrarymanagementsystem.domain.Book;
import org.example.campuslibrarymanagementsystem.domain.Student;
import org.example.campuslibrarymanagementsystem.storage.BinaryStudentRegistry;
import org.example.campuslibrarymanagementsystem.storage.EventType;
import org.example.campuslibrarymanagementsystem.storage.LibraryCatalog;
import org.example.campuslibrarymanagementsystem.storage.StudentFileLog;

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

    public boolean rentBook(String studentId, String isbn) {
        try {
            //find the student using a thread running in the background
            RecordMatcher matcher = new RecordMatcher(registry, studentId);
            Future<Optional<Student>> future = pool.submit(matcher);
            Optional<Student> studentOpt = future.get();

            if (!studentOpt.isPresent()) {
                System.out.println("Student was not found");
                return false;
            }
            //find the book in the catalogue
            Optional<Book> bookOpt = catalog.get(isbn);
            if (!bookOpt.isPresent()) {
                System.out.println("Book not fonud");
                return false;
            }
            Book book = bookOpt.get();

            //Acquire locks for catalogue and students
            locks.catalogue().writeLock().lock();
            locks.forStudent(studentId).writeLock().lock();

            //Check if book is available, then checkout
            try {
                if (!book.isAvailable()) {
                    System.out.println("Book is not available");
                    return false;
                }
                book.checkout();
                catalog.add(book);

                //Create a log entry
                StudentFileLog log = new StudentFileLog(logsDir, studentId);
                log.append(EventType.RENT, isbn, book.getTitle());
                return true;
            } finally {
                locks.catalogue().writeLock().unlock();
                locks.forStudent(studentId).writeLock().unlock();
            }


        } catch (Exception e) {
            System.out.println("Error renting the book: " + e.getMessage());
            return false;
        }
    }

    public boolean returnBook(String studentId, String isbn) {
        try {
            //find the student using a thread running in the background
            RecordMatcher matcher = new RecordMatcher(registry, studentId);
            Future<Optional<Student>> future = pool.submit(matcher);
            Optional<Student> studentOpt = future.get();

            if (!studentOpt.isPresent()) {
                System.out.println("Student was not found");
                return false;
            }
            //find the book in the catalogue
            Optional<Book> bookOpt = catalog.get(isbn);
            if (!bookOpt.isPresent()) {
                System.out.println("Book not fonud");
                return false;
            }
            Book book = bookOpt.get();

            //Acquire locks for catalogue and students
            locks.catalogue().writeLock().lock();
            locks.forStudent(studentId).writeLock().lock();

            //check-in
            try {
                book.checkin();
                catalog.add(book);

                //Create a log entry
                StudentFileLog log = new StudentFileLog(logsDir, studentId);
                log.append(EventType.RETURN, isbn, book.getTitle());
                return true;
            } finally {
                locks.catalogue().writeLock().unlock();
                locks.forStudent(studentId).writeLock().unlock();
            }


        } catch (Exception e) {
            System.out.println("Error returning the book: " + e.getMessage());
            return false;
        }
    }

}