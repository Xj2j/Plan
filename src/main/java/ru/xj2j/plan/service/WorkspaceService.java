package ru.xj2j.plan.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.xj2j.plan.dto.WorkspaceCreateDTO;
import ru.xj2j.plan.dto.WorkspaceDTO;
import ru.xj2j.plan.dto.WorkspaceMemberDTO;
import ru.xj2j.plan.dto.WorkspaceUpdateDTO;
import ru.xj2j.plan.exception.CustomBadRequestException;
import ru.xj2j.plan.exception.MyEntityNotFoundException;
import ru.xj2j.plan.mapper.UserMapper;
import ru.xj2j.plan.mapper.WorkspaceMemberMapper;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.model.Workspace;
import ru.xj2j.plan.model.WorkspaceMember;
import ru.xj2j.plan.model.WorkspaceRoleType;
import ru.xj2j.plan.repository.WorkspaceMemberRepository;
import ru.xj2j.plan.repository.WorkspaceRepository;
import ru.xj2j.plan.mapper.WorkspaceMapper;

import javax.validation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceMapper workspaceMapper;
    private final WorkspaceMemberMapper memberMapper;

    @Transactional(rollbackFor = CustomBadRequestException.class)
    public WorkspaceDTO createWorkspace(WorkspaceCreateDTO workspaceDTO, User requestingUser) {

        log.info("Creating workspace by user: {}", requestingUser.getEmail());

        if (workspaceRepository.existsBySlug(workspaceDTO.getSlug())) {
            throw new CustomBadRequestException("Slug must be unique");
        }

        Workspace workspace = workspaceMapper.toEntity(workspaceDTO);
        Workspace createdWorkspace = workspaceRepository.save(workspace);
        log.info("Workspace created with slug: {} ", createdWorkspace.getSlug());

        WorkspaceMemberDTO createdMember = addOwner(createdWorkspace, requestingUser);
        log.info("Owner added to workspace: {}", createdWorkspace.getSlug());

        WorkspaceDTO createdWorkspaceDTO = workspaceMapper.toDto(createdWorkspace);
        createdWorkspaceDTO.getMembers().add(createdMember);

        return workspaceMapper.toDto(createdWorkspace);
    }

    private WorkspaceMemberDTO addOwner(Workspace createdWorkspace, User requestingUser) {

        log.info("Creating owner member for user: {} in workspace with slug: {} ", requestingUser.getEmail(), createdWorkspace.getSlug());

        WorkspaceMember workspaceMember = new WorkspaceMember();
        workspaceMember.setWorkspace(createdWorkspace);
        workspaceMember.setMember(requestingUser);
        workspaceMember.setRole(WorkspaceRoleType.OWNER);

        return memberMapper.toDto(workspaceMemberRepository.save(workspaceMember));
    }

    @Transactional(rollbackFor = {MyEntityNotFoundException.class, CustomBadRequestException.class})
    public WorkspaceDTO updateWorkspace(String slug, WorkspaceUpdateDTO workspaceDTO) {
        log.info("Updating workspace with slug: {}", slug);

        Workspace workspace = workspaceRepository.findBySlug(slug)
                .orElseThrow(() -> new MyEntityNotFoundException("Workspace not found with slug: " + slug));

        if (workspaceDTO.getSlug() != null && (!(workspaceDTO.getSlug().equals(slug)) && (workspaceRepository.existsBySlug(workspaceDTO.getSlug())))) {
            throw new CustomBadRequestException("New slug must be unique");
        }

        Workspace modifiedWorkspace = workspaceMapper.updateWorkspaceFromDto(workspaceDTO, workspace);

        Workspace updatedWorkspace = workspaceRepository.save(modifiedWorkspace);

        log.info("Workspace updated with slug: {}", updatedWorkspace.getSlug());

        return workspaceMapper.toDto(updatedWorkspace);
    }

    @Transactional(rollbackFor = MyEntityNotFoundException.class)
    public void deleteWorkspace(String slug) {
        log.info("Removal member with slug: {}", slug);

        if (workspaceRepository.existsBySlug(slug)) {
            workspaceRepository.deleteBySlug(slug);
        } else {
            throw new MyEntityNotFoundException("Workspace not found with slug: " + slug);
        }

        /*Workspace workspace = workspaceRepository.findBySlug(slug)
                .orElseThrow(() -> new MyEntityNotFoundException("Workspace not found with slug: " + slug));*/

        //TODO удаление workspaceMembers, удаление issues, удаление issue assignees

        //workspaceRepository.delete(workspace);

        log.info("Workspace removed with slug: {}", slug);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceDTO> getUserWorkspaces(User requestingUser) throws ValidationException {
        log.info("Returning workspaces for user with email: {}", requestingUser.getEmail());

        List<Workspace> workSpaces = workspaceRepository.findAllByMembers_Member_Id(requestingUser.getId());

        //return workSpaces.stream().map(workspaceMapper::toDto).collect(Collectors.toList());
        return workspaceMapper.toDto(workSpaces);
    }

    /*@Transactional(readOnly = true)
    public WorkspaceDTO getWorkspaceById(String slug) {
        return workspaceMapper.toDto(workspaceRepository.findBySlug(slug)
                .orElseThrow(() -> new MyEntityNotFoundException("Workspace not found with slug: " + slug)));
    }*/

}
