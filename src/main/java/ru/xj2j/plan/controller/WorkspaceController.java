package ru.xj2j.plan.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.xj2j.plan.dto.*;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.service.WorkspaceService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/workspaces")
@AllArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WorkspaceDTO> createWorkspace(@Valid @RequestBody WorkspaceCreateDTO workspaceDTO, Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();

        WorkspaceDTO createdWorkspace = workspaceService.createWorkspace(workspaceDTO, requestingUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWorkspace);
    }

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WorkspaceDTO>> getUserWorkspaces(Authentication authentication) {

        User requestingUser = (User) authentication.getPrincipal();
        List<WorkspaceDTO> workSpaces = workspaceService.getUserWorkspaces(requestingUser);
        return ResponseEntity.ok(workSpaces);
    }

    @PatchMapping("/{workspaceSlug}")
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.ADMIN)")
    public ResponseEntity<WorkspaceDTO> updateWorkSpace(@PathVariable("workspaceSlug") String workspaceSlug, @Valid @RequestBody WorkspaceUpdateDTO workspaceDTO) {

        WorkspaceDTO updatedWorkspace = workspaceService.updateWorkspace(workspaceSlug, workspaceDTO);
        return ResponseEntity.ok(updatedWorkspace);
    }

    @DeleteMapping("/{workspaceSlug}")
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.OWNER)")
    public ResponseEntity<String> deleteWorkspace(@PathVariable("workspaceSlug") String workspaceSlug) {

        workspaceService.deleteWorkspace(workspaceSlug);
        return ResponseEntity.noContent().build();
    }

    /*@GetMapping("/{workspaceId}")
    public ResponseEntity<?> getWorkspace(@PathVariable Long workspaceId) {

        WorkspaceDTO workSpace = workspaceService.getWorkspaceById(workspaceId);
        return ResponseEntity.ok(workSpace);
    }*/


}