package ru.xj2j.plan.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.xj2j.plan.dto.UserDTO;
import ru.xj2j.plan.dto.WorkspaceDTO;
import ru.xj2j.plan.model.User;
import ru.xj2j.plan.model.Workspace;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDto(User user);
    User toEntity(UserDTO userDTO);
}
