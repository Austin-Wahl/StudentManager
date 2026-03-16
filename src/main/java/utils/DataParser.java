package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.UUID;
import models.Database;
import models.StudentTableRecord;

public class DataParser {

    /**
     * Parses the data from the test data file 
     */
    public static void createDatabaseInstanceAndWriteData(BufferedReader reader) throws IOException {
        // While there is a next line
        String line;
        while((line = reader.readLine()) != null) {
            // If the line is a new line then just move on
            if(line.length() <= 0) continue;

            UUID uuid = UUID.fromString(line);
            String name = reader.readLine();
            String email = reader.readLine();
            double GPA = Double.parseDouble(reader.readLine());

            StudentTableRecord temp = new StudentTableRecord(uuid, name, email, GPA);
            
            Database.getInstance().createStudent(temp);
        }

        System.out.printf("DATABASE CREATED... %d records inserted.",Database.getInstance().getStudents().size());
    }
}