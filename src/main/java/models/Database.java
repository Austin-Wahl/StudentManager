package models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import structures.Hashtable;

/**
 * Singleton object which acts as the "Database" to interface with the records file
 */
public class Database {
    private static Database instance;
    private Hashtable<String, StudentTableRecord> students = new Hashtable<>(100_000);
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
     * Creates a new student record
     */
    public StudentTableRecord createStudent(Student student) {
        StudentTableRecord str = new StudentTableRecord(student.getUUID(), student.getName(), student.getEmail(), student.getGPA());
        students.put(student.getUUID().toString(), str);

        GPARecord temp = new GPARecord(student.getUUID(), student.getGPA());
        gpaOrderedStudents.add(temp);

        return str;
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
    public Hashtable<String, StudentTableRecord> getGPAOrderedStudents() {
        Hashtable<String, StudentTableRecord> temp = new Hashtable<>(100_000);
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
        List<StudentTableRecord> values =  new ArrayList<>(this.students.values());

        return values;
    }

    /**
     * Returns a List of students sorted by GPA
     */
    public List<StudentTableRecord> getStudentsSortedByGpaAsList() {
        List<StudentTableRecord> values =  new ArrayList<>(this.getGPAOrderedStudents().values());

        return values;
    }
    
    /**
     * Saves data to the file on disk
     */
    public boolean save(String fp) throws IOException {        
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fp)));

        students.forEach((key, value) -> {
            try {
                bw.write(value.toString());
                bw.write("\n");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        });

        bw.flush();
        bw.close();

        return true;
    }

    /**
     * Updates the memory buffers with the new data
     */
    public void UpdateStudent(StudentTableRecord student) {

        StudentTableRecord orignialRecord = students.get(student.getUUID().toString());
        GPARecord originalGPA = new GPARecord(orignialRecord.getUUID(), orignialRecord.getGPA());
        GPARecord newGPA = new GPARecord(student.getUUID(), student.getGPA());

        gpaOrderedStudents.remove(originalGPA);
        gpaOrderedStudents.add(newGPA);

        students.put(student.getUUID().toString(), student);
    }

    /**
     * Deletes a student from the memory buffers
     */
    public void DeleteStudent(StudentTableRecord student) {
        students.remove(student.getUUID().toString());
        gpaOrderedStudents.remove(new GPARecord(student.getUUID(), student.getGPA()));
    }
}