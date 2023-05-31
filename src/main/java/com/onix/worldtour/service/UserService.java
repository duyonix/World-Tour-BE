package com.onix.worldtour.service;

import com.onix.worldtour.controller.request.UpdatePasswordRequest;
import com.onix.worldtour.dto.mapper.UserMapper;
import com.onix.worldtour.dto.model.UserDto;
import com.onix.worldtour.exception.ApplicationException;
import com.onix.worldtour.exception.EntityType;
import com.onix.worldtour.exception.ExceptionType;
import com.onix.worldtour.model.Role;
import com.onix.worldtour.model.User;
import com.onix.worldtour.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    public Page<UserDto> getUsers(Integer page, Integer size, String search) {
        log.info("UserService::getUsers execution started");
        Page<UserDto> userDtos;
        try {
            log.debug("UserService::getUsers request parameters page {}, size {}, search {}", page, size, search);
            Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
            Page<User> users = userRepository.findByEmailContainingIgnoreCase(search.replaceAll("\\s+", " ").trim(), pageable);

            userDtos = users.map(UserMapper::toUserDto);
            log.debug("UserService::getUsers received response from database {}", userDtos);
        } catch (Exception e) {
            log.error("UserService::getUsers execution failed with error {}", e.getMessage());
            throw exception(EntityType.USER, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("UserService::getUsers execution completed");
        return userDtos;
    }

    public UserDto getUser(Integer id) {
        log.info("UserService::getUser execution started");
        UserDto userDto;

        log.debug("UserService::getUser request parameters id {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("UserService::getUser execution failed with user not found {}", id);
                    return exception(EntityType.USER, ExceptionType.ENTITY_NOT_FOUND, id.toString());
                });

        userDto = UserMapper.toUserDto(user);
        log.debug("UserService::getUser received response from database {}", userDto);

        log.info("UserService::getUser execution completed");
        return userDto;
    }

    public UserDto updateProfile(UserDto userDto) {
        log.info("UserService::updateProfile execution started");
        UserDto updatedUserDto;

        log.debug("UserService::updateProfile request parameters userDto {}", userDto);
        User user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> {
                    log.error("UserService::updateProfile execution failed with user not found {}", userDto.getEmail());
                    return exception(EntityType.USER, ExceptionType.ENTITY_NOT_FOUND, userDto.getEmail());
                });

        try {
            user.setFirstName(userDto.getFirstName())
                    .setLastName(userDto.getLastName())
                    .setMobileNumber(userDto.getMobileNumber())
                    .setAvatar(userDto.getAvatar())
                    .setModel(userDto.getModel());

            User savedUser = userRepository.save(user);
            updatedUserDto = UserMapper.toUserDto(savedUser);
            log.debug("UserService::updateProfile received response from database {}", updatedUserDto);
        } catch (Exception e) {
            log.error("UserService::updateProfile execution failed with error {}", e.getMessage());
            throw exception(EntityType.USER, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("UserService::updateProfile execution completed");
        return updatedUserDto;
    }

    public UserDto updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        log.info("UserService::updatePassword execution started");
        UserDto updatedUserDto;

        log.debug("UserService::updatePassword request parameters updatePasswordRequest {}", updatePasswordRequest);
        User user = userRepository.findByEmail(updatePasswordRequest.getEmail())
                .orElseThrow(() -> {
                    log.error("UserService::updatePassword execution failed with user not found {}", updatePasswordRequest.getEmail());
                    return exception(EntityType.USER, ExceptionType.ENTITY_NOT_FOUND, updatePasswordRequest.getEmail());
                });

        if (!bCryptPasswordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
            log.error("UserService::updatePassword execution failed with old password not match {}", updatePasswordRequest.getEmail());
            throw exception(EntityType.USER, ExceptionType.NOT_MATCH, updatePasswordRequest.getOldPassword());
        }

        try {
            user.setPassword(bCryptPasswordEncoder.encode(updatePasswordRequest.getNewPassword()));

            User savedUser = userRepository.save(user);
            updatedUserDto = UserMapper.toUserDto(savedUser);
            log.debug("UserService::updatePassword received response from database {}", updatedUserDto);
        } catch (Exception e) {
            log.error("UserService::updatePassword execution failed with error {}", e.getMessage());
            throw exception(EntityType.USER, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("UserService::updatePassword execution completed");
        return updatedUserDto;
    }

    public UserDto updateRole(Integer id, String role) {
        log.info("UserService::updateRole execution started");
        UserDto updatedUserDto;

        log.info("UserService::updateRole request parameters id {}, role {}", id, role);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("UserService::updateRole execution failed with user not found {}", id);
                    return exception(EntityType.USER, ExceptionType.ENTITY_NOT_FOUND, id.toString());
                });

        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            user.setRole(roleEnum);

            User savedUser = userRepository.save(user);
            updatedUserDto = UserMapper.toUserDto(savedUser);
            log.debug("UserService::updateRole received response from database {}", updatedUserDto);
        } catch (Exception e) {
            log.error("UserService::updateRole execution failed with error {}", e.getMessage());
            throw exception(EntityType.USER, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("UserService::updateRole execution completed");
        return updatedUserDto;
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return ApplicationException.throwException(entityType, exceptionType, args);
    }
}