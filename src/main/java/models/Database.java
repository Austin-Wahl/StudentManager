package models;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Database {
    private static Database instance;
    private Hashtable<String, StudentTableRecord> students = new Hashtable<>();
    private PriorityQueue<GPARecord> gpaOrderedStudents = new PriorityQueue<>();

    private Database() {}

    public static Database getInstance() {
        if(instance != null) return instance;

        Database.instance = new Database();

        return Database.instance;
    }

    public void createStudent(StudentTableRecord student) {
        students.put(student.getUUID().toString(), student);
        GPARecord temp = new GPARecord(student.getUUID(), student.getGPA());
        gpaOrderedStudents.add(temp);
    }

    public Hashtable<String, StudentTableRecord> getStudents() {
        return this.students;
    }

    public PriorityQueue<GPARecord> getGPAOrderedStudents() {
        return this.gpaOrderedStudents;
    }

    public Student getStudentById(String uuid) {
        return this.students.get(uuid);
    }

    public Hashtable<String, StudentTableRecord> getStudentsByName(String name) {
        Hashtable<String, StudentTableRecord> result = new Hashtable<String, StudentTableRecord>();

        for(Map.Entry<String, StudentTableRecord> entry : students.entrySet()) {
            if(entry.getValue().getName().equals(name)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    public StudentTableRecord[] getStudentsAsArray() {
        StudentTableRecord values[] =  (StudentTableRecord[])(this.students.values().toArray());

        return values;
    }

    public List<StudentTableRecord> getStudentsAsList() {
        List<StudentTableRecord> values =  new ArrayList<StudentTableRecord>(this.students.values());

        return values;
    }
}