package models;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Singleton object which acts as the "Database" to interface with the records file
 */
public class Database {
    private static Database instance;
    private Hashtable<String, StudentTableRecord> students = new Hashtable<>();
    private PriorityQueue<GPARecord> gpaOrderedStudents = new PriorityQueue<>();
    

    private Database() {}

    /**
     * Returns the instance of the database singleton
     */
    public static Database getInstance() {
        if(instance != null) return instance;

        Database.instance = new Database();
        return Database.instance;
    }

    /**
     * Creates a new student record
     */
    public void createStudent(StudentTableRecord student) {
        students.put(student.getUUID().toString(), student);
        GPARecord temp = new GPARecord(student.getUUID(), student.getGPA());
        gpaOrderedStudents.add(temp);
    }

    /**
     * Returns all the studens
     */
    public Hashtable<String, StudentTableRecord> getStudents() {
        return this.students;
    }

    /**
     * Returns students ordered by GPA. Asc order.
     */
    public LinkedHashMap<String, StudentTableRecord> getGPAOrderedStudents() {
        LinkedHashMap<String, StudentTableRecord> temp = new LinkedHashMap<>();
        PriorityQueue<GPARecord> tempStudends = new PriorityQueue<>(this.gpaOrderedStudents);

        while(!tempStudends.isEmpty()) {
            GPARecord record = tempStudends.poll();
            temp.put(record.getUUID().toString(), students.get(record.getUUID().toString()));
        }

        return temp;
    }

    /**
     * Returns a specific student or null
     */
    public Student getStudentById(String uuid) {
        return this.students.get(uuid);
    }

    /**
     * Returns a hash table of students by name
     */
    public Hashtable<String, StudentTableRecord> getStudentsByName(String name) {
        Hashtable<String, StudentTableRecord> result = new Hashtable<String, StudentTableRecord>();

        for(Map.Entry<String, StudentTableRecord> entry : students.entrySet()) {
            if(entry.getValue().getName().equals(name)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    /**
     * Returns list of students as an array
     */
    public StudentTableRecord[] getStudentsAsArray() {
        StudentTableRecord values[] =  (StudentTableRecord[])(this.students.values().toArray());

        return values;
    }

    /**
     * Returns a list of students as a list
     */
    public List<StudentTableRecord> getStudentsAsList() {
        List<StudentTableRecord> values =  new ArrayList<StudentTableRecord>(this.students.values());

        return values;
    }

    /**
     * Returns a List of students sorted by GPA
     */
    public List<StudentTableRecord> getStudentsSortedByGpaAsList() {
        List<StudentTableRecord> values =  new ArrayList<StudentTableRecord>(this.getGPAOrderedStudents().values());

        return values;
    }
}