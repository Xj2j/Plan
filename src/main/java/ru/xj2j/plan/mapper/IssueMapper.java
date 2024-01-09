package ru.xj2j.plan.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.xj2j.plan.dto.IssueCreateDTO;
import ru.xj2j.plan.dto.IssueDTO;
import ru.xj2j.plan.dto.IssueUpdateDTO;
import ru.xj2j.plan.model.Issue;

import java.util.List;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

//WorkspaceMapper.class,
@Mapper(componentModel = "spring", injectionStrategy = CONSTRUCTOR, uses = {UserMapper.class, IssueCommentMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueMapper {

    IssueDTO toDto(Issue issue);
    Issue toEntity(IssueDTO issueDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(IssueUpdateDTO issueUpdateDTO, @MappingTarget Issue issue);

    Issue toEntity(IssueCreateDTO issueCreateDTO);
    Issue toEntity(IssueUpdateDTO issueUpdateDTO);

    List<IssueDTO> toDtoList(List<Issue> issues);
}
