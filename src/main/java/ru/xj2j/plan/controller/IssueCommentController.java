package ru.xj2j.plan.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.xj2j.plan.dto.CommentCreateDTO;
import ru.xj2j.plan.dto.CommentDTO;
import ru.xj2j.plan.dto.CommentUpdateDTO;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.service.IssueCommentService;

import javax.validation.Valid;

@RestController
@RequestMapping("/{issueId}/comments")
@AllArgsConstructor
public class IssueCommentController {

    private final IssueCommentService issueCommentService;

    @PostMapping
    @PreAuthorize("@roleService.isIssueOwnerOrAssigneeByIssueId(#issueId)")
    public ResponseEntity<CommentDTO> createComment(@PathVariable Long issueId, @Valid @RequestBody CommentCreateDTO commentDTO, Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        CommentDTO createdComment = issueCommentService.createComment(issueId, commentDTO, requestingUser);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @PatchMapping
    @PreAuthorize("@roleService.isCommentCreator(#id)")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long issueId, @RequestBody Long id, @Valid @RequestBody CommentUpdateDTO commentDTO, Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        CommentDTO updatedComment = issueCommentService.updateComment(issueId, commentDTO, requestingUser);
        if (updatedComment != null) {
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping
    @PreAuthorize("@roleService.isCommentCreator(#id)")
    public ResponseEntity<String> deleteComment(@PathVariable Long issueId, @RequestBody Long id, Authentication authentication) {
        User requestingUser = (User) authentication.getPrincipal();
        issueCommentService.deleteComment(issueId, id, requestingUser);
        return ResponseEntity.noContent().build();
    }

}
