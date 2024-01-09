package ru.xj2j.plan.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.xj2j.plan.dto.*;
import ru.xj2j.plan.exception.MyEntityNotFoundException;
import ru.xj2j.plan.mapper.IssueMapper;
import ru.xj2j.plan.model.Issue;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.model.Workspace;
import ru.xj2j.plan.model.WorkspaceMember;
import ru.xj2j.plan.repository.IssueRepository;
import ru.xj2j.plan.repository.UserRepository;
import ru.xj2j.plan.repository.WorkspaceMemberRepository;
import ru.xj2j.plan.repository.WorkspaceRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    private final IssueMapper issueMapper;

    public IssueDTO createIssue(IssueCreateDTO issueDTO, String workspaceSlug, User owner) {
        log.info("Creating issue in workspace with slug: {} and by owner: {}", workspaceSlug, owner);

        Workspace workspace = workspaceRepository.findBySlug(workspaceSlug)
                .orElseThrow(() -> new MyEntityNotFoundException("Workspace not found with slug: " + workspaceSlug));

        Set<UserDTO> newAssigneesDTO = issueDTO.getAssignees();
        List<Long> userIds = newAssigneesDTO.stream().map(UserDTO::getId).collect(Collectors.toList());

        Set<User> newAssignees = userRepository.findAllByIdIn(userIds);
        if (newAssignees.size() != userIds.size()) {
            throw new MyEntityNotFoundException("One or more users not found");
        }

        Issue issue = issueMapper.toEntity(issueDTO);
        issue.setWorkspace(workspace);
        issue.setAssignees(newAssignees);
        if (issueDTO.getState() == null) {
            issue.setState(Issue.State.BACKLOG);
        }

        Issue savedIssue = issueRepository.save(issue);
        log.info("Issue created with id: {}", savedIssue.getId());

        return issueMapper.toDto(savedIssue);
    }

    @Transactional
    public IssueDTO updateIssue(Long issueId, String workspaceSlug, IssueUpdateDTO issueUpdateDTO, User requestingUser) {
        log.info("Updating issue with id: {} and workspace slug: {}", issueId, workspaceSlug);

        Issue issue = issueRepository.findByIdAndWorkspaceSlug(issueId, workspaceSlug)
                .orElseThrow(() -> new MyEntityNotFoundException("Issue not found with id: " + issueId));

        issueMapper.updateFromDto(issueUpdateDTO, issue);

        if (issueUpdateDTO.getState().equals(Issue.State.COMPLETED.toString())) {
            //User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.getReferenceById(requestingUser.getId());
            issue.setCompletedBy(user);
            issue.setCompletedAt(LocalDateTime.now());
        }

        Issue updatedIssue = issueRepository.save(issue);
        log.info("Issue updated with id: {}", updatedIssue.getId());

        return issueMapper.toDto(updatedIssue);
    }

    @Transactional
    public IssueDTO addAssignees(String workspaceSlug, Long issueId, List<UserDTO> userDTOs) {
        log.info("Adding assignees to issue with id: {}", issueId);
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new MyEntityNotFoundException("Issue not found with id: " + issueId));
        for (UserDTO userDTO : userDTOs) {
            //User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new MyEntityNotFoundException("User not found with id: " + userDTO.getId()));
            WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspace_SlugAndMember_Id(workspaceSlug, userDTO.getId())
                    .orElseThrow(() -> new MyEntityNotFoundException("Workspace member not found with user id: " + userDTO.getId()));
            issue.getAssignees().add(workspaceMember.getMember());
        }
        Issue updatedIssue = issueRepository.save(issue);
        return issueMapper.toDto(updatedIssue);
    }

    @Transactional
    public IssueDTO removeAssignees(String workspaceSlug, Long issueId, List<Long> userIds) {
        log.info("Removing assignees from issue with id: {}", issueId);
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new MyEntityNotFoundException("Issue not found with id: " + issueId));
        for (Long userId : userIds) {
            //User user = userRepository.findById(userId).orElseThrow(() -> new MyEntityNotFoundException("User not found with id: " + userId));
            WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspace_SlugAndMember_Id(workspaceSlug, userId)
                    .orElseThrow(() -> new MyEntityNotFoundException("Workspace member not found with user id: " + userId));
            issue.getAssignees().remove(workspaceMember.getMember());
        }
        Issue updatedIssue = issueRepository.save(issue);
        return issueMapper.toDto(updatedIssue);
    }

    @Transactional
    public void deleteIssue(Long issueId, String workspaceSlug) {
        log.info("Removal issue with id: {} from workspace with slug: {}", issueId, workspaceSlug);

        Issue issue = issueRepository.findByIdAndWorkspaceSlug(issueId, workspaceSlug)
                .orElseThrow(() -> new MyEntityNotFoundException("Issue not found with id: " + issueId + " in workspace with slug: " + workspaceSlug));

        issueRepository.delete(issue);

        log.info("Issue removed with id: {} from workspace with slug: {}", issueId, workspaceSlug);
    }

    @Transactional(readOnly = true)
    public List<IssueDTO> getAllIssuesByWorkspaceSlug(String workspaceSlug) {
        log.info("Getting all issues for workspace with slug: {}", workspaceSlug);
        return issueMapper.toDtoList(issueRepository.findAllByWorkspaceSlug(workspaceSlug));
    }

    @Transactional(readOnly = true)
    public List<IssueDTO> getIssuesByWorkspaceSlugAndOwner(String workspaceSlug, User requestingUser) {
        log.info("Getting issues for workspace with slug: {} and created by user with id: {}", workspaceSlug, requestingUser.getId());
        return issueMapper.toDtoList(issueRepository.findAllByWorkspaceSlugAndCreatedBy_Id(workspaceSlug, requestingUser.getId()));
    }

    @Transactional(readOnly = true)
    public List<IssueDTO> getIssuesByWorkspaceSlugAndAssignee(String workspaceSlug, User requestingUser) {
        log.info("Getting issues for workspace with slug: {} and assignee with id: {}", workspaceSlug, requestingUser.getId());
        return issueMapper.toDtoList(issueRepository.findAllByWorkspaceSlugAndAssignees_Id(workspaceSlug, requestingUser.getId()));
    }

    @Transactional(readOnly = true)
    public IssueDTO getIssueById(Long issueId, String workspaceSlug) {
        return issueMapper.toDto(issueRepository.findByIdAndWorkspaceSlug(issueId, workspaceSlug)
                .orElseThrow(() -> new MyEntityNotFoundException("Issue not found with id: " + issueId)));
    }

    /*@Transactional
    public IssueDTO updateIssue(Long issueId, Long workspaceId, IssueUpdateDTO issueUpdateDTO) {
        log.info("Updating issue with id: {} and workspaceId: {}", issueId, workspaceId);

        Issue issue = issueRepository.findByIdAndWorkspaceId(issueId, workspaceId)
                .orElseThrow(() -> new MyEntityNotFoundException("Issue not found with id: " + issueId));

        List<User> existingAssignees = issue.getAssignees();
        IssueMapper.INSTANCE.updateFromDto(issueUpdateDTO, issue);
        issue.setAssignees(existingAssignees);

        List<UserDTO> newAssigneesDTO = issueUpdateDTO.getAssignees();
        List<User> newAssignees = new ArrayList<>();

        if (!newAssigneesDTO.isEmpty()) {
            List<Long> userIds = newAssigneesDTO.stream().map(UserDTO::getId).collect(Collectors.toList());
            if (!userRepository.existsByIdIn(userIds)) {
                throw new MyEntityNotFoundException("One or more users not found");
            }
            newAssignees = userRepository.findAllById(userIds);
        }
        issue.getAssignees().addAll(newAssignees);

        if (issueUpdateDTO.getState().equals(Issue.State.COMPLETED.toString())) {

            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            issue.setCompletedBy(currentUser);
            issue.setCompletedAt(LocalDateTime.now());
        }

        Issue updatedIssue = issueRepository.save(issue);
        log.info("Issue updated with id: {}", updatedIssue.getId());

        return IssueMapper.INSTANCE.toDto(updatedIssue);
    }*/
}
