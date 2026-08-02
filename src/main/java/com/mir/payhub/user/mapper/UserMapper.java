package com.mir.payhub.user.mapper;

import com.mir.payhub.auth.dto.response.UserResponse;
import com.mir.payhub.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(
        target = "roles",
        expression = "java(user.getRoles().stream().map(role -> role.getName().name()).collect(java.util.stream.Collectors.toSet()))"
    )
    UserResponse toResponse(User user);
}