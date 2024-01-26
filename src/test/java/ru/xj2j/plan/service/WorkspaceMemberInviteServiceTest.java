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
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.repository.WorkspaceMemberInviteRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WorkspaceMemberInviteServiceTest {

    @Mock
    private WorkspaceMemberInviteRepository workspaceMemberInviteRepository;

    @InjectMocks
    private WorkspaceMemberInviteService workspaceMemberInviteService;

    @Test
    @DisplayName("hasInviteToWorkspaceWithSlug return true when the user has an invitation to the corresponding workspace")
    void hasInviteToWorkspaceWithSlug_UserHasInvite_ReturnsTrue() {
        var slug = "testSlug";
        var inviteId = 1L;
        User user = new User();
        user.setEmail("testEmail");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(workspaceMemberInviteRepository.existsByIdAndEmailAndWorkspaceSlug(inviteId, user.getEmail(), slug)).thenReturn(true);

        boolean result = workspaceMemberInviteService.hasInviteToWorkspaceWithSlug(slug, inviteId);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("hasInviteToWorkspaceWithSlug return false when the user does not have an invitation to the corresponding workspace")
    void hasInviteToWorkspaceWithSlug_UserDoesNotHaveInvite_ReturnsFalse() {
        var slug = "testSlug";
        var inviteId = 1L;
        User user = new User();
        user.setEmail("testEmail");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(workspaceMemberInviteRepository.existsByIdAndEmailAndWorkspaceSlug(inviteId, user.getEmail(), slug)).thenReturn(false);

        boolean result = workspaceMemberInviteService.hasInviteToWorkspaceWithSlug(slug, inviteId);

        assertThat(result).isFalse();
    }
}