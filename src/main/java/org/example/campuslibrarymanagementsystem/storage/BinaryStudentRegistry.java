package org.example.campuslibrarymanagementsystem.storage;

import javafx.scene.shape.Path;
import org.example.campuslibrarymanagementsystem.domain.Student;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BinaryStudentRegistry {
    private Path dataFile;
    private Map<String, Long> indexByld;

    public BinaryStudentRegistry(Path baseDir){

    }

    public void addOrUpdateStudent(Student student){

    }

    public Optional<Student> findById(String id){

        return Optional.empty();
    }

    public List<Student> listAll(){

        return List.of();
    }

}
