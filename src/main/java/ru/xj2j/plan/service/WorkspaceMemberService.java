package ru.xj2j.plan.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.xj2j.plan.dto.WorkspaceMemberDTO;
import ru.xj2j.plan.exception.CustomBadRequestException;
import ru.xj2j.plan.exception.MyEntityNotFoundException;
import ru.xj2j.plan.mapper.WorkspaceMemberMapper;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.model.Workspace;
import ru.xj2j.plan.model.WorkspaceMember;
import ru.xj2j.plan.model.WorkspaceRoleType;
import ru.xj2j.plan.repository.UserRepository;
import ru.xj2j.plan.repository.WorkspaceMemberRepository;
import ru.xj2j.plan.repository.WorkspaceRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class WorkspaceMemberService {

    private final WorkspaceMemberRepository workspaceMemberRepository;

    private final UserRepository userRepository;

    private final WorkspaceRepository workspaceRepository;

    private final WorkspaceMemberMapper memberMapper;

    @Transactional
    public WorkspaceMemberDTO addMember(String workspaceSlug, WorkspaceMemberDTO memberDto, User requestingUser) {
        log.info("Adding member to workspace with slug: {}", workspaceSlug);

        User user = userRepository.findById(memberDto.getMember().getId())
                .orElseThrow(() -> new MyEntityNotFoundException("User not found"));

        Workspace workspace = workspaceRepository.findBySlug(workspaceSlug)
                .orElseThrow(() -> new MyEntityNotFoundException("Workspace with slug " + workspaceSlug + " not found"));

        WorkspaceMember requester = workspaceMemberRepository.findByWorkspace_SlugAndMember_Id(workspaceSlug, requestingUser.getId())
                .orElseThrow(() -> new CustomBadRequestException("Invalid requester or workspace"));

        if (!requester.getRole().includes(WorkspaceRoleType.valueOf(memberDto.getRole()))) {
            throw new CustomBadRequestException("You cannot assign a role higher than your own role");
        }

        WorkspaceMember newMember = memberMapper.toEntity(memberDto);
        newMember.setWorkspace(workspace);
        newMember.setMember(user);
        WorkspaceMember savedMember = workspaceMemberRepository.save(newMember);

        log.info("Member added to workspace with slug: {}", workspaceSlug);

        return memberMapper.toDto(savedMember);
    }

    @Transactional
    public WorkspaceMemberDTO updateMemberRole(String workspaceSlug, Long memberId, WorkspaceMemberDTO memberDto, User requestingUser) {
        log.info("Updating role for member with id: {} in workspace with slug: {}", memberId, workspaceSlug);

        WorkspaceMember member = workspaceMemberRepository.findByWorkspace_SlugAndId(workspaceSlug, memberId)
                .orElseThrow(() -> new MyEntityNotFoundException("User with ID " + memberId + " not found in workspace with slug " + workspaceSlug));

        if (Objects.equals(requestingUser.getId(), member.getMember().getId())) {
            throw new CustomBadRequestException("You cannot update your own role");
        }

        WorkspaceMember requester = workspaceMemberRepository.findByWorkspace_SlugAndMember_Id(workspaceSlug, requestingUser.getId())
                .orElseThrow(() -> new CustomBadRequestException("Invalid requester or workspace"));

        if (!requester.getRole().includes(member.getRole())) {
            throw new CustomBadRequestException("You cannot update a role that is higher than your own role");
        }

        member.setRole(WorkspaceRoleType.valueOf(memberDto.getRole()));
        WorkspaceMember updatedMember = workspaceMemberRepository.save(member);

        log.info("Role updated for member with id: {} in workspace with slug: {}", memberId, workspaceSlug);

        return memberMapper.toDto(updatedMember);
    }

    @Transactional
    public void deleteMember(String workspaceSlug, Long memberId, User requestingUser) {
        log.info("Removal member with id: {} from workspace with slug: {}", memberId, workspaceSlug);

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspace_SlugAndId(workspaceSlug, memberId)
                .orElseThrow(() -> new MyEntityNotFoundException("The workspace member being deleted does not exist"));

        WorkspaceMember requestingWorkspaceMember = workspaceMemberRepository.findByWorkspace_SlugAndMember_Id(workspaceSlug, requestingUser.getId())
                .orElseThrow(() -> new MyEntityNotFoundException("Requesting workspace member does not exist"));

        if (!requestingWorkspaceMember.getRole().includes(workspaceMember.getRole())) {
            throw new CustomBadRequestException("You cannot delete a role that is higher than your own role");
        }

        //TODO удалить issues юзера из workspace и его прикрепления к issues
        //issueAssigneeRepository.deleteByWorkspaceIdAndAssignee(workspaceId, workspaceMember.getMember());
        workspaceMemberRepository.delete(workspaceMember);

        log.info("Removed member with id: {} from workspace with slug: {}", memberId, workspaceSlug);
    }

    /*@Transactional(readOnly = true)
    public WorkspaceMember getWorkspaceMemberByWorkspaceIdAndMemberEmail(String workspaceSlug, String email) throws MyEntityNotFoundException {
        return workspaceMemberRepository.findByWorkspaceSlugAndMemberEmail(workspaceSlug, email)
                .orElseThrow(() -> new MyEntityNotFoundException("Workspace member not found with email: " + email + " in workspace with slug:  " + ));
    }*/

    /*@Transactional(readOnly = true)
    public List<WorkspaceMember> getWorkspaceMembersByWorkspaceIdAndMemberEmails(Long workspaceId, List<String> emails) {
        return workspaceMemberRepository.findByWorkspaceIdAndMemberEmailIn(workspaceId, emails);
    }*/

    @Transactional(readOnly = true)
    public List<WorkspaceMemberDTO> getAllMembers(String workspaceSlug) {
        log.info("Returning members from workspace with slug: {}", workspaceSlug);
        List<WorkspaceMember> members = workspaceMemberRepository.findWorkspaceMembersByWorkspaceSlug(workspaceSlug);
        return members.stream().map(memberMapper::toDto).collect(Collectors.toList());
    }

    /*public WorkspaceMemberDTO updateWorkspaceMemberRole(Long workspaceId, Long memberId, WorkspaceMemberDTO workspaceMemberDTO) throws InviteWorkspaceNotFoundException {
        Optional<WorkspaceMember> existingMemberOptional = workspaceMemberRepository.findByWorkspaceIdAndId(memberId, workspaceId);
        WorkspaceMember existingMember = existingMemberOptional.orElseThrow(() -> new WorkspaceMemberNotFoundException("Workspace Member does not exist"));

        if (existingMember.getRole().compareTo(workspaceMemberDTO.getRole()) < 0) {
            throw new BadRequestException("You cannot update the role of a user with a higher role than yours");
        }

        // Delete related ProjectMembers
        List<ProjectMember> projectMembers = projectMemberRepository.findByWorkspaceMember(existingMember);
        projectMemberRepository.deleteAll(projectMembers);

        // Delete related ProjectFavorites
        List<ProjectFavorite> projectFavorites = projectFavoriteRepository.findByWorkspaceMember(existingMember);
        projectFavoriteRepository.deleteAll(projectFavorites);

        existingMember.setRole(workspaceMemberDTO.getRole());
        return workspaceMapper.toDto(workspaceMemberRepository.save(existingMember));
    }*/

    /*public WorkspaceMemberDTO findByWorkspaceIdAndMember(Long workspaceId, User user) {
        return workspaceMapper.toDto(workspaceMemberRepository.findByWorkspaceIdAndMember(workspaceId, user)
                .orElseThrow(() -> new CustomForbiddenException("User not a member of workspace")));
    }*/

}
