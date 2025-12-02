package org.example.campuslibrarymanagementsystem.storage;

import org.example.campuslibrarymanagementsystem.domain.Student;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BinaryStudentRegistry {
    private Path dataFile;
    private Map<String, Long> indexById;

    public BinaryStudentRegistry(Path baseDir){
        try {
            Files.createDirectories(baseDir);  // Create directory if doesn't exist
        } catch (Exception e) {
            System.out.println("Error creating directory: " + e.getMessage());
        }
        this.dataFile = baseDir.resolve("students.dat");
        this.indexById = new HashMap<>();
        loadIndex();
    }

    private void loadIndex(){
        if(!Files.exists(this.dataFile)) {
            return;
        }

        try(DataInputStream in = new DataInputStream(new FileInputStream(this.dataFile.toFile()))){
            long position = 0; //position in the file
            while(true){
                try{
                    int length = in.readInt(); //read the length of the frame
                    byte[] data = new byte[length];
                    in.readFully(data); //read the data of the student

                    //deserialize
                    ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
                    Student student = (Student) objectInputStream.readObject();

                    //add the id to the index, and change the positoin
                    this.indexById.put(student.getId(), position);
                    position += 4 + length;

                }catch(Exception e){
                    break; //end of the file
                }
            }
        }catch(Exception e){
            System.out.println("Error loading the file: " + e.getMessage());
        }
    }

    public void addOrUpdateStudent(Student student){
        try{
            //serialize student to bytes
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(student);
            objOut.flush();;
            byte[] data = byteOut.toByteArray();

            //get the file size
            long position = Files.exists(this.dataFile) ? Files.size(this.dataFile) : 0;

            //append to the file
            try(DataOutputStream out = new DataOutputStream(new FileOutputStream(this.dataFile.toFile(), true))){
                out.writeInt(data.length); //write lenght of the data
                out.write(data); // write the actual data
            }
            //update the index
            this.indexById.put(student.getId(), position);
        }catch (Exception e){
            System.out.println("Error while adding student to the file: " + e.getMessage());
        }
    }

    public Optional<Student> findById(String id){
        Long position = this.indexById.get(id);
        if(position == null){
            return Optional.empty(); //if the student is not found
        }

        try(RandomAccessFile raf = new RandomAccessFile(this.dataFile.toFile(), "r")){
            raf.seek(position); //go directly to the position
            int length = raf.readInt(); //read the frame length
            byte[] data = new byte[length];
            raf.readFully(data); //read the student data

            //deserialize
            ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(data));
            Student student = (Student) objIn.readObject();
            return Optional.of(student);
        }catch (Exception e){
            System.out.println("error finding thw student: " + e.getMessage());
            return Optional.empty();
        }

    }

    public List<Student> listAll(){
        List<Student> students = new ArrayList<>();
        if(!Files.exists(this.dataFile)){
            return students; //this is empty
        }
        try(DataInputStream in = new DataInputStream(new FileInputStream(this.dataFile.toFile()))){
            while(true){
                try{
                    int length = in.readInt();
                    byte[] data = new byte[length];
                    in.readFully(data);
                    ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(data));
                    Student student = (Student) objIn.readObject();
                    students.add(student);
                }catch(Exception e){
                    break; //the end of the file
                }

            }

        }catch(Exception e){
            System.out.println("error reading from the file: " + e.getMessage());
        }

        return students;
    }

}
