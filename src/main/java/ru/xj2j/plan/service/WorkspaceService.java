package ru.xj2j.plan.service;

import org.springframework.stereotype.Service;
import ru.xj2j.plan.dto.WorkspaceDTO;
import ru.xj2j.plan.exception.MyEntityAlreadyExistsException;
import ru.xj2j.plan.exception.MyEntityNotFoundException;
import ru.xj2j.plan.mapper.UserMapper;
import ru.xj2j.plan.model.Workspace;
import ru.xj2j.plan.repository.WorkspaceRepository;
import ru.xj2j.plan.mapper.WorkspaceMapper;

import javax.validation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkspaceService {

    private WorkspaceRepository workspaceRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public WorkspaceDTO getWorkspaceById(Long workspaceId) throws MyEntityNotFoundException {
        Optional<Workspace> optionalWorkSpace = workspaceRepository.findById(workspaceId);
        if (optionalWorkSpace.isPresent()) {
            return WorkspaceMapper.INSTANCE.toDto(optionalWorkSpace.get());
        } else {
            throw new MyEntityNotFoundException("Workspace not found with id: " + workspaceId);
        }
    }

    public WorkspaceDTO create(WorkspaceDTO request, String owner) throws ValidationException {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new ValidationException("Workspace name is required");
        }

        if (workspaceRepository.existsBySlug(request.getSlug())) {
            throw new MyEntityAlreadyExistsException("Workspace already exists with slug: " + request.getSlug());
        }

        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setOwner(UserMapper.INSTANCE.toEntity(request.getOwner()));
        workspaceRepository.save(workspace);

        return WorkspaceMapper.INSTANCE.toDto(workspace);
    }

    public WorkspaceDTO updateWorkspace(Long workspaceId, WorkspaceDTO workSpaceDTO) throws MyEntityNotFoundException {
        Optional<Workspace> optionalWorkSpace = workspaceRepository.findById(workspaceId);
        if (optionalWorkSpace.isPresent()) {
            Workspace workspace = optionalWorkSpace.get();
            workspace.setName(workSpaceDTO.getName());
            workspace.setDescription(workSpaceDTO.getDescription());
            workspaceRepository.save(workspace);

            return WorkspaceMapper.INSTANCE.toDto(workspace);
        } else {
            throw new MyEntityNotFoundException("Workspace not found with id: " + workspaceId);
        }
    }

    public void deleteWorkspace(Long workspaceId) throws MyEntityNotFoundException {
        Optional<Workspace> optionalWorkSpace = workspaceRepository.findById(workspaceId);
        if (optionalWorkSpace.isPresent()) {
            Workspace workSpace = optionalWorkSpace.get();
            workspaceRepository.delete(workSpace);
        } else {
            throw new MyEntityNotFoundException("Workspace not found with id: " + workspaceId);
        }
    }

    /*public List<WorkspaceDTO> getUserWorkspaces(String userEmail) throws ValidationException {
        List<Workspace> workSpaces = workspaceRepository.findByUserEmail(userEmail);
        List<WorkspaceDTO> workspaceDTOS = new ArrayList<>();
        for (Workspace workSpace : workSpaces) {
            workspaceDTOS.add(WorkspaceMapper.INSTANCE.toDto(workSpace));
        }

        return workspaceDTOS;
    }*/

}
