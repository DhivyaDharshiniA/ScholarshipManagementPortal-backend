// package com.examly.springapp.controller;

// import com.examly.springapp.entity.Scholarship;
// import com.examly.springapp.service.ScholarshipService;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(ScholarshipController.class)
// public class ScholarshipControllerTest {
//     @Autowired
//     private MockMvc mockMvc;
//     @MockBean
//     private ScholarshipService scholarshipService;
//     @Autowired
//     private ObjectMapper objectMapper;

//     @Test
//     public void testCreateScholarship_Valid_ShouldReturn201() throws Exception {
//         Scholarship req = new Scholarship(null, "Merit Scholarship", "Desc", 5000.0, LocalDate.now().plusDays(10), "Criteria", true);
//         Scholarship saved = new Scholarship(1L, "Merit Scholarship", "Desc", 5000.0, req.getDeadline(), "Criteria", true);
//         Mockito.when(scholarshipService.createScholarship(Mockito.any())).thenReturn(saved);
//         mockMvc.perform(post("/api/scholarships")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(req)))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.id").value(1L));
//     }

//     @Test
//     public void testCreateScholarship_DeadlinePast_ShouldReturn400() throws Exception {
//         Scholarship req = new Scholarship(null, "Bad Scholarship", "Desc", 100.0, LocalDate.now().minusDays(1), "Criteria", true);
//         mockMvc.perform(post("/api/scholarships")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(req)))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.message").value("Deadline must be a future date"));
//     }

//     @Test
//     public void testGetAllScholarships_ShouldReturnList() throws Exception {
//         Scholarship s1 = new Scholarship(1L, "S1", "Desc1", 1000.0, LocalDate.now().plusDays(5), "C1", true);
//         Scholarship s2 = new Scholarship(2L, "S2", "Desc2", 2000.0, LocalDate.now().plusDays(7), "C2", false);
//         Mockito.when(scholarshipService.getAllScholarships()).thenReturn(List.of(s1, s2));
//         mockMvc.perform(get("/api/scholarships"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$[0].name").value("S1"));
//     }

//     @Test
//     public void testFilterActiveScholarships_ShouldReturnActiveOnly() throws Exception {
//         Scholarship s1 = new Scholarship(1L, "Active", "Desc", 500.0, LocalDate.now().plusDays(5), "C", true);
//         Mockito.when(scholarshipService.getScholarshipsByActive(true)).thenReturn(List.of(s1));
//         mockMvc.perform(get("/api/scholarships?active=true"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$[0].isActive").value(true)
//                 );
//     }

//     @Test
//     public void testGetScholarshipById_Found_ShouldReturn200() throws Exception {
//         Scholarship s = new Scholarship(5L, "Found", "longdesc", 2500.0, LocalDate.now().plusDays(8), "C", true);
//         Mockito.when(scholarshipService.getScholarshipById(5L)).thenReturn(Optional.of(s));
//         mockMvc.perform(get("/api/scholarships/5"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath(".name").value("Found"));
//     }

//     @Test
//     public void testGetScholarshipById_NotFound_ShouldReturn404() throws Exception {
//         Mockito.when(scholarshipService.getScholarshipById(5L)).thenReturn(Optional.empty());
//         mockMvc.perform(get("/api/scholarships/5"))
//                 .andExpect(status().isNotFound())
//                 .andExpect(jsonPath(".message").value("Scholarship not found"));
//     }
// }
