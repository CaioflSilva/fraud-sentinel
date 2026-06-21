package com.fraudsentinel.infrastructure.persistence.mapper;

import com.fraudsentinel.domain.user.Role;
import com.fraudsentinel.domain.user.User;
import com.fraudsentinel.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserEntity toEntity(User user);

    default User toDomain(UserEntity entity) {
        return User.reconstitute(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                Role.valueOf(entity.getRole()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}