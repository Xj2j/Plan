package ru.xj2j.plan.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.xj2j.plan.dto.CreateInviteRequestDTO;
import ru.xj2j.plan.dto.WorkspaceMemberInviteDTO;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.service.WorkspaceMemberInviteService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/workspaces")
public class WorkspaceMemberInviteController {

    private final WorkspaceMemberInviteService workspaceMemberInviteService;

    @PostMapping("/{workspaceSlug}/invites")
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.MEMBER)")
    public List<WorkspaceMemberInviteDTO> inviteUsers(@PathVariable String workspaceSlug, @NotEmpty @Valid List<CreateInviteRequestDTO> createInviteRequestDTOs, Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        return workspaceMemberInviteService.inviteUsers(workspaceSlug, createInviteRequestDTOs, requestingUser);
    }

    @PostMapping("/{workspaceSlug}/invites/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> joinWorkspace(@PathVariable String workspaceSlug, @PathVariable("id") Long id, Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        return workspaceMemberInviteService.handleJoinWorkspaceRequest(workspaceSlug, id, requestingUser);
    }

    @GetMapping("/{workspaceSlug}/invites")
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.ADMIN)")
    public List<CreateInviteRequestDTO> getWorkspaceInvites(@PathVariable String workspaceSlug) {
        return workspaceMemberInviteService.getWorkspaceInvites(workspaceSlug);
    }

    @GetMapping("/invites")
    @PreAuthorize("isAuthenticated()")
    public List<CreateInviteRequestDTO> getUserInvites(Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        return workspaceMemberInviteService.getUserInvites(requestingUser);
    }

    @DeleteMapping("/{workspaceSlug}/invites/{id}")
    public ResponseEntity<?> deleteInvitation(@PathVariable String workspaceSlug, @PathVariable Long id) {
            workspaceMemberInviteService.deleteInvitation(workspaceSlug, id);
            return ResponseEntity.noContent().build();
    }
}
