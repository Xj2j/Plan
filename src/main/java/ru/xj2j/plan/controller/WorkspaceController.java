package ru.xj2j.plan.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.xj2j.plan.dto.WorkspaceDTO;
import ru.xj2j.plan.service.WorkspaceService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    private WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<?> getWorkspace(@PathVariable Long workspaceId) {

        WorkspaceDTO workSpace = workspaceService.getWorkspaceById(workspaceId);
        return ResponseEntity.ok(workSpace);
    }

    /*@PostMapping("")
    public ResponseEntity<?> createWorkspace(@Valid @RequestBody WorkspaceDTO workspaceDTO) {
        *//*if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }*//*

        *//*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerUsername = authentication.getName();*//*

        //TODO получение текущего пользователя

        try {
            WorkspaceDTO createdWorkspace = workspaceService.create(workspaceDTO, ownerUsername);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdWorkspace);
            //} catch (WorkspaceAlreadyExistsException e) {
            //return ResponseEntity.status(HttpStatus.CONFLICT).body("The workspace with the slug already exists");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong. Please try again later.");
        }
    }*/

    @PatchMapping("/{workspaceId}")
    public ResponseEntity<?> updateWorkSpace(@PathVariable("workspaceId") Long workspaceId, @Valid @RequestBody WorkspaceDTO workspaceDTO) {

        //TODO проверка владельца

        WorkspaceDTO updatedWorkspace = workspaceService.updateWorkspace(workspaceId, workspaceDTO);
        return ResponseEntity.ok(updatedWorkspace);
    }

    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<?> deleteWorkspace(@PathVariable("workspaceId") Long workspaceId) {

        //TODO проверка владельца

        workspaceService.deleteWorkspace(workspaceId);
        return ResponseEntity.noContent().build();
    }

    /*@GetMapping("")
    public ResponseEntity<?> getUserWorkspaces(Authentication authentication) {

        //TODO получение текущего пользователя

        List<WorkspaceDTO> workSpaces = workspaceService.getUserWorkspaces(authentication.getName());
        return ResponseEntity.ok(workSpaces);
    }*/
}