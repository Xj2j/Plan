package ru.xj2j.plan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.xj2j.plan.dto.WorkspaceCreateDTO;
import ru.xj2j.plan.dto.WorkspaceDTO;
import ru.xj2j.plan.dto.WorkspaceMemberDTO;
import ru.xj2j.plan.dto.WorkspaceUpdateDTO;
import ru.xj2j.plan.exception.CustomBadRequestException;
import ru.xj2j.plan.exception.MyEntityNotFoundException;
import ru.xj2j.plan.mapper.UserMapper;
import ru.xj2j.plan.mapper.UserMapperImpl;
import ru.xj2j.plan.mapper.WorkspaceMapper;
import ru.xj2j.plan.mapper.WorkspaceMapperImpl;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.model.Workspace;
import ru.xj2j.plan.repository.WorkspaceMemberRepository;
import ru.xj2j.plan.repository.WorkspaceRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@SpringBootTest(classes = {WorkspaceMapperImpl.class, UserMapperImpl.class})
//@SpringJUnitConfig(MapstructConfig.class)
//@ContextConfiguration
@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceMemberService workspaceMemberService;

    @Mock
    private WorkspaceMapper workspaceMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private WorkspaceService workspaceService;

    @Test
    @DisplayName("Create workspace with unique slug")
    void createWorkspace_withUniqueSlug_shouldCreateWorkspace() {
        WorkspaceCreateDTO workspaceDTO = new WorkspaceCreateDTO("Test Workspace", "test-workspace", "Test description");
        User requestingUser = new User();
        requestingUser.setEmail("test@example.com");

        Workspace workspace = new Workspace();
        workspace.setSlug(workspaceDTO.getSlug());
        Workspace createdWorkspace = new Workspace();
        createdWorkspace.setSlug(workspaceDTO.getSlug());
        WorkspaceMemberDTO createdMember = new WorkspaceMemberDTO();
        WorkspaceDTO createdWorkspaceDTO = new WorkspaceDTO();
        createdWorkspaceDTO.setSlug(workspaceDTO.getSlug());

        when(workspaceRepository.existsBySlug(workspaceDTO.getSlug())).thenReturn(false);
        when(workspaceMapper.toEntity(workspaceDTO)).thenReturn(workspace);
        when(workspaceRepository.save(workspace)).thenReturn(createdWorkspace);
        when(workspaceMemberService.addOwner(createdWorkspace, requestingUser)).thenReturn(createdMember);
        when(workspaceMapper.toDto(createdWorkspace)).thenReturn(createdWorkspaceDTO);

        WorkspaceDTO result = workspaceService.createWorkspace(workspaceDTO, requestingUser);

        assertThat(result.getSlug()).isEqualTo(workspaceDTO.getSlug());
        assertThat(result.getMembers()).contains(createdMember);
    }

    @Test
    @DisplayName("Create workspace with non-unique slug")
    void createWorkspace_withNonUniqueSlug_shouldThrowException() {
        WorkspaceCreateDTO workspaceDTO = new WorkspaceCreateDTO("Test Workspace", "test-workspace", "Test description");
        User requestingUser = new User();

        when(workspaceRepository.existsBySlug(workspaceDTO.getSlug())).thenReturn(true);

        assertThatThrownBy(() -> workspaceService.createWorkspace(workspaceDTO, requestingUser))
                .isInstanceOf(CustomBadRequestException.class)
                .hasMessage("Slug must be unique");
    }

    @Test
    @DisplayName("updateWorkspace should throw MyEntityNotFoundException when workspace is not found")
    void updateWorkspace_WhenWorkspaceNotFound_ShouldThrowMyEntityNotFoundException() {
        String slug = "non-existent-slug";
        WorkspaceUpdateDTO workspaceUpdateDTO = new WorkspaceUpdateDTO();

        when(workspaceRepository.findBySlug(slug)).thenReturn(Optional.empty());

        //assertThrows(MyEntityNotFoundException.class, () -> workspaceService.updateWorkspace(slug, workspaceUpdateDTO));
        assertThatThrownBy(() -> workspaceService.updateWorkspace(slug, workspaceUpdateDTO))
                .isInstanceOf(MyEntityNotFoundException.class)
                .hasMessageContaining("Workspace not found with slug: " + slug);

        verify(workspaceRepository, times(1)).findBySlug(slug);
        verify(workspaceRepository, never()).existsBySlug(anyString());
        verify(workspaceMapper, never()).updateWorkspaceFromDto(any(), any());
        verify(workspaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateWorkspace should throw CustomBadRequestException when new slug is not unique")
    void updateWorkspace_WhenNewSlugNotUnique_ShouldThrowCustomBadRequestException() {
        String existingSlug = "existing-slug";
        String newSlug = "non-unique-slug";
        Workspace existingWorkspace = new Workspace();
        existingWorkspace.setSlug(existingSlug);
        WorkspaceUpdateDTO workspaceUpdateDTO = new WorkspaceUpdateDTO();
        workspaceUpdateDTO.setSlug(newSlug);

        when(workspaceRepository.findBySlug(existingSlug)).thenReturn(Optional.of(existingWorkspace));
        when(workspaceRepository.existsBySlug(newSlug)).thenReturn(true);

        //assertThrows(CustomBadRequestException.class, () -> workspaceService.updateWorkspace(existingSlug, workspaceUpdateDTO));

        assertThatThrownBy(() -> workspaceService.updateWorkspace(existingSlug, workspaceUpdateDTO))
                .isInstanceOf(CustomBadRequestException.class)
                .hasMessageContaining("New slug must be unique");
        verify(workspaceRepository, times(1)).findBySlug(existingSlug);
        verify(workspaceRepository, times(1)).existsBySlug(newSlug);
        verify(workspaceMapper, never()).updateWorkspaceFromDto(any(), any());
        verify(workspaceRepository, never()).save(any());
    }

    /*@Test
    @DisplayName("Test updateWorkspace when workspace is successfully updated")
    void testUpdateWorkspace_success() {
        String slug = "existing-slug";
        WorkspaceUpdateDTO workspaceDTO = new WorkspaceUpdateDTO();
        Workspace workspace = new Workspace();
        when(workspaceRepository.findBySlug(slug)).thenReturn(Optional.of(workspace));
        when(workspaceRepository.existsBySlug(workspaceDTO.getSlug())).thenReturn(false);
        Workspace updatedWorkspace = new Workspace();
        when(workspaceRepository.save(workspace)).thenReturn(updatedWorkspace);
        WorkspaceDTO workspaceDTOExpected = new WorkspaceDTO();
        when(workspaceMapper.toDto(updatedWorkspace)).thenReturn(workspaceDTOExpected);

        WorkspaceDTO workspaceDTOResult = workspaceService.updateWorkspace(slug, workspaceDTO);

        assertThat(workspaceDTOResult).isEqualTo(workspaceDTOExpected);

        *//*verify(workspaceRepository, times(1)).findBySlug(slug);
        verify(workspaceRepository, times(1)).existsBySlug(workspaceDTO.getSlug());
        verify(workspaceMapper, times(1)).updateWorkspaceFromDto(workspaceDTO, workspace);
        verify(workspaceRepository, times(1)).save(workspace);*//*
    }

    @Test
    @DisplayName("Update workspace should update and return the workspaceDTO")
    void updateWorkspaceShouldUpdateAndReturnTheWorkspaceDTO() {
        // Arrange
        String slug = "test-slug";
        Workspace workspace = new Workspace();
        workspace.setSlug(slug);
        WorkspaceUpdateDTO workspaceDTO = new WorkspaceUpdateDTO();
        workspaceDTO.setName("New Name");
        workspaceDTO.setSlug("new-slug");
        workspaceDTO.setDescription("New Description");
        WorkspaceDTO workspaceDTOResponse = new WorkspaceDTO();
        workspaceDTOResponse.setName(workspaceDTO.getName());
        workspaceDTOResponse.setSlug(workspaceDTO.getSlug());
        workspaceDTOResponse.setDescription(workspaceDTO.getDescription());

        when(workspaceRepository.findBySlug(slug)).thenReturn(Optional.of(workspace));
        when(workspaceRepository.existsBySlug(workspaceDTO.getSlug())).thenReturn(false);
        when(workspaceMapper.toDto(workspace)).thenReturn(workspaceDTOResponse);
        when(workspaceMapper.updateWorkspaceFromDto(workspace)).then()

        // Act
        WorkspaceDTO result = workspaceService.updateWorkspace(slug, workspaceDTO);

        // Assert
        assertThat(result).isEqualTo(workspaceDTOResponse);
        verify(workspaceMapper).updateWorkspaceFromDto(workspaceDTO, workspace);
        verify(workspaceRepository).save(workspace);
    }*/

    @Test
    @DisplayName("Should update and return the workspaceDTO")
    public void testUpdateWorkspace() {
        String slug = "test-slug";
        WorkspaceUpdateDTO workspaceUpdateDTO = new WorkspaceUpdateDTO();
        workspaceUpdateDTO.setName("New Name");

        Workspace existingWorkspace = new Workspace();
        existingWorkspace.setSlug(slug);
        existingWorkspace.setName("Old Name");
        existingWorkspace.setDescription("Old Description");

        Workspace modifiedWorkspace = new Workspace();
        modifiedWorkspace.setSlug(slug);
        modifiedWorkspace.setName("New Name");
        modifiedWorkspace.setDescription("Old Description");

        WorkspaceDTO updatedWorkspace = new WorkspaceDTO();
        updatedWorkspace.setSlug(slug);
        updatedWorkspace.setName("New Name");
        updatedWorkspace.setDescription("Old Description");

        when(workspaceRepository.findBySlug(slug)).thenReturn(Optional.of(existingWorkspace));
        when(workspaceMapper.updateWorkspaceFromDto(workspaceUpdateDTO, existingWorkspace)).thenReturn(modifiedWorkspace);
        when(workspaceRepository.save(modifiedWorkspace)).thenReturn(modifiedWorkspace);
        when(workspaceMapper.toDto(modifiedWorkspace)).thenReturn(updatedWorkspace);

        WorkspaceDTO result = workspaceService.updateWorkspace(slug, workspaceUpdateDTO);

        assertThat(result).isEqualTo(updatedWorkspace);
    }



    /*@Mock
    private WorkspaceRepository workspaceRepository;
    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;
    @Autowired
    @Spy
    private UserMapper userMapper;
    @Autowired
    @Spy
    private WorkspaceMapper workspaceMapper; // = Mockito.spy(WorkspaceMapper.class);
    @InjectMocks
    private WorkspaceService workspaceService;

    *//*@BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }*//*

    void initMocks() {
        workspaceService = new WorkspaceService(workspaceRepository, workspaceMemberRepository, workspaceMapper, userMapper);
    }


    //init
    @BeforeEach
    void setUp() {
        initMocks();
    }

    @Test
    void createWorkspace() {
        // Создание тестового пользователя
        User owner = new User();
        owner.setEmail("test@example.com");

        // Создание тестового DTO
        WorkspaceCreateDTO workspaceDTO = new WorkspaceCreateDTO();
        workspaceDTO.setName("Test Workspace");
        workspaceDTO.setSlug("test-workspace");
        workspaceDTO.setDescription("This is a test workspace");

        // Создание тестового объекта Workspace
        Workspace workspace = new Workspace();
        workspace.setId(1L);
        workspace.setName(workspaceDTO.getName());
        workspace.setSlug(workspaceDTO.getSlug());
        workspace.setDescription(workspaceDTO.getDescription());

        // Настройка мокитов
        when(workspaceRepository.save(any(Workspace.class))).thenReturn(workspace);

        // Вызов тестируемого метода
        WorkspaceDTO result = workspaceService.createWorkspace(workspaceDTO, owner);

        // Проверка результата
        assertEquals(workspace.getId(), result.getId());
        assertEquals(workspace.getName(), result.getName());
        assertEquals(workspace.getSlug(), result.getSlug());
        assertEquals(workspace.getDescription(), result.getDescription());
        //assertEquals(workspace.getCreatedBy().getEmail(), "test");
    }

    @Test
    void updateWorkspace() {
    }

    @Test
    void deleteWorkspace() {
    }

    @Test
    void getWorkspaceById() {
    }

    @Test
    void getUserWorkspaces() {
    }*/
}