package ru.xj2j.plan.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.xj2j.plan.dto.WorkspaceCreateDTO;
import ru.xj2j.plan.dto.WorkspaceDTO;
import ru.xj2j.plan.dto.WorkspaceUpdateDTO;
import ru.xj2j.plan.model.Workspace;

import java.util.List;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;


@Mapper(componentModel = "spring", injectionStrategy = CONSTRUCTOR, uses = {WorkspaceMemberMapper.class, UserMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkspaceMapper {

    //@Mapping(source = "user", target = "userDTO")
    @Mapping(target = "members", ignore = true)
    WorkspaceDTO toDto(Workspace workspace);
    List<WorkspaceDTO> toDto(List<Workspace> workspaceList);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Workspace toEntity(WorkspaceDTO workspaceDTO);

    Workspace toEntity(WorkspaceCreateDTO workspaceDTO);

    /*@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateWorkspaceFromDto(WorkspaceUpdateDTO dto, @MappingTarget Workspace workspace);*/

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Workspace updateWorkspaceFromDto(WorkspaceUpdateDTO dto, @MappingTarget Workspace workspace);

}
