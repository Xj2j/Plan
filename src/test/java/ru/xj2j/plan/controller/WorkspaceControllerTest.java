package ru.xj2j.plan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.xj2j.plan.dto.WorkspaceCreateDTO;
import ru.xj2j.plan.dto.WorkspaceDTO;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.service.WorkspaceService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(WorkspaceController.class)
class WorkspaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkspaceService workspaceService;

    @Test
    public void testCreateWorkspace() throws Exception {
        /*WorkspaceCreateDTO workspaceDTO = new WorkspaceCreateDTO();
        workspaceDTO.setName("Test Workspace");
        workspaceDTO.setSlug("test-workspace");
        workspaceDTO.setDescription("This is a test workspace");

        User requestingUser = new User();

        WorkspaceDTO expectedWorkspace = new WorkspaceDTO();
        expectedWorkspace.setId(1L);
        expectedWorkspace.setName(workspaceDTO.getName());
        expectedWorkspace.setSlug(workspaceDTO.getSlug());
        expectedWorkspace.setDescription(workspaceDTO.getDescription());

        when(workspaceService.createWorkspace(workspaceDTO, requestingUser)).thenReturn(expectedWorkspace);

        // When & Then
        mockMvc.perform(post("/api/v1/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(workspaceDTO))
                        .with(user("testuser").password("password")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(expectedWorkspace.getId().intValue())))
                .andExpect(jsonPath("$.name", is(expectedWorkspace.getName())))
                .andExpect(jsonPath("$.slug", is(expectedWorkspace.getSlug())))
                .andExpect(jsonPath("$.description", is(expectedWorkspace.getDescription())));

        //Mockito.verify(workspaceService).createWorkspace(workspaceDTO, requestingUser);*/
    }
}