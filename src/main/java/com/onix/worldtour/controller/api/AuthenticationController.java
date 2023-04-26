package com.onix.worldtour.controller.api;

import com.onix.worldtour.controller.request.AuthenticationRequest;
import com.onix.worldtour.controller.request.RegisterRequest;
import com.onix.worldtour.dto.response.Response;
import com.onix.worldtour.service.AuthenticationService;
import com.onix.worldtour.service.LogoutService;
import com.onix.worldtour.util.ValueMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody @Valid RegisterRequest request) {
        log.info("AuthenticationController::register request body {}", ValueMapper.jsonAsString(request));
        Response<Object> response = Response.ok().setPayload(authenticationService.register(request));
        log.info("AuthenticationController::register response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Response> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        log.info("AuthenticationController::authenticate request body {}", ValueMapper.jsonAsString(request));
        Response<Object> response = Response.ok().setPayload(authenticationService.authenticate(request));
        log.info("AuthenticationController::authenticate response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
