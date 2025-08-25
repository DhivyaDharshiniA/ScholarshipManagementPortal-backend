// package com.examly.springapp.controller;

// import com.examly.springapp.entity.*;
// import com.examly.springapp.service.ApplicationService;
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

// @WebMvcTest(ApplicationController.class)
// public class ApplicationControllerTest {
//     @Autowired
//     private MockMvc mockMvc;
//     @MockBean
//     private ApplicationService applicationService;
//     @Autowired
//     private ObjectMapper objectMapper;

//     @Test
//     public void testSubmitApplication_Success() throws Exception {
//         ApplicationController.CreateApplicationRequest req = new ApplicationController.CreateApplicationRequest();
//         req.setStudentId(1L);
//         req.setScholarshipId(2L);
//         req.setDocuments("https://8080-bdcbdbbceebebfafcccdccbdcbfbadaafcebeb.premiumproject.examly.io/transcript.pdf");
//         Application app = new Application(5L, new Student(), new Scholarship(), LocalDate.now(), ApplicationStatus.PENDING, null, "https://8080-bdcbdbbceebebfafcccdccbdcbfbadaafcebeb.premiumproject.examly.io/transcript.pdf");
//         Mockito.when(applicationService.createApplication(Mockito.eq(1L), Mockito.eq(2L), Mockito.anyString())).thenReturn(app);
//         mockMvc.perform(post("/api/applications")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(req)))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.id").value(5L))
//                 .andExpect(jsonPath("$.status").value("PENDING"));
//     }

//     @Test
//     public void testSubmitApplication_StudentNotFound_ShouldReturn400() throws Exception {
//         ApplicationController.CreateApplicationRequest req = new ApplicationController.CreateApplicationRequest();
//         req.setStudentId(404L); req.setScholarshipId(2L); req.setDocuments("");
//         Mockito.when(applicationService.createApplication(Mockito.eq(404L), Mockito.eq(2L), Mockito.anyString())).thenThrow(new IllegalArgumentException("Student not found"));
//         mockMvc.perform(post("/api/applications")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(req)))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.message").value("Student not found"));
//     }

//     @Test
//     public void testGetAllApplications_StatusFilter_ShouldReturnPending() throws Exception {
//         Application app1 = new Application(10L, new Student(), new Scholarship(), LocalDate.now(), ApplicationStatus.PENDING, null, null);
//         Mockito.when(applicationService.getApplicationsByStatus(ApplicationStatus.PENDING)).thenReturn(List.of(app1));
//         mockMvc.perform(get("/api/applications?status=PENDING"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$[0].status").value("PENDING"));
//     }

//     @Test
//     public void testUpdateApplicationStatus_Valid_ShouldUpdate() throws Exception {
//         ApplicationController.UpdateApplicationStatusRequest req = new ApplicationController.UpdateApplicationStatusRequest();
//         req.setStatus("APPROVED");
//         req.setComments("Student meets all criteria. Approved.");
//         Application app = new Application(9L, new Student(), new Scholarship(), LocalDate.now(), ApplicationStatus.APPROVED, "Student meets all criteria. Approved.", null);
//         Mockito.when(applicationService.updateApplicationStatus(9L, ApplicationStatus.APPROVED, "Student meets all criteria. Approved.")).thenReturn(app);
//         mockMvc.perform(put("/api/applications/9/status")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(req)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value("APPROVED"))
//                 .andExpect(jsonPath("$.comments").value("Student meets all criteria. Approved."));
//     }

//     @Test
//     public void testUpdateApplicationStatus_InvalidStatus_ShouldReturn400() throws Exception {
//         ApplicationController.UpdateApplicationStatusRequest req = new ApplicationController.UpdateApplicationStatusRequest();
//         req.setStatus("INVALID"); req.setComments("");
//         mockMvc.perform(put("/api/applications/99/status")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(req)))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.message").value("Status must be PENDING, APPROVED, or REJECTED"));
//     }

//     @Test
//     public void testUpdateApplicationStatus_NotFound_ShouldReturn404() throws Exception {
//         ApplicationController.UpdateApplicationStatusRequest req = new ApplicationController.UpdateApplicationStatusRequest();
//         req.setStatus("REJECTED"); req.setComments("Missing documents");
//         Mockito.when(applicationService.updateApplicationStatus(404L, ApplicationStatus.REJECTED, "Missing documents")).thenThrow(new IllegalArgumentException("Application not found"));
//         mockMvc.perform(put("/api/applications/404/status")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(req)))
//                 .andExpect(status().isNotFound())
//                 .andExpect(jsonPath("$.message").value("Application not found"));
//     }

//     @Test
//     public void testGetApplicationById_Found_ShouldReturnSuccess() throws Exception {
//         Application app = new Application(87L, new Student(), new Scholarship(), LocalDate.now(), ApplicationStatus.PENDING, null, null);
//         Mockito.when(applicationService.getApplicationById(87L)).thenReturn(Optional.of(app));
//         mockMvc.perform(get("/api/applications/87"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id").value(87L));
//     }

//     @Test
//     public void testGetApplicationById_NotFound_ShouldReturn404() throws Exception {
//         Mockito.when(applicationService.getApplicationById(1234L)).thenReturn(Optional.empty());
//         mockMvc.perform(get("/api/applications/1234"))
//                 .andExpect(status().isNotFound())
//                 .andExpect(jsonPath("$.message").value("Application not found"));
//     }
// }
