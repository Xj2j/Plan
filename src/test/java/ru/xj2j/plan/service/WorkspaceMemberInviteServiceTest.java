package ru.xj2j.plan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.xj2j.plan.dto.CreateInviteRequest;
import ru.xj2j.plan.dto.WorkspaceMemberInviteDTO;
import ru.xj2j.plan.exception.CustomBadRequestException;
import ru.xj2j.plan.exception.MyEntityNotFoundException;
import ru.xj2j.plan.mapper.WorkspaceMemberInviteMapper;
import ru.xj2j.plan.model.*;
import ru.xj2j.plan.repository.WorkspaceMemberInviteRepository;
import ru.xj2j.plan.repository.WorkspaceMemberRepository;
import ru.xj2j.plan.repository.WorkspaceRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WorkspaceMemberInviteService Tests")
@ExtendWith(MockitoExtension.class)
class WorkspaceMemberInviteServiceTest {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Mock
    private WorkspaceMemberInviteRepository workspaceMemberInviteRepository;

    @Mock
    private WorkspaceMemberInviteMapper inviteMapper;

    @InjectMocks
    private WorkspaceMemberInviteService workspaceMemberInviteService;

    private User requestingUser;
    private Workspace workspace;
    private WorkspaceMember requester;
    private List<WorkspaceMemberInvite> invites;
    private List<CreateInviteRequest> createInviteRequests;
    private List<WorkspaceMemberInviteDTO> invitesDTOs;

    private static final String SLUG = "test-slug";
    private static final String EMAIL = "test@mail.com";
    private static final String ROLE_MEMBER = "MEMBER";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final Long INVITE_ID = 1L;
    private static final String MESSAGE = "Test message";

    @BeforeEach
    public void setUp() {
        requestingUser = User.builder()
                .id(1L)
                .email(EMAIL)
                .build();
        workspace = Workspace.builder()
                .slug(SLUG)
                .build();
        requester = WorkspaceMember.builder()
                .role(WorkspaceRoleType.MEMBER)
                .build();
        invites = List.of(new WorkspaceMemberInvite());
        invitesDTOs = List.of(new WorkspaceMemberInviteDTO());
        createInviteRequests = List.of(new CreateInviteRequest(EMAIL, ROLE_MEMBER, MESSAGE));
    }

    @Test
    @DisplayName("Test inviteUsers method with valid input")
    void inviteUsers_success() {
        when(workspaceRepository.findBySlug(any())).thenReturn(Optional.of(workspace));
        when(workspaceMemberRepository.findByWorkspace_SlugAndMember_Id(any(), any())).thenReturn(Optional.of(requester));
        when(workspaceMemberRepository.findByWorkspace_SlugAndMember_EmailIn(any(), any())).thenReturn(List.of());
        when(workspaceMemberInviteRepository.saveAll(any())).thenReturn(invites);
        when(inviteMapper.toDtoList(any())).thenReturn(invitesDTOs);

        var result = workspaceMemberInviteService.inviteUsers(SLUG, createInviteRequests, requestingUser);

        verify(workspaceMemberRepository).findByWorkspace_SlugAndMember_EmailIn(SLUG, List.of(EMAIL));
        assertThat(result).isEqualTo(invitesDTOs);
    }

    @Test
    @DisplayName("Test inviteUsers method with non-existing workspace")
    void inviteUsers_WhenWorkspaceNotFound_ShouldThrowException() {
        when(workspaceRepository.findBySlug(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workspaceMemberInviteService.inviteUsers(SLUG, createInviteRequests, requestingUser))
                .isInstanceOf(MyEntityNotFoundException.class)
                .hasMessageContaining("Workspace with slug test-slug not found");

        verify(workspaceRepository).findBySlug(SLUG);
    }

    @Test
    @DisplayName("Test inviteUsers method with invalid requester")
    void inviteUsers_WhenInvalidRequester_ShouldThrowException() {
        when(workspaceRepository.findBySlug(any())).thenReturn(Optional.of(workspace));
        when(workspaceMemberRepository.findByWorkspace_SlugAndMember_Id(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workspaceMemberInviteService.inviteUsers(SLUG, createInviteRequests, requestingUser))
                .isInstanceOf(CustomBadRequestException.class)
                .hasMessageContaining("Invalid requester or workspace");
        verify(workspaceMemberRepository).findByWorkspace_SlugAndMember_Id(SLUG, requestingUser.getId());
    }

    @Test
    @DisplayName("Test inviteUsers method with requester having higher role")
    void inviteUsers_WhenAssignedRoleHigher_ShouldThrowException() {
        createInviteRequests = List.of(new CreateInviteRequest(EMAIL, ROLE_ADMIN, MESSAGE));

        when(workspaceRepository.findBySlug(any())).thenReturn(Optional.of(workspace));
        when(workspaceMemberRepository.findByWorkspace_SlugAndMember_Id(any(), any())).thenReturn(Optional.of(requester));

        assertThatThrownBy(() -> workspaceMemberInviteService.inviteUsers(SLUG, createInviteRequests, requestingUser))
                .isInstanceOf(CustomBadRequestException.class)
                .hasMessageContaining("You cannot assign a role higher than your own role");
    }

    @Test
    @DisplayName("Test inviteUsers method with already existing members")
    void inviteUsers_WhenInvitedAlreadyMember_ShouldThrowException() {
        when(workspaceRepository.findBySlug(any())).thenReturn(Optional.of(workspace));
        when(workspaceMemberRepository.findByWorkspace_SlugAndMember_Id(any(), any())).thenReturn(Optional.of(requester));
        when(workspaceMemberRepository.findByWorkspace_SlugAndMember_EmailIn(any(), any())).thenReturn(List.of(new WorkspaceMember()));

        assertThatThrownBy(() -> workspaceMemberInviteService.inviteUsers(SLUG, createInviteRequests, requestingUser))
                .isInstanceOf(CustomBadRequestException.class)
                .hasMessageContaining("Some users are already members of the workspace");
        verify(workspaceMemberRepository).findByWorkspace_SlugAndMember_EmailIn(SLUG, List.of(EMAIL));
    }


    @Test
    @DisplayName("Test hasInviteToWorkspaceWithSlug when the user has an invitation to the corresponding workspace")
    void hasInviteToWorkspaceWithSlug_UserHasInvite_ReturnsTrue() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(requestingUser, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(workspaceMemberInviteRepository.existsByIdAndEmailAndWorkspace_Slug(INVITE_ID, requestingUser.getEmail(), SLUG)).thenReturn(true);

        boolean result = workspaceMemberInviteService.hasInviteToWorkspaceWithSlug(SLUG, INVITE_ID);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Test hasInviteToWorkspaceWithSlug when the user does not have an invitation to the corresponding workspace")
    void hasInviteToWorkspaceWithSlug_UserDoesNotHaveInvite_ReturnsFalse() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(requestingUser, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(workspaceMemberInviteRepository.existsByIdAndEmailAndWorkspace_Slug(INVITE_ID, requestingUser.getEmail(), SLUG)).thenReturn(false);

        boolean result = workspaceMemberInviteService.hasInviteToWorkspaceWithSlug(SLUG, INVITE_ID);

        assertThat(result).isFalse();
    }
}