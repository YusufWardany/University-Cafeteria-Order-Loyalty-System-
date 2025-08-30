package services;

import interfaces.IStudentRepository;
import models.Student;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class StudentManager implements IStudentRepository {
    private Map<String, Student> students;

    public StudentManager() {
        this.students = new HashMap<>();
        initializeSampleStudents();
    }

    private void initializeSampleStudents() {
        // Add some sample students
        registerStudent("S1001", "john_doe", "password123", "John Doe", "john.doe@university.edu");
        registerStudent("S1002", "jane_smith", "password123", "Jane Smith", "jane.smith@university.edu");
        registerStudent("S1003", "bob_johnson", "password123", "Bob Johnson", "bob.johnson@university.edu");
    }

    @Override
    public Student getStudent(String studentId) {
        return students.get(studentId);
    }

    @Override
    public boolean updateStudent(Student student) {
        if (students.containsKey(student.getStudentId())) {
            students.put(student.getStudentId(), student);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public boolean registerStudent(String studentId, String username, String password, String name, String email) {
        if (students.containsKey(studentId)) {
            System.out.println("Student with ID " + studentId + " already exists");
            return false;
        }

        // Check if username is already taken (with null safety)
        boolean usernameExists = students.values().stream()
                .anyMatch(student -> student.getUsername() != null &&
                        student.getUsername().equals(username));

        if (usernameExists) {
            System.out.println("Username " + username + " is already taken");
            return false;
        }

        Student newStudent = new Student(
                "U" + (students.size() + 1001),
                username, password, name, email, studentId
        );

        students.put(studentId, newStudent);
        System.out.println("Student " + name + " registered successfully");
        return true;
    }

    public Student login(String username, String password) {
        System.out.println("Attempting login for username: " + username);

        for (Student student : students.values()) {
            System.out.println("Checking student: " + student.getUsername());
            if (student.getUsername() != null &&
                    student.getUsername().equals(username) &&
                    student.getPassword() != null &&
                    student.getPassword().equals(password)) {
                System.out.println("Login successful for: " + username);
                return student;
            }
        }
        System.out.println("Login failed for: " + username);
        return null;
    }
    public Student login(String username, String password, String role) {
        if (!"Student".equals(role)) {
            return null;
        }
        return login(username, password);
    }

    public void displayAllStudents() {
        System.out.println("\n=== Registered Students ===");
        students.values().forEach(student ->
                System.out.println(student.getStudentId() + ": " + student.getName() + " (" + student.getEmail() + ")"));
    }

    // Helper method to get student by username
    public Student getStudentByUsername(String username) {
        return students.values().stream()
                .filter(student -> student.getUsername() != null &&
                        student.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}