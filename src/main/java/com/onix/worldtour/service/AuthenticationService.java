package com.onix.worldtour.service;

import com.onix.worldtour.controller.request.AuthenticationRequest;
import com.onix.worldtour.controller.request.RegisterRequest;
import com.onix.worldtour.dto.mapper.UserMapper;
import com.onix.worldtour.dto.model.AuthenticationDto;
import com.onix.worldtour.dto.model.UserDto;
import com.onix.worldtour.exception.ApplicationException;
import com.onix.worldtour.exception.EntityType;
import com.onix.worldtour.exception.ExceptionType;
import com.onix.worldtour.model.Role;
import com.onix.worldtour.model.Token;
import com.onix.worldtour.model.TokenType;
import com.onix.worldtour.model.User;
import com.onix.worldtour.repository.TokenRepository;
import com.onix.worldtour.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final ModelMapper modelMapper;

    public UserDto register(RegisterRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            User newUser = new User()
                    .setEmail(request.getEmail())
                    .setPassword(bCryptPasswordEncoder.encode(request.getPassword()))
                    .setFirstName(request.getFirstName())
                    .setLastName(request.getLastName())
                    .setMobileNumber(request.getMobileNumber())
                    .setRole(Role.USER);
            User savedUser = userRepository.save(newUser);
            String jwtToken = jwtService.generateToken(newUser);
            saveUserToken(newUser, jwtToken);
            return UserMapper.toUserDto(savedUser);
        }
        throw exception(EntityType.USER, ExceptionType.DUPLICATE_ENTITY, request.getEmail());
    }

    public AuthenticationDto authenticate(AuthenticationRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent()) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            String jwtToken = jwtService.generateToken(user.get());
            boolean firstLogin = user.get().getFirstLogin();
            if (firstLogin) {
                user.get().setFirstLogin(false);
                userRepository.save(user.get());
            }
            saveUserToken(user.get(), jwtToken);
            return new AuthenticationDto()
                    .setToken(jwtToken)
                    .setUser(UserMapper.toUserDto(user.get()))
                    .setFirstLogin(firstLogin);
        }
        throw exception(EntityType.USER, ExceptionType.ENTITY_NOT_FOUND, request.getEmail());
    }

    public UserDto findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return modelMapper.map(user.get(), UserDto.class);
        }
        throw exception(EntityType.USER, ExceptionType.ENTITY_NOT_FOUND, email);
    }

    public UserDto updateProfile(UserDto userDto) {
        Optional<User> user = userRepository.findByEmail(userDto.getEmail());
        if (user.isPresent()) {
            User updatedUser = user.get()
                    .setFirstName(userDto.getFirstName())
                    .setLastName(userDto.getLastName())
                    .setMobileNumber(userDto.getMobileNumber())
                    .setModel(userDto.getModel());
            User savedUser = userRepository.save(updatedUser);
            return UserMapper.toUserDto(savedUser);
        }
        throw exception(EntityType.USER, ExceptionType.ENTITY_NOT_FOUND, userDto.getEmail());
    }

    public UserDto changePassword(UserDto userDto, String newPassword) {
        Optional<User> user = userRepository.findByEmail(userDto.getEmail());
        if (user.isPresent()) {
            User updatedUser = user.get()
                    .setPassword(bCryptPasswordEncoder.encode(newPassword));
            User savedUser = userRepository.save(updatedUser);
            return UserMapper.toUserDto(savedUser);
        }
        throw exception(EntityType.USER, ExceptionType.ENTITY_NOT_FOUND, userDto.getEmail());
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = new Token()
                .setUser(user)
                .setToken(jwtToken)
                .setTokenType(TokenType.BEARER)
                .setRevoked(false)
                .setExpired(false);
        tokenRepository.save(token);
    }

    private void revokeUserToken(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return ApplicationException.throwException(entityType, exceptionType, args);
    }
}
