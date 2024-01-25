package ru.xj2j.plan.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.xj2j.plan.dto.CreateInviteRequestDTO;
import ru.xj2j.plan.dto.WorkspaceMemberInviteDTO;
import ru.xj2j.plan.exception.CustomBadRequestException;
import ru.xj2j.plan.exception.MyEntityNotFoundException;
import ru.xj2j.plan.mapper.WorkspaceMemberInviteMapper;
import ru.xj2j.plan.model.*;
import ru.xj2j.plan.repository.WorkspaceMemberInviteRepository;
import ru.xj2j.plan.repository.WorkspaceMemberRepository;
import ru.xj2j.plan.repository.WorkspaceRepository;

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
    public List<WorkspaceMemberInviteDTO> inviteUsers(String workspaceSlug, List<CreateInviteRequestDTO> createInviteRequestDTOs, User requestingUser) {

        log.info("Creating invitations to workspace with slug: {}", workspaceSlug);

        Workspace workspace = workspaceRepository.findBySlug(workspaceSlug)
                .orElseThrow(() -> new MyEntityNotFoundException("Workspace with slug " + workspaceSlug + " not found"));

        WorkspaceMember requester = workspaceMemberRepository.findByWorkspace_SlugAndMember_Id(workspaceSlug, requestingUser.getId())
                .orElseThrow(() -> new CustomBadRequestException("Invalid requester or workspace"));

        boolean hasHigherRole = createInviteRequestDTOs.stream()
                .anyMatch(req -> !requester.getRole().includes(WorkspaceRoleType.valueOf(req.getRole())));

        if (hasHigherRole) throw new CustomBadRequestException("You cannot assign a role higher than your own role");

        List<String> invitedEmails = createInviteRequestDTOs.stream()
                .map(CreateInviteRequestDTO::getEmail)
                .toList();

        List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findByWorkspaceSlugAndMember_EmailIn(workspace.getSlug(), invitedEmails);
        if (!workspaceMembers.isEmpty()) throw new CustomBadRequestException("Some users are already members of the workspace");

        //TODO маппинг
        List<WorkspaceMemberInvite> workspaceMemberInvites = new ArrayList<>();
        for (CreateInviteRequestDTO inviteRequest : createInviteRequestDTOs) {
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


}
