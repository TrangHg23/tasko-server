package com.hoangtrang.taskoserver.mapper;

import com.hoangtrang.taskoserver.dto.request.RegisterRequest;
import com.hoangtrang.taskoserver.dto.response.RegisterResponse;
import com.hoangtrang.taskoserver.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toUser(RegisterRequest request);

    RegisterResponse toRegisterResponse(User user);
}
