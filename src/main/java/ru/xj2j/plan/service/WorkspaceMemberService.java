package ru.xj2j.plan.service;

import org.springframework.stereotype.Service;
import ru.xj2j.plan.dto.WorkspaceMemberDTO;
import ru.xj2j.plan.exception.MyEntityNotFoundException;
import ru.xj2j.plan.mapper.WorkspaceMapper;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.model.Workspace;
import ru.xj2j.plan.model.WorkspaceMember;
import ru.xj2j.plan.repository.UserRepository;
import ru.xj2j.plan.repository.WorkspaceMemberRepository;
import ru.xj2j.plan.repository.WorkspaceRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkspaceMemberService {

    private WorkspaceMemberRepository workspaceMemberRepository;

    private UserRepository userRepository;

    private WorkspaceRepository workspaceRepository;

    private WorkspaceMapper workspaceMapper;

    private JsonConverter jsonConverter;

    public WorkspaceMemberService(WorkspaceMemberRepository workspaceMemberRepository, UserRepository userRepository, WorkspaceRepository workspaceRepository, WorkspaceMapper workspaceMapper) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
        this.workspaceMapper = workspaceMapper;
    }

    public WorkspaceMember getWorkspaceMemberByWorkspaceIdAndMemberEmail(Long workspaceId, String email) throws MyEntityNotFoundException {
        return workspaceMemberRepository.findByWorkspaceIdAndMemberEmail(workspaceId, email)
                .orElseThrow(() -> new MyEntityNotFoundException("Workspace member not found with id: " + workspaceId + " and email: " + email));
    }

    public List<WorkspaceMember> getWorkspaceMembersByWorkspaceIdAndMemberEmails(Long workspaceId, List<String> emails) {
        return workspaceMemberRepository.findByWorkspaceIdAndMemberEmailIn(workspaceId, emails);
    }

    public List<WorkspaceMemberDTO> getAllMembers(Long workspaceId) {
        List<WorkspaceMember> members = workspaceMemberRepository.findByWorkspaceId(workspaceId);
        return members.stream().map(workspaceMapper::toDto).collect(Collectors.toList());
    }

    public WorkspaceMemberDTO addMember(Long workspaceId, WorkspaceMemberCreateDTO memberDto) {
        Optional<User> userOpt = userRepository.findById(memberDto.getMember().getId());
        if (!userOpt.isPresent()) {
           throw new MyEntityNotFoundException("User not found");
        }

        Optional<Workspace> workspaceOpt = workspaceRepository.findById(workspaceId);
        if (!workspaceOpt.isPresent()) {
            throw new MyEntityNotFoundException("Workspace not found");
        }

        Workspace workspace = workspaceOpt.get();

        WorkspaceMember newMember = workspaceMapper.toEntity(memberDto);
        newMember.setWorkspace(workspace);
        newMember.setMember(userOpt.get());
        WorkspaceMember savedMember = workspaceMemberRepository.save(newMember);
        return workspaceMapper.toDto(savedMember);
    }

    public WorkspaceMemberDTO updateMember(Long workspaceId, Long memberId, WorkspaceMemberDTO memberDto, User user) {
        Optional<WorkspaceMember> memberOpt = workspaceMemberRepository.findByWorkspaceIdAndId(memberId, workspaceId);
        if (!memberOpt.isPresent()) {
            return null;
        }
        WorkspaceMember member = memberOpt.get();

        if (user.getId() == member.getMember().getId()) {
            throw new BadRequestException("You cannot update your own role");
        }

        Optional<WorkspaceMember> requesterOpt = workspaceMemberRepository.findByWorkspaceIdAndMember(workspaceId, user);
        if (!requesterOpt.isPresent() || requesterOpt.get().getRole().compareTo(member.getRole()) > 0) {
            throw new BadRequestException("You cannot update a role that is higher than your own role");
        }

        member.setRole(memberDto.getRole());
        member.getMember().setIsActive(memberDto.getMember().getIsActive());
        WorkspaceMember updatedMember = workspaceMemberRepository.save(member);
        return workspaceMapper.toDto(updatedMember);
    }

    public WorkspaceMemberDTO updateWorkspaceMemberRole(Long workspaceId, Long memberId, WorkspaceMemberDTO workspaceMemberDTO) throws InviteWorkspaceNotFoundException {
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
    }

    /*public boolean deleteMember(Long workspaceId, Long memberId, User user) {
        Optional<WorkspaceMember> memberOpt = workspaceMemberRepository.findByIdAndWorkspaceId(memberId, workspaceId);
        if (!memberOpt.isPresent()) {
            return false;
        }
        WorkspaceMember member = memberOpt.get();

        Optional<WorkspaceMember> requesterOpt = workspaceMemberRepository.findByWorkspaceIdAndMember(workspaceId, user);
        if (!requesterOpt.isPresent() || requesterOpt.get().getRole().compareTo(member.getRole()) > 0) {
            throw new BadRequestException("You cannot delete a member with a higher role than your own");
        }

        workspaceMemberRepository.delete(member);
        return true;
    }*/

    public void deleteWorkspaceMember(Long workspaceId, Long id, User requestingUser) {
        WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspaceIdAndId(id, workspaceId)
                .orElseThrow(() -> new WorkspaceMemberNotFoundException("User role who is deleting the user does not exist"));

        WorkspaceMember requestingWorkspaceMember = workspaceMemberRepository.findByWorkspaceIdAndMember(workspaceId, requestingUser)
                .orElseThrow(() -> new WorkspaceMemberNotFoundException("Requesting user role does not exist"));

        if (requestingWorkspaceMember.getRole().compareTo(workspaceMember.getRole()) < 0) {
            throw new BadRequestException("You cannot delete a role that is higher than your own role");
        }

        issueAssigneeRepository.deleteByWorkspaceSlugAndAssignee(workspaceId, workspaceMember.getMember());
        workspaceMemberRepository.delete(workspaceMember);
    }

    public WorkspaceMemberDTO findByWorkspaceIdAndMember(Long workspaceId, User user) {
        return workspaceMapper.toDto(workspaceMemberRepository.findByWorkspaceIdAndMember(workspaceId, user)
                .orElseThrow(() -> new ForbiddenException("User not a member of workspace")));
    }

}
