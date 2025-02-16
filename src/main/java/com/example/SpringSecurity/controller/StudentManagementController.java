package com.example.SpringSecurity.controller;

import com.example.SpringSecurity.model.Student;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagementController {

    // We created a new controller to manage students. This controller is only accessible to users with the role of ADMIN and ADMIN_TRAINEE.
    private static final List<Student> STUDENTS = new ArrayList<>(Arrays.asList(
            new Student(1, "Ravi Kumar"),
            new Student(2, "Amit Singh"),
            new Student(3, "Alok Bhatt")
    ));

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINTRAINEE')")
    public List<Student> getStudents() {
        return STUDENTS;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('student:write')")
    public void registerNewStudent(@RequestBody Student student) {
        System.out.println("registerNewStudent");
        System.out.println(student);
        STUDENTS.add(student);
    }

    @DeleteMapping(path = "{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public void deleteStudent(@PathVariable("studentId") Integer studentId) {
        System.out.println("deleteStudent");
        System.out.println(studentId);

        STUDENTS.removeIf(student -> student.getStudentId().equals(studentId));
    }

    @PutMapping(path = "{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public void updateStudent(@PathVariable("studentId") Integer studentId, @RequestBody Student student) {
        System.out.println("updateStudent");
        System.out.printf("%s %s%n", studentId, student);

    }
}
