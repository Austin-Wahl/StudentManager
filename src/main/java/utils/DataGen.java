package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import models.Student;

/**
 * Simple utility class that just generates 100_000 fake student records
 */
public class DataGen {
    public static final int MAX_RECORDS = 100000;

    public static void main(String args[]) throws IOException {
        GenerateData();
    }
    public static void GenerateData() throws IOException {
        File file = new File("data.txt");
        FileWriter fw = new FileWriter(file);
        int i = 0;

        for(; i < MAX_RECORDS; i++) {
            Student fakeData = GenerateFakeRecord(i);
            fw.write(fakeData.toString());
            if(i < MAX_RECORDS - 1) {
                fw.write("\n\n");
            }
        }

        System.out.printf("GENERATED %d RECORDS\n", i);

        fw.flush();
        fw.close();
    }

    /**
     * Generates a Fake Data Record
     */
    private static Student GenerateFakeRecord(int index) {
        UUID uuid = UUID.randomUUID();
        String name = String.format("Student_%d Last_%d", index, index);
        String email = String.format("student_%d@my.utsa.edu", index);
        double GPA = generateRandomGPA();
        return new Student(uuid, name, email, GPA);
    }

    private static double generateRandomGPA() {
        Random random = new Random();
        double gpa = 0.0 + (4.0 - 0.0) * random.nextDouble();
        return Math.round(gpa * 100.0) / 100.0;
    }
}