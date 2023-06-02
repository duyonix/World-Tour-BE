package com.onix.worldtour.controller.api;

import com.onix.worldtour.controller.request.RoleRequest;
import com.onix.worldtour.controller.request.UpdatePasswordRequest;
import com.onix.worldtour.dto.model.UserDto;
import com.onix.worldtour.dto.response.Response;
import com.onix.worldtour.model.Role;
import com.onix.worldtour.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Response> getUsers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "role", required = false) String role
    ) {
        log.info("UserController::getUsers page {} size {} search {} role {}", page, size, search, role);
        Page<UserDto> users = userService.getUsers(page, size, search, role);

        Response<Object> response = Response.ok().setPayload(users);
        log.info("UserController::getUsers response {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getUser(@PathVariable("id") Integer id) {
        log.info("UserController::getUser by id {}", id);
        UserDto user = userService.getUser(id);

        Response<Object> response = Response.ok().setPayload(user);
        log.info("UserController::getUser by id {} response {}", id, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<Response> updateProfile(@RequestBody UserDto userDto) {
        log.info("UserController::updateProfile request body {}", userDto);
        UserDto user = userService.updateProfile(userDto);

        Response<Object> response = Response.ok().setPayload(user);
        log.info("UserController::updateProfile response {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-password")
    public ResponseEntity<Response> updatePassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest) {
        log.info("UserController::updatePassword request body {}", updatePasswordRequest);
        UserDto user = userService.updatePassword(updatePasswordRequest);

        Response<Object> response = Response.ok().setPayload(user);
        log.info("UserController::updatePassword response {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/update-role")
    public ResponseEntity<Response> updateRole(@PathVariable("id") Integer id, @RequestBody @Valid RoleRequest roleRequest) {
        String role = roleRequest.getRole();
        log.info("UserController::updateRole by id {} role {}", id, role);
        UserDto user = userService.updateRole(id, role);

        Response<Object> response = Response.ok().setPayload(user);
        log.info("UserController::updateRole by id {} response {}", id, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
