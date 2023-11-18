package ru.xj2j.plan.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.xj2j.plan.dto.WorkspaceDTO;
import ru.xj2j.plan.model.Workspace;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {

    WorkspaceMapper INSTANCE = Mappers.getMapper(WorkspaceMapper.class);

    //@Mapping(source = "user", target = "userDTO")
    WorkspaceDTO toDto(Workspace workspace);
    Workspace toEntity(WorkspaceDTO workspaceDTO);

}
