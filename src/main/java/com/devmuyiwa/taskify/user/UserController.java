package com.devmuyiwa.taskify.user;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user management")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

//    @GetMapping("/me")
//    @Operation(
//            summary = "Get current user information",
//            description = "Retrieves information about the currently authenticated user"
//    )
//    public ApiSuccessResponse<UserResponse> getCurrentUser(
//            @Parameter(description = "Current user ID extracted from JWT token")
//            @CurrentUserId UUID userId,
//            HttpServletRequest httpRequest) {
//
//        String requestId = (String) httpRequest.getAttribute(RequestIdFilter.REQUEST_ID_HEADER);
//        UserResponse userResponse = userService.getCurrentUser(userId);
//
//        return ApiSuccessResponse.<UserResponse>builder()
//                .message("User information retrieved successfully")
//                .data(userResponse)
//                .requestId(requestId)
//                .build();
//    }
}
