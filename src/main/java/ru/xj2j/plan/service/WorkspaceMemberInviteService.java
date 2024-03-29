package ru.xj2j.plan.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.xj2j.plan.dto.CreateInviteRequest;
import ru.xj2j.plan.dto.JoinWorkspaceRequest;
import ru.xj2j.plan.dto.WorkspaceMemberInviteDTO;
import ru.xj2j.plan.exception.CustomBadRequestException;
import ru.xj2j.plan.exception.MyEntityNotFoundException;
import ru.xj2j.plan.mapper.WorkspaceMemberInviteMapper;
import ru.xj2j.plan.model.*;
import ru.xj2j.plan.repository.WorkspaceMemberInviteRepository;
import ru.xj2j.plan.repository.WorkspaceMemberRepository;
import ru.xj2j.plan.repository.WorkspaceRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class WorkspaceMemberInviteService {

    private final WorkspaceMemberInviteRepository workspaceMemberInviteRepository;

    private final WorkspaceMemberRepository workspaceMemberRepository;

    private final WorkspaceRepository workspaceRepository;

    private final WorkspaceMemberInviteMapper inviteMapper;

    @Transactional
    public List<WorkspaceMemberInviteDTO> inviteUsers(String workspaceSlug, List<CreateInviteRequest> createInviteRequests, User requestingUser) {

        log.info("Creating invitations to workspace with slug: {}", workspaceSlug);

        Workspace workspace = workspaceRepository.findBySlug(workspaceSlug)
                .orElseThrow(() -> new MyEntityNotFoundException("Workspace with slug " + workspaceSlug + " not found"));

        WorkspaceMember requester = workspaceMemberRepository.findByWorkspace_SlugAndMember_Id(workspaceSlug, requestingUser.getId())
                .orElseThrow(() -> new CustomBadRequestException("Invalid requester or workspace"));

        boolean hasHigherRole = createInviteRequests.stream()
                .anyMatch(req -> !requester.getRole().includes(WorkspaceRoleType.valueOf(req.getRole())));

        if (hasHigherRole) throw new CustomBadRequestException("You cannot assign a role higher than your own role");

        List<String> invitedEmails = createInviteRequests.stream()
                .map(CreateInviteRequest::getEmail)
                .toList();

        List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findByWorkspace_SlugAndMember_EmailIn(workspace.getSlug(), invitedEmails);
        if (!workspaceMembers.isEmpty()) throw new CustomBadRequestException("Some users are already members of the workspace");

        //TODO маппинг
        List<WorkspaceMemberInvite> workspaceMemberInvites = new ArrayList<>();
        for (CreateInviteRequest inviteRequest : createInviteRequests) {
            WorkspaceMemberInvite workspaceMemberInvite = new WorkspaceMemberInvite();
            workspaceMemberInvite.setWorkspace(workspace);
            workspaceMemberInvite.setEmail(inviteRequest.getEmail());
            workspaceMemberInvite.setRole(WorkspaceRoleType.valueOf(inviteRequest.getRole()));
            workspaceMemberInvite.setMessage(inviteRequest.getMessage());
            workspaceMemberInvite.setInvitor(requestingUser);
            workspaceMemberInvites.add(workspaceMemberInvite);
        }

        List<WorkspaceMemberInviteDTO> inviteDTOs = inviteMapper.toDtoList(workspaceMemberInviteRepository.saveAll(workspaceMemberInvites));

        log.info("Invitations to workspace with slug: {} have been created", workspaceSlug);

        return inviteDTOs;
    }

    @Transactional
    public void handleJoinWorkspaceRequest(String workspaceSlug, Long inviteId, JoinWorkspaceRequest request, User user) {

        WorkspaceMemberInvite invite = workspaceMemberInviteRepository.findByIdAndWorkspaceSlug(inviteId, workspaceSlug)
                .orElseThrow(() -> new MyEntityNotFoundException("Invite with id: " + inviteId + " to workspace with slug: " + workspaceSlug + " + not found"));

        if (invite.getRespondedAt() == null) {
            invite.setAccepted(request.isAccepted());
            invite.setRespondedAt(LocalDateTime.now());

            createWorkspaceMember(workspaceSlug, user, invite.getRole());

            /*if (request.isAccepted()) {
                createWorkspaceMember(workspaceSlug, user, invite.getRole());
            } else {
                workspaceMemberInviteRepository.delete(invite);
            }*/
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already responded to the invitation request");
        }
    }

    private void createWorkspaceMember(String workspaceSlug, User user, WorkspaceRoleType role) {
        log.info("Adding member to workspace with slug: {}", workspaceSlug);
        Workspace workspace = workspaceRepository.findBySlug(workspaceSlug)
                .orElseThrow(() -> new MyEntityNotFoundException("Workspace with slug " + workspaceSlug + " not found"));

        WorkspaceMember workspaceMember = new WorkspaceMember();
        workspaceMember.setWorkspace(workspace);
        workspaceMember.setMember(user);
        workspaceMember.setRole(role);

        workspaceMemberRepository.save(workspaceMember);

        log.info("Member added to workspace with slug: {}", workspaceSlug);
    }


    @Transactional(readOnly = true)
    public boolean hasInviteToWorkspaceWithSlug(String workspaceSlug, Long invitationId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Checking for an invitation for a user with an email : {} in a workspace with slug: {}", email, workspaceSlug);

        boolean isPresent = workspaceMemberInviteRepository.existsByIdAndEmailAndWorkspace_Slug(invitationId, email, workspaceSlug);

        if (isPresent) {
            log.info("User with email: {} has an invitation to workspace with slug: {}", email, workspaceSlug);
            return true;
        }
        log.info("User with email: {} does not have an invitation to workspace with slug: {}", email, workspaceSlug);
        return false;
    }

    @Transactional(readOnly = true)
    public boolean isInvitor(String workspaceSlug, Long invitationId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Checking that user with an email : {} is invitor invitations with id: {}", email, invitationId);

        boolean isInvitor = workspaceMemberInviteRepository.existsByIdAndInvitor_EmailAndWorkspace_Slug(invitationId, email, workspaceSlug);

        if (isInvitor) {
            log.info("User with email: {} is inviter of the invitation with id: {}", email, invitationId);
            return true;
        }
        log.info("User with email: {} is not the inviter of the invitation with id: {}", email, invitationId);
        return false;

    }

    @Transactional(readOnly = true)
    public List<WorkspaceMemberInviteDTO> getInvitationsForUser(User requestingUser) {
        log.info("Returning invitations for user with email: {}", requestingUser.getEmail());
        return inviteMapper.toDtoList(workspaceMemberInviteRepository.getByEmail(requestingUser.getEmail()));
    }

    @Transactional(readOnly = true)
    public List<WorkspaceMemberInviteDTO> getInvitationsToWorkspace(String workspaceSlug) {
        log.info("Returning invitations to Workspace with slug: {}", workspaceSlug);
        return inviteMapper.toDtoList(workspaceMemberInviteRepository.getByWorkspace_Slug(workspaceSlug));
    }

    public void deleteInvitation(String workspaceSlug, Long invitationId) {
        log.info("Removal invitation with id: {} to workspace with slug: {}", invitationId, workspaceSlug);

        if (workspaceMemberInviteRepository.existsById(invitationId)) {
            workspaceMemberInviteRepository.deleteById(invitationId);
        } else {
            throw new MyEntityNotFoundException("The Invitation being deleted does not exist");
        }

        log.info("Removed invitation with id: {} to workspace with slug: {}", invitationId, workspaceSlug);
    }
}
