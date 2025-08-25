package com.devmuyiwa.taskify.user;

import com.devmuyiwa.taskify.auth.util.AuthUser;
import com.devmuyiwa.taskify.common.dto.ApiSuccessResponse;
import com.devmuyiwa.taskify.common.filter.RequestIdFilter;
import com.devmuyiwa.taskify.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user management")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Get current user information",
            description = "Retrieves information about the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ApiSuccessResponse<UserResponse> getCurrentUser(
            @Parameter(description = "Current user extracted from JWT token")
            @AuthenticationPrincipal AuthUser authUser,
            HttpServletRequest httpRequest) {

        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);
        UserResponse userResponse = userService.getCurrentUser(authUser.id());
        
        return ApiSuccessResponse.<UserResponse>builder()
                .message("User information retrieved successfully")
                .data(userResponse)
                .requestId(requestId)
                .build();
    }
}
