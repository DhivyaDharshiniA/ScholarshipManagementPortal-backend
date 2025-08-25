// package com.examly.springapp.controller;

// import com.examly.springapp.entity.Student;
// import com.examly.springapp.service.StudentService;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import java.util.List;
// import java.util.Optional;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(StudentController.class)
// public class StudentControllerTest {
//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private StudentService studentService;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Test
//     public void testCreateStudent_Valid_ShouldReturn201() throws Exception {
//         Student student = new Student(null, "John Doe", "john.doe@example.com", "1234567890", "Computer Science", 2, 8.5);
//         Student saved = new Student(1L, "John Doe", "john.doe@example.com", "1234567890", "Computer Science", 2, 8.5);
//         Mockito.when(studentService.createStudent(Mockito.any())).thenReturn(saved);
//         mockMvc.perform(post("/api/students")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(student)))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.id").value(1L))
//                 .andExpect(jsonPath("$.name").value("John Doe"));
//     }

//     @Test
//     public void testCreateStudent_InvalidEmail_ShouldReturn400() throws Exception {
//         Student student = new Student(null, "John Doe", "invalid-email", "1234567890", "CS", 2, 8.5);
//         mockMvc.perform(post("/api/students")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(student)))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.message").exists());
//     }

//     @Test
//     public void testGetAllStudents_ShouldReturnList() throws Exception {
//         Student s1 = new Student(1L, "A", "a@b.com", "1234567890", "Dept", 2, 7.1);
//         Student s2 = new Student(2L, "B", "b@b.com", "2234567890", "Dept", 4, 9.0);
//         Mockito.when(studentService.getAllStudents()).thenReturn(List.of(s1, s2));
//         mockMvc.perform(get("/api/students"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$[0].name").value("A"))
//                 .andExpect(jsonPath("$[1].cgpa").value(9.0));
//     }

//     @Test
//     public void testGetStudent_Existing_ShouldReturn200() throws Exception {
//         Student s1 = new Student(1L, "Alice", "alice@example.com", "1234567890", "Dept", 2, 7.1);
//         Mockito.when(studentService.getStudentById(1L)).thenReturn(Optional.of(s1));
//         mockMvc.perform(get("/api/students/1"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath(".name").value("Alice"));
//     }

//     @Test
//     public void testGetStudent_NotFound_ShouldReturn404() throws Exception {
//         Mockito.when(studentService.getStudentById(123L)).thenReturn(Optional.empty());
//         mockMvc.perform(get("/api/students/123"))
//                 .andExpect(status().isNotFound())
//                 .andExpect(jsonPath(".message").value("Student not found"));
//     }
// }
