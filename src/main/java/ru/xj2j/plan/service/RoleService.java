package ru.xj2j.plan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.xj2j.plan.model.*;
import ru.xj2j.plan.repository.IssueCommentRepository;
import ru.xj2j.plan.repository.IssueRepository;
import ru.xj2j.plan.repository.UserRepository;
import ru.xj2j.plan.repository.WorkspaceMemberRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final IssueRepository issueRepository;
    private final IssueCommentRepository issueCommentRepository;

    @Transactional(readOnly = true)
    public boolean hasAnyRoleByWorkspaceSlug(String workspaceSlug, Role... roles) {
        log.info("Checking roles for user in workspace with slug: {}", workspaceSlug);
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<WorkspaceRoleType> workspaceRoleType = workspaceMemberRepository.findByMember_IdAndWorkspace_Slug(user.getId(), workspaceSlug);
        if (workspaceRoleType.isPresent()) {
            for (Role role : roles) {
                if (workspaceRoleType.get().includes(role)) {
                    log.info("User has role: {}", role);
                    return true;
                }
            }
        }
        log.info("User does not have any of the specified roles");
        return false;
    }

    @Transactional(readOnly = true)
    public boolean isIssueOwner(Long issueId) {
        log.info("Checking if user is the owner of issue with id: {}", issueId);
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return issueRepository.existsByCreatedBy_IdAndId(user.getId(), issueId);

        /*boolean isOwner = issueRepository.existsByCreatedBy_IdAndId(user.getId(), issueId);
        if (isOwner) {
            log.info("User is the owner of the issue");
        } else {
            log.info("User is not the owner of the issue");
        }
        return isOwner;*/
    }

    @Transactional(readOnly = true)
    public boolean isIssueOwnerOrAssigneeByIssueId(Long issueId) {
        log.info("Checking if user is the owner or assignee of issue with id: {}", issueId);
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return issueRepository.existsByCreatedByOrAssignee(user.getId(), issueId);

        /*boolean isOwnerOrAssignee = issueRepository.existsByCreatedByOrAssignee(user.getId(), issueId);
        if (isOwnerOrAssignee) {
            log.info("User is the owner or assignee of the issue");
        } else {
            log.info("User is not the owner or assignee of the issue");
        }
        return isOwnerOrAssignee;*/
    }

    @Transactional(readOnly = true)
    public boolean isCommentCreator(Long commentId) {
        log.info("Checking if user is the creator of comment with id: {}", commentId);
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return issueCommentRepository.existsByCreatedBy(user.getId(), commentId);
    }

}
