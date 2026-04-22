package models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import structures.Hashtable;
import structures.PriorityQueue;

/**
 * Singleton object which acts as the "Database" to interface with the records file
 */
public class Database {
    private static Database instance;
    private Hashtable<String, StudentTableRecord> students = new Hashtable<>(100_000);
    private PriorityQueue<GPARecord> gpaOrderedStudents = new PriorityQueue<>(100_000);
    private PriorityQueue.CustomComparator<GPARecord> cc = (a, b) -> Double.compare(b.getGPA(), a.getGPA());
    private PriorityQueue<GPARecord> maxGpaStudents = new PriorityQueue<>(cc); //Abel -> Declararation of new priority queue that uses a max heap, used for returning students by descending GPA


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
        maxGpaStudents.add(temp);       // max-heap (new behavior)
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

    public Hashtable<String, StudentTableRecord> getStudentsByDescendingGPA() {
         Hashtable<String, StudentTableRecord> temp = new Hashtable<>(100_000);

        // copy max heap so we don’t destroy original
         PriorityQueue<GPARecord> tempStudents =
         new PriorityQueue<>(this.maxGpaStudents.queueList);

         while (!tempStudents.isEmpty()) {
            GPARecord record = tempStudents.poll();

            temp.put(
            record.getUUID().toString(),
            students.get(record.getUUID().toString())
            );
        }

         return temp;
    } //-> Abel: method that returns a LinkedHashMap that contains student records sorted by GPA in descending order

    public ArrayList<StudentTableRecord> getTopFivePercentStudents() {
        ArrayList<StudentTableRecord> result = new ArrayList<>();

        if (students.isEmpty()) {
            return result;
        }

        PriorityQueue<GPARecord> tempStudents = new PriorityQueue<>(this.maxGpaStudents.queueList, cc);

        int totalStudents = students.size();
        int topCount = (int) Math.ceil(totalStudents * 0.05);

        if (topCount < 1) {
            topCount = 1;
        }

        for (int i = 0; i < topCount && !tempStudents.isEmpty(); i++) {
            GPARecord record = tempStudents.poll();
            StudentTableRecord student = students.get(record.getUUID().toString());
            result.add(student);
        }

        return result;
    }//-> Abel: method that returns an arrayList of the top 5 percent student records sorted by GPA

     public ArrayList<StudentTableRecord> getGPAOrderedStudents() {
        // Hashtable<String, StudentTableRecord> temp = new Hashtable<>(100_000);
        ArrayList<StudentTableRecord> temp = new ArrayList<>(100_000);
        PriorityQueue<GPARecord> tempStudends = new PriorityQueue<>(this.gpaOrderedStudents.queueList);

        while(!tempStudends.isEmpty()) {
            GPARecord record = tempStudends.poll();
            temp.add(students.get(record.getUUID().toString()));
        }

        return temp;
    }

    public boolean doesStudentExistInTop5Percent(StudentTableRecord studentToFind) {
        GPARecord tempRecord = new GPARecord(studentToFind.getUUID(), studentToFind.getGPA());
        return this.maxGpaStudents.queueList.indexOf(tempRecord) > -1;
    }

    public boolean willStudentExistIntTop5Percent(StudentTableRecord studentToFind) {

        ArrayList<StudentTableRecord> fivePercent = getTopFivePercentStudents();

        // Only exist if the students GPA is larger than the list student in top 5
        return studentToFind.getGPA() > fivePercent.getLast().getGPA();
    }

    public void addNewTop5Student(StudentTableRecord newStudent) {
        // remove the last student becuse this function only runs if the new student exists in t5
        this.maxGpaStudents.removeLast();
        this.maxGpaStudents.add(new GPARecord(newStudent.getUUID(), newStudent.getGPA()));
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
        List<StudentTableRecord> values =  new ArrayList<>(this.getGPAOrderedStudents());

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
        maxGpaStudents.remove(new GPARecord(student.getUUID(), student.getGPA()));
    }
}