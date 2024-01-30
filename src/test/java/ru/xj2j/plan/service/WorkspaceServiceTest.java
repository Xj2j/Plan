package ru.xj2j.plan.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.xj2j.plan.dto.WorkspaceCreateDTO;
import ru.xj2j.plan.dto.WorkspaceDTO;
import ru.xj2j.plan.dto.WorkspaceMemberDTO;
import ru.xj2j.plan.dto.WorkspaceUpdateDTO;
import ru.xj2j.plan.exception.CustomBadRequestException;
import ru.xj2j.plan.exception.MyEntityNotFoundException;
import ru.xj2j.plan.mapper.UserMapper;
import ru.xj2j.plan.mapper.WorkspaceMapper;
import ru.xj2j.plan.mapper.WorkspaceMemberMapper;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.model.Workspace;
import ru.xj2j.plan.model.WorkspaceMember;
import ru.xj2j.plan.repository.WorkspaceMemberRepository;
import ru.xj2j.plan.repository.WorkspaceRepository;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

    private static final String EMAIl = "test@email.com";
    private static final String WORKSPACE_NAME = "Test Workspace";
    private static final String WORKSPACE_SLUG = "test-workspace";
    private static final String WORKSPACE_DESCRIPTION = "Test description";
    private static final String NON_UNIQUE_SLUG = "non-unique-slug";
    private static final String NEW_NAME = "New Name";
    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Mock
    private WorkspaceMapper workspaceMapper;

    @Mock
    private WorkspaceMemberMapper memberMapper;

    @InjectMocks
    private WorkspaceService workspaceService;

    @Test
    @DisplayName("Should create and return the workspaceDTO")
    void createWorkspace_withUniqueSlug_shouldCreateWorkspace() throws NoSuchMethodException {
        WorkspaceCreateDTO workspaceDTO = new WorkspaceCreateDTO(WORKSPACE_NAME, WORKSPACE_SLUG, WORKSPACE_DESCRIPTION);
        User requestingUser = User.builder()
                .email(EMAIl)
                .build();

        Workspace workspace = new Workspace();
        workspace.setSlug(workspaceDTO.getSlug());
        Workspace createdWorkspace = new Workspace();
        createdWorkspace.setSlug(workspaceDTO.getSlug());
        WorkspaceMemberDTO createdMemberDTO = new WorkspaceMemberDTO();
        WorkspaceDTO createdWorkspaceDTO = new WorkspaceDTO();
        createdWorkspaceDTO.setSlug(workspaceDTO.getSlug());

        when(workspaceRepository.existsBySlug(WORKSPACE_SLUG)).thenReturn(false);
        when(workspaceMapper.toEntity(workspaceDTO)).thenReturn(workspace);
        when(workspaceRepository.save(workspace)).thenReturn(createdWorkspace);
        when(workspaceMemberRepository.save(any())).thenReturn(new WorkspaceMember());
        when(memberMapper.toDto(any())).thenReturn(createdMemberDTO);
        when(workspaceMapper.toDto(createdWorkspace)).thenReturn(createdWorkspaceDTO);

        WorkspaceDTO result = workspaceService.createWorkspace(workspaceDTO, requestingUser);

        assertThat(result.getSlug()).isEqualTo(workspaceDTO.getSlug());
        assertThat(result.getMembers()).contains(createdMemberDTO);
    }

    @Test
    @DisplayName("Create workspace should throw CustomBadRequestException when slug is not unique")
    void createWorkspace_withNonUniqueSlug_shouldThrowException() {
        WorkspaceCreateDTO workspaceDTO = new WorkspaceCreateDTO(WORKSPACE_NAME, WORKSPACE_SLUG, WORKSPACE_DESCRIPTION);
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
        Workspace existingWorkspace = new Workspace();
        existingWorkspace.setSlug(WORKSPACE_SLUG);
        WorkspaceUpdateDTO workspaceUpdateDTO = new WorkspaceUpdateDTO();
        workspaceUpdateDTO.setSlug(NON_UNIQUE_SLUG);

        when(workspaceRepository.findBySlug(WORKSPACE_SLUG)).thenReturn(Optional.of(existingWorkspace));
        when(workspaceRepository.existsBySlug(NON_UNIQUE_SLUG)).thenReturn(true);

        assertThatThrownBy(() -> workspaceService.updateWorkspace(WORKSPACE_SLUG, workspaceUpdateDTO))
                .isInstanceOf(CustomBadRequestException.class)
                .hasMessageContaining("New slug must be unique");
        verify(workspaceRepository, times(1)).findBySlug(WORKSPACE_SLUG);
        verify(workspaceRepository, times(1)).existsBySlug(NON_UNIQUE_SLUG);
        verify(workspaceMapper, never()).updateWorkspaceFromDto(any(), any());
        verify(workspaceRepository, never()).save(any());
    }


    @Test
    @DisplayName("Should update and return the workspaceDTO")
    public void testUpdateWorkspace() {
        WorkspaceUpdateDTO workspaceUpdateDTO = WorkspaceUpdateDTO.builder()
                .name(NEW_NAME)
                .build();
        Workspace existingWorkspace = Workspace.builder()
                .slug(WORKSPACE_SLUG)
                .name(WORKSPACE_NAME)
                .description(WORKSPACE_DESCRIPTION)
                .build();
        Workspace modifiedWorkspace = Workspace.builder()
                .slug(WORKSPACE_SLUG)
                .name(NEW_NAME)
                .description(WORKSPACE_DESCRIPTION)
                .build();
        WorkspaceDTO updatedWorkspace = WorkspaceDTO.builder()
                .slug(WORKSPACE_SLUG)
                .name(NEW_NAME)
                .description(WORKSPACE_DESCRIPTION)
                .build();

        when(workspaceRepository.findBySlug(WORKSPACE_SLUG)).thenReturn(Optional.of(existingWorkspace));
        when(workspaceMapper.updateWorkspaceFromDto(workspaceUpdateDTO, existingWorkspace)).thenReturn(modifiedWorkspace);
        when(workspaceRepository.save(modifiedWorkspace)).thenReturn(modifiedWorkspace);
        when(workspaceMapper.toDto(modifiedWorkspace)).thenReturn(updatedWorkspace);

        WorkspaceDTO result = workspaceService.updateWorkspace(WORKSPACE_SLUG, workspaceUpdateDTO);

        assertThat(result).isEqualTo(updatedWorkspace);
    }

}