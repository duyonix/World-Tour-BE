package com.onix.worldtour.dto.mapper;

import com.onix.worldtour.dto.model.UserDto;
import com.onix.worldtour.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setMobileNumber(user.getMobileNumber())
                .setAvatar(user.getAvatar())
                .setModel(user.getModel())
                .setRole(user.getRole());
    }
}
