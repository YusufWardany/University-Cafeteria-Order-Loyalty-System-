package interfaces;

import models.Student;

import java.util.ArrayList;

public interface IStudentRepository {
    Student getStudent(String studentId);
    boolean updateStudent(Student student);

    ArrayList<Student> getAllStudents();
}