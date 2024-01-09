package ru.xj2j.plan.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.xj2j.plan.model.Role;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.model.WorkspaceRoleType;
import ru.xj2j.plan.repository.WorkspaceMemberRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    @DisplayName("hasAnyRoleByWorkspaceSlug returns true when user has role")
    void hasAnyRoleByWorkspaceSlug_UserHasRole_ReturnsTrue() {
        String workspaceSlug = "testSlug";
        Role role = WorkspaceRoleType.ADMIN;
        User user = new User();
        user.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Optional<WorkspaceRoleType> workspaceRoleType = Optional.of(WorkspaceRoleType.ADMIN);
        when(workspaceMemberRepository.findByMember_IdAndWorkspace_Slug(user.getId(), workspaceSlug)).thenReturn(workspaceRoleType);

        boolean result = roleService.hasAnyRoleByWorkspaceSlug(workspaceSlug, role);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("hasAnyRoleByWorkspaceSlug returns false when user does not have role")
    void hasAnyRoleByWorkspaceSlug_UserDoesNotHaveRole_ReturnsFalse() {
        String workspaceSlug = "testSlug";
        Role role = WorkspaceRoleType.ADMIN;
        User user = new User();
        user.setId(1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Optional<WorkspaceRoleType> workspaceRoleType = Optional.of(WorkspaceRoleType.MEMBER);
        when(workspaceMemberRepository.findByMember_IdAndWorkspace_Slug(user.getId(), workspaceSlug)).thenReturn(workspaceRoleType);

        boolean result = roleService.hasAnyRoleByWorkspaceSlug(workspaceSlug, role);

        assertThat(result).isFalse();
    }

    @Test
    void isIssueOwner() {
    }

    @Test
    void isIssueOwnerOrAssigneeByIssueId() {
    }

    @Test
    void isCommentCreator() {
    }
}