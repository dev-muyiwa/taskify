package com.devmuyiwa.taskify.auth;

import com.devmuyiwa.taskify.auth.dto.req.LoginRequest;
import com.devmuyiwa.taskify.auth.dto.req.RegisterRequest;
import com.devmuyiwa.taskify.auth.dto.res.AuthResponse;
import com.devmuyiwa.taskify.common.dto.ApiSuccessResponse;
import com.devmuyiwa.taskify.common.filter.RequestIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiSuccessResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(
                ApiSuccessResponse
                        .<AuthResponse>builder()
                        .message("User registered successfully.")
                        .data(response)
                        .requestId(requestId)
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiSuccessResponse
                        .<AuthResponse>builder()
                        .message("User logged in successfully.")
                        .data(response)
                        .requestId(requestId)
                        .build()
        );
    }
}
