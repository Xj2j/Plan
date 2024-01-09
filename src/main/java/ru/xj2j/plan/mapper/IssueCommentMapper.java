package ru.xj2j.plan.mapper;

import org.mapstruct.*;
import ru.xj2j.plan.dto.*;
import ru.xj2j.plan.model.Issue;
import ru.xj2j.plan.model.IssueComment;

import java.util.List;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(componentModel = "spring", injectionStrategy = CONSTRUCTOR, uses = {UserMapper.class, IssueMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueCommentMapper {

    CommentDTO toDto(IssueComment issueComment);

    IssueComment toEntity(CommentCreateDTO commentDTO);

    IssueComment toEntity(CommentUpdateDTO commentDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(CommentUpdateDTO commentDTO, @MappingTarget IssueComment comment);

    List<CommentDTO> toDtoList(List<IssueComment> comments);
}
