package ru.xj2j.plan.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.xj2j.plan.dto.WorkspaceMemberDTO;
import ru.xj2j.plan.dto.WorkspaceMemberInviteDTO;
import ru.xj2j.plan.model.WorkspaceMember;
import ru.xj2j.plan.model.WorkspaceMemberInvite;

import java.util.List;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(componentModel = "spring", injectionStrategy = CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkspaceMemberInviteMapper {

    WorkspaceMemberInviteDTO toDto(WorkspaceMemberInvite workspaceMemberInvite);

    List<WorkspaceMemberInviteDTO> toDtoList(List<WorkspaceMemberInvite> workspaceMemberInvites);
}
