package ru.xj2j.plan.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.xj2j.plan.dto.WorkspaceDTO;
import ru.xj2j.plan.dto.WorkspaceMemberDTO;
import ru.xj2j.plan.model.Workspace;
import ru.xj2j.plan.model.WorkspaceMember;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(componentModel = "spring", injectionStrategy = CONSTRUCTOR, uses = {UserMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkspaceMemberMapper {

    WorkspaceMemberDTO toDto(WorkspaceMember workspaceMember);
    WorkspaceMember toEntity(WorkspaceMemberDTO workspaceMemberDTO);
}
