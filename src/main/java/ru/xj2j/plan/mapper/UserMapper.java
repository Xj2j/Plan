package ru.xj2j.plan.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.xj2j.plan.dto.UserDTO;
import ru.xj2j.plan.model.User;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(componentModel = "spring", injectionStrategy = CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    //UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDTO toDto(User user);
    User toEntity(UserDTO userDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserDTO dto, @MappingTarget User user);
}
