package com.mir.payhub.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        log.info("%%%%%%%%%%%%%%%%%% " + "AUTH ENTRY POINT: " +
                request.getMethod() + " " +
                request.getRequestURI() +
                " | " + exception.getMessage());

        response.getWriter().write(
                "{\"status\":401,\"message\":\"" +
                        exception.getMessage() +
                        "\"}"
        );
    }
}
