package org.example.campuslibrarymanagementsystem.service;

import org.example.campuslibrarymanagementsystem.domain.Student;
import org.example.campuslibrarymanagementsystem.storage.BinaryStudentRegistry;

import java.util.Optional;
import java.util.concurrent.Callable;

public class RecordMatcher implements Callable<Optional<Student>> {
    private BinaryStudentRegistry registry;
    private String studentId;

    public RecordMatcher(BinaryStudentRegistry registry, String studentId){
        this.registry = registry;
        this.studentId = studentId;
    }

    public Optional<Student> call(){
        return registry.findById(studentId);
    }


}
