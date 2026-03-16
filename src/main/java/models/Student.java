package models;

import java.util.UUID;

public class Student { 
    private UUID uuid;
    private String name;
    private String email;
    private double GPA;

    public Student(UUID uuid, String name, String email, double GPA) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.GPA = GPA;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public double getGPA() {
        return this.GPA;
    }

    @Override
    public String toString() {
        String UUID_STRING = uuid.toString();

        return String.format("%s\n%s\n%s\n%.2f", UUID_STRING, this.name, this.email, this.GPA);
    }
}