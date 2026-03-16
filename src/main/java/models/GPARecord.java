package models;

import java.util.UUID;

public class GPARecord implements Comparable<GPARecord>  {
    private UUID uuid;
    private double GPA;

    public GPARecord(UUID uuid, double GPA) {
        this.uuid = uuid;
        this.GPA = GPA;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public double getGPA() {
        return this.GPA;
    }

    @Override
    public int compareTo(GPARecord o) {
        return Double.compare(this.GPA, o.GPA);
    }
}