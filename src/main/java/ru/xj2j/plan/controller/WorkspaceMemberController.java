package ru.xj2j.plan.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.xj2j.plan.dto.WorkspaceMemberDTO;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.service.RoleService;
import ru.xj2j.plan.service.WorkspaceMemberService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceSlug}/members")
@AllArgsConstructor
public class WorkspaceMemberController {

    private final WorkspaceMemberService workspaceMemberService;

    @GetMapping
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.MEMBER)")
    public ResponseEntity<List<WorkspaceMemberDTO>> getAllMembers(@PathVariable String workspaceSlug) {
        List<WorkspaceMemberDTO> members = workspaceMemberService.getAllMembers(workspaceSlug);
        return ResponseEntity.ok(members);
    }

    @PostMapping
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.ADMIN)")
    public ResponseEntity<WorkspaceMemberDTO> addMember(@PathVariable String workspaceSlug,
                                                        @RequestBody WorkspaceMemberDTO memberDto,
                                                        Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        WorkspaceMemberDTO addedMember = workspaceMemberService.addMember(workspaceSlug, memberDto, requestingUser);
        return new ResponseEntity<>(addedMember, HttpStatus.CREATED);
    }

    @PatchMapping("/{memberId}")
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.ADMIN)")
    public ResponseEntity<WorkspaceMemberDTO> updateMemberRole(@PathVariable String workspaceSlug,
                                                           @PathVariable Long memberId,
                                                           @RequestBody WorkspaceMemberDTO memberDto,
                                                           Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        WorkspaceMemberDTO updatedMember = workspaceMemberService.updateMemberRole(workspaceSlug, memberId, memberDto, requestingUser);
        if (updatedMember != null) {
            return new ResponseEntity<>(updatedMember, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.ADMIN)")
    public ResponseEntity<String> deleteMember(@PathVariable("workspaceSlug") String workspaceSlug, @PathVariable("id") Long id, Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        workspaceMemberService.deleteMember(workspaceSlug, id, requestingUser);
        return ResponseEntity.noContent().build();
    }

    /*@PatchMapping("/{memberId}")
    @PreAuthorize("@RoleService.hasAnyRoleByWorkspaceId(#workspaceId, @WorkspaceRole.ADMIN)")
    public WorkspaceMemberDTO updateWorkspaceMemberRole(@PathVariable Long workspaceId, @PathVariable Long memberId, @RequestBody WorkspaceMemberDTO workspaceMemberDTO) throws InviteWorkspaceNotFoundException {
        return workspaceMemberService.updateWorkspaceMemberRole(workspaceId, memberId, workspaceMemberDTO);
    }*/

    /*@GetMapping
    public ResponseEntity<WorkspaceMemberDTO> getWorkspaceMemberUser(@PathVariable("workspaceId") Long workspaceId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        WorkspaceMemberDTO member = workspaceMemberService.findByWorkspaceIdAndMember(workspaceId, user);
        return ResponseEntity.ok(member);
    }*/

    /*@PostMapping
    public ResponseEntity<Void> updateWorkspaceMemberUser(@PathVariable("workspaceId") Long workspaceId, @RequestBody Map<String, Object> viewProps, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        WorkspaceMemberDTO member = workspaceMemberService.findByWorkspaceIdAndMember(workspaceId, user);
        workspaceMemberService.updateViewProps(member, viewProps);
        return ResponseEntity.ok().build();
    }*/
}
