package ru.xj2j.plan.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.xj2j.plan.dto.CreateInviteRequest;
import ru.xj2j.plan.dto.JoinWorkspaceRequest;
import ru.xj2j.plan.dto.WorkspaceMemberInviteDTO;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.service.WorkspaceMemberInviteService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@AllArgsConstructor
public class WorkspaceMemberInviteController {

    private final WorkspaceMemberInviteService workspaceMemberInviteService;

    @PostMapping("/api/v1/workspaces/{workspaceSlug}/invitations")
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.MEMBER)")
    public List<WorkspaceMemberInviteDTO> inviteUsers(@PathVariable String workspaceSlug, @RequestBody @NotEmpty @Valid List<CreateInviteRequest> createInviteRequests, Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        return workspaceMemberInviteService.inviteUsers(workspaceSlug, createInviteRequests, requestingUser);
    }

    @PostMapping("/api/v1/workspaces/{workspaceSlug}/invitations/{id}")
    @PreAuthorize("@workspaceMemberInviteService.hasInviteToWorkspaceWithSlug(#workspaceSlug, #id)")
    public ResponseEntity<?> joinWorkspace(@PathVariable String workspaceSlug, @PathVariable Long id, @RequestBody JoinWorkspaceRequest request, Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        workspaceMemberInviteService.handleJoinWorkspaceRequest(workspaceSlug, id, request, requestingUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/invitations")
    @PreAuthorize("isAuthenticated()")
    public List<WorkspaceMemberInviteDTO> getInvitationsForUser(Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        return workspaceMemberInviteService.getInvitationsForUser(requestingUser);
    }

    @GetMapping("/api/v1/workspaces/{workspaceSlug}/invitations")
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.ADMIN)")
    public List<WorkspaceMemberInviteDTO> getInvitationsToWorkspace(@PathVariable String workspaceSlug) {
        return workspaceMemberInviteService.getInvitationsToWorkspace(workspaceSlug);
    }

    @DeleteMapping("/api/v1/workspaces/{workspaceSlug}/invitations/{id}")
    @PreAuthorize("@workspaceMemberInviteService.isInvitor(#workspaceSlug, #id) OR @roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.ADMIN)")
    public ResponseEntity<?> deleteInvitation(@PathVariable String workspaceSlug, @PathVariable Long id) {
            workspaceMemberInviteService.deleteInvitation(workspaceSlug, id);
            return ResponseEntity.noContent().build();
    }
}
