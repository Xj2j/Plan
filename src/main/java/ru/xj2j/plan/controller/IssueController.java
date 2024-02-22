package ru.xj2j.plan.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.xj2j.plan.dto.IssueCreateDTO;
import ru.xj2j.plan.dto.IssueDTO;
import ru.xj2j.plan.dto.IssueUpdateDTO;
import ru.xj2j.plan.dto.UserDTO;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.service.IssueService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/workspaces/{workspaceSlug}/issues")
@AllArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @PostMapping("")
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.MEMBER)")
    public ResponseEntity<IssueDTO> createIssue(@Valid @RequestBody IssueCreateDTO issueDTO, @PathVariable String workspaceSlug, Authentication authentication) {
        User owner = (User) authentication.getPrincipal();
        IssueDTO createdIssue = issueService.createIssue(issueDTO, workspaceSlug, owner);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIssue);
    }

    @PatchMapping("/{issueId}")
    @PreAuthorize("@roleService.isIssueOwnerOrAssigneeByIssueId(#issueId)")
    public ResponseEntity<IssueDTO> updateIssueById(@PathVariable("issueId") Long issueId, @PathVariable String workspaceSlug, @Valid @RequestBody IssueUpdateDTO issueDTO, Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        IssueDTO updatedIssue = issueService.updateIssue(issueId, workspaceSlug, issueDTO, requestingUser);
        return ResponseEntity.ok(updatedIssue);
    }

    @PostMapping("/{issueId}/assignees")
    @PreAuthorize("@roleService.isIssueOwner(#issueId)")
    public ResponseEntity<String> addAssignees(@PathVariable String workspaceSlug, @PathVariable Long issueId, @RequestBody Set<UserDTO> userDTOs) {
        //IssueDTO issue = issueService.addAssignees(workspaceSlug, issueId, userDTOs);
        issueService.addAssignees(workspaceSlug, issueId, userDTOs);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{issueId}/assignees")
    @PreAuthorize("@roleService.isIssueOwner(#issueId)")
    public ResponseEntity<IssueDTO> removeAssignees(@PathVariable Long issueId, @RequestBody Set<Long> userIds) {
        IssueDTO issue = issueService.removeAssignees(issueId, userIds);
        return ResponseEntity.ok(issue);
    }

    @GetMapping("/all")
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.MEMBER)")
    public ResponseEntity<List<IssueDTO>> getWorkspaceIssues(@PathVariable String workspaceSlug) {
        List<IssueDTO> issues = issueService.getAllIssuesByWorkspaceSlug(workspaceSlug);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/myOwn")
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.MEMBER)")
    public ResponseEntity<List<IssueDTO>> getWorkspaceIssuesByOwner(@PathVariable String workspaceSlug, Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        List<IssueDTO> issues = issueService.getIssuesByWorkspaceSlugAndOwner(workspaceSlug, requestingUser);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/my")
    @PreAuthorize("@roleService.hasAnyRoleByWorkspaceSlug(#workspaceSlug, @WorkspaceRole.MEMBER)")
    public ResponseEntity<List<IssueDTO>> getWorkspaceIssuesByAssignee(@PathVariable String workspaceSlug, Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        List<IssueDTO> issues = issueService.getIssuesByWorkspaceSlugAndAssignee(workspaceSlug, requestingUser);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/{issueId}")
    @PreAuthorize("@roleService.isIssueOwnerOrAssigneeByIssueId(#issueId)")
    public ResponseEntity<IssueDTO> getIssueWithDetailsById(@PathVariable Long issueId, @PathVariable String workspaceSlug) {
        IssueDTO issue = issueService.getIssueById(issueId, workspaceSlug);
        return ResponseEntity.ok(issue);
    }

    @DeleteMapping("/{issueId}")
    @PreAuthorize("@roleService.isIssueOwner(#issueId)")
    public ResponseEntity<String> deleteIssueById(@PathVariable("issueId") Long issueId, @PathVariable String workspaceSlug) {
        issueService.deleteIssue(issueId, workspaceSlug);
        return ResponseEntity.ok().build();
    }
}
