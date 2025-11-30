package org.example.campuslibrarymanagementsystem.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Locks {
    ReentrantReadWriteLock catalogLock = new ReentrantReadWriteLock();
    Map<String, ReentrantReadWriteLock> studentLock = new HashMap<>();

    public ReentrantReadWriteLock forStudent(String id){
        if(studentLock.containsKey(id)){
            return studentLock.get(id);
        }
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        studentLock.put(id, lock);
        return lock;
    }

    public ReentrantReadWriteLock catalogue(){
        return catalogLock;
    }
}
