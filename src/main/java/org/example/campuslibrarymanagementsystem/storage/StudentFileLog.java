package org.example.campuslibrarymanagementsystem.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StudentFileLog {
    private Path logFile;

    public StudentFileLog(Path logsDir, String studentId){
        try {
            Files.createDirectories(logsDir);  // Create logs directory if doesn't exist
        } catch (Exception e) {
            System.out.println("Error creating logs directory: " + e.getMessage());
        }
        this.logFile = logsDir.resolve(studentId+ ".dat");
    }

    public void append(EventType eventType, String isbn, String title){
        try(DataOutputStream out = new DataOutputStream(
                new FileOutputStream(this.logFile.toFile(), true)
        )){
         out.writeLong(System.currentTimeMillis()); //Write the time
         out.writeByte(eventType.ordinal()); //Write event (0 = rent, 1 = return)
         out.writeUTF(isbn);
         out.writeUTF(title);
        }catch(Exception e){
            System.out.println("Error while writing into the log: " + e.getMessage());
        }
    }

    public List<String> readAllPretty(){
        List<String> entries = new ArrayList<>();

        if(!Files.exists(this.logFile)){
            //return empty list
            return entries;
        }

        try(DataInputStream in = new DataInputStream(new FileInputStream(this.logFile.toFile()))){
            while(true){
                try{
                    long timestamp = in.readLong();
                    byte eventByte = in.readByte();
                    String isbn =  in.readUTF();
                    String title = in.readUTF();

                    EventType event = EventType.values()[eventByte];

                    String formatted = String.format("%tF %<tT - %s - %s (%s)", timestamp, event, title, isbn);
                    entries.add(formatted);
                }catch(Exception e){
                    break; //the end of the file was reached
                }
            }
        }catch(Exception e){
            System.out.println("error reading from the log file: " + e.getMessage());
        }

        return entries;
    }
}
