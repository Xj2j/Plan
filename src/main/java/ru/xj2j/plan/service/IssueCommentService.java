package ru.xj2j.plan.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.xj2j.plan.dto.CommentCreateDTO;
import ru.xj2j.plan.dto.CommentDTO;
import ru.xj2j.plan.dto.CommentUpdateDTO;
import ru.xj2j.plan.exception.CustomBadRequestException;
import ru.xj2j.plan.exception.MyEntityNotFoundException;
import ru.xj2j.plan.mapper.IssueCommentMapper;
import ru.xj2j.plan.model.*;
import ru.xj2j.plan.repository.IssueCommentRepository;
import ru.xj2j.plan.repository.IssueRepository;
import ru.xj2j.plan.repository.UserRepository;

@Slf4j
@Service
@AllArgsConstructor
public class IssueCommentService {

    private final IssueCommentRepository issueCommentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final IssueCommentMapper commentMapper;

    @Transactional
    public CommentDTO createComment(Long issueId, CommentCreateDTO commentCreateDTO, User requestingUser) {
        log.info("Creating comment by user: " + requestingUser.getEmail());
        /*Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new MyEntityNotFoundException("User with ID " + memberId + " not found in workspace with ID " + workspaceId));*/

        Issue issue = issueRepository.getReferenceById(issueId);

        /*User actor = userRepository.findById(requestingUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", commentCreateDTO.getActorId()));*/

        IssueComment issueComment = commentMapper.toEntity(commentCreateDTO);
        issueComment.setIssue(issue);
        /*issueComment.setActor(actor);*/

        IssueComment savedComment = issueCommentRepository.save(issueComment);

        log.info("comment created with id: {} by user: {}", savedComment.getId(), requestingUser.getEmail());

        return commentMapper.toDto(savedComment);
    }

    @Transactional
    public CommentDTO updateComment(Long issueId, CommentUpdateDTO commentUpdateDTO, User requestingUser) {
        log.info("Updating comment with id: {} and issueId: {} by user: {}", commentUpdateDTO.getId(), issueId, requestingUser.getEmail());

        /*Issue issue = issueRepository.findByIdAndWorkspaceId(issueId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue", "id", issueId));*/

        IssueComment issueComment = issueCommentRepository.findById(commentUpdateDTO.getId())
                .orElseThrow(() -> new MyEntityNotFoundException("Workspace with ID " + commentUpdateDTO.getId() + " not found"));;

        commentMapper.updateFromDto(commentUpdateDTO, issueComment);

        IssueComment updatedIssueComment = issueCommentRepository.save(issueComment);
        log.info("Comment updated with id: {} and issueId: {} by user: {}", updatedIssueComment.getId(), issueId, requestingUser.getEmail());

        return commentMapper.toDto(updatedIssueComment);
    }

    @Transactional
    public void deleteComment(Long issueId, Long commentId, User requestingUser) {
        log.info("Removal comment with id: {} from issue with id: {} by user: {}", commentId, issueId, requestingUser.getEmail());

        IssueComment comment = issueCommentRepository.findById(commentId)
                .orElseThrow(() -> new MyEntityNotFoundException("Issue comment not found with id: " + commentId));

        issueCommentRepository.delete(comment);

        log.info("Removed comment with id: {} from issue with id: {} by user: {}", commentId, issueId, requestingUser.getEmail());
    }


}
