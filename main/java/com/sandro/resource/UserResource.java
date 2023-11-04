package com.sandro.resource;

import com.sandro.domain.HttpResponse;
import com.sandro.domain.Role;
import com.sandro.domain.User;
import com.sandro.domain.UserPrincipal;
import com.sandro.dto.UserDTO;
import com.sandro.dtomapper.UserDTOMapper;
import com.sandro.enumeration.EventType;
import com.sandro.event.NewUserEvent;
import com.sandro.exception.ApiException;
import com.sandro.form.*;
import com.sandro.provider.TokenProvider;
import com.sandro.repository.RoleRepository;
import com.sandro.service.EventService;
import com.sandro.service.RoleService;
import com.sandro.service.UserService;
import com.sandro.utils.ExceptionUtils;
import com.sandro.utils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 05.06.2023
 */

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
@Slf4j
public class UserResource {

    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RoleRepository<Role> roleRepository;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ApplicationEventPublisher publisher;
    private final EventService eventService;

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) {
        UserDTO userDTO = userService.createUser(user);
        return ResponseEntity
                .created(getUri())
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of("user", userDTO))
                                .message("User successfully created")
                                .httpStatus(HttpStatus.CREATED)
                                .statusCode(HttpStatus.CREATED.value())
                                .build());
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> getProfile(Authentication authentication) {
        UserDTO userDTO = userService.getUserByEmail(UserUtils.getAuthenticatedUser(authentication).getEmail());
        return ResponseEntity
                .created(getUri())
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userDTO,
                                        "roles", roleService.getRoles(),
                                        "events", eventService.getEventsByUserId(userDTO.getId())))
                                .message("Profile retrieved")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    /**
     *
     */
    @PatchMapping("/update")
    public ResponseEntity<HttpResponse> update(@RequestBody @Valid UpdateForm user) {
        UserDTO updatedUser = userService.updateUserDetails(user);
        publisher.publishEvent(new NewUserEvent(updatedUser.getEmail(), EventType.PROFILE_UPDATE));
        return ResponseEntity
                .created(getUri())
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", updatedUser,
                                        "roles", roleService.getRoles(),
                                        "events", eventService.getEventsByUserId(updatedUser.getId())))
                                .message("User updated")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
//        authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginForm.getEmail(), loginForm.getPassword())); // this line is moved on authenticate()
//        UserDTO userDTO = userService.getUserByEmail(loginForm.getEmail());  // this line is not needed anymore since a user is given and not the email anymore
        UserDTO userDTO = authenticate(loginForm.getEmail(), loginForm.getPassword());
        return userDTO.isUsingMfa() ? sendVerificationCode(userDTO) : sendResponse(userDTO);
    }

    private UserDTO authenticate(String email, String password) {
        try {
//            if (userService.getUserByEmail(email) != null) {
//                publisher.publishEvent(new NewUserEvent(email, EventType.LOGIN_ATTEMPT));
//            }
            Authentication authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(email, password));
            UserDTO loggedInUser = UserUtils.getLoggedInUser(authentication);
            if (!loggedInUser.isUsingMfa()) {
                publisher.publishEvent(new NewUserEvent(email, EventType.LOGIN_ATTEMPT_SUCCESS));
            }
            return loggedInUser;
        } catch (BadCredentialsException exception) {
            publisher.publishEvent(new NewUserEvent(email, EventType.LOGIN_ATTEMPT_FAILURE));
            throw new ApiException("Bad credentials. Access denied.");
        } catch (Exception exception) {
            ExceptionUtils.processError(request, response, exception);
//            return null;
            throw new ApiException(exception.getMessage());
        }
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO userDTO) {
        userService.sendVerificationCode(userDTO);
        return ResponseEntity.ok()
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userDTO))
                                .message("Sent verification code.")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    private ResponseEntity<HttpResponse> sendResponse(UserDTO userDTO) {
        return ResponseEntity.ok()
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userDTO,
                                        "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                        "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDTO))))
                                .message("Login success")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    private UserPrincipal getUserPrincipal(UserDTO userDTO) {
        return new UserPrincipal(
                UserDTOMapper.toUser(userService.getUserByEmail(userDTO.getEmail())),
                roleService.getRoleByUserId(userDTO.getId())
        );
    }

    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email,
                                                   @PathVariable("code") String verificationCode) {
        UserDTO userDTO = userService.verifyCode(email, verificationCode);
        publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), EventType.LOGIN_ATTEMPT_SUCCESS));
        return ResponseEntity.ok()
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userDTO,
                                        "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                        "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDTO))))
                                .message("Login success")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @GetMapping("/error")
    public ResponseEntity<HttpResponse> notFoundError(HttpServletRequest request) {
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .message("The requested page was not found")
                        .developerMessage("No mapping for a " + request.getMethod() + " request for this path")
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    // - - - Set new password when logged in
    @PatchMapping("/update/password")
    public ResponseEntity<HttpResponse> updatePassword(Authentication authentication, @RequestBody @Valid UpdatePasswordForm form) {
        UserDTO userDTO = UserUtils.getAuthenticatedUser(authentication);
        userService.updatePassword(userDTO.getId(), form.getCurrentPassword(), form.getNewPassword(), form.getConfirmNewPassword());
        publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), EventType.PASSWORD_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Password updated successfully")
                        .httpStatus(HttpStatus.OK)
                        .statusCode(HttpStatus.resolve(200).value())
                        .data(Map.of(
                                "user", userService.getUserById(userDTO.getId()),
                                "roles", roleService.getRoles(),
                                "events", eventService.getEventsByUserId(userDTO.getId())))
                        .build()
        );
    }

    @PatchMapping("/update/settings")
    public ResponseEntity<HttpResponse> updateSettings(Authentication authentication, @RequestBody @Valid UpdateSettingsForm form) {
        UserDTO userDTO = UserUtils.getAuthenticatedUser(authentication);
        userService.updateSettings(userDTO.getId(), form.isEnabled(), form.isNotLocked());
        publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), EventType.ACCOUNT_SETTINGS_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Account settings updated successfully")
                        .httpStatus(HttpStatus.OK)
                        .statusCode(HttpStatus.resolve(200).value())
                        .data(Map.of(
                                "user", userService.getUserById(userDTO.getId()),
                                "roles", roleService.getRoles(),
                                "events", eventService.getEventsByUserId(userDTO.getId())
                        ))
                        .build()
        );
    }

    @PatchMapping("/update/usingMfa")
    public ResponseEntity<HttpResponse> updateUsingMfa(Authentication authentication) {
        UserDTO userDTO = UserUtils.getAuthenticatedUser(authentication);
        userService.updateUsingMfa(userDTO.getId());
        publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), EventType.MFA_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Multi-Factor Authentication updated successfully")
                        .httpStatus(HttpStatus.OK)
                        .statusCode(HttpStatus.resolve(200).value())
                        .data(Map.of(
                                "user", userService.getUserById(userDTO.getId()),
                                "roles", roleService.getRoles(),
                                "events", eventService.getEventsByUserId(userDTO.getId())
                        ))
                        .build()
        );
    }

    @PatchMapping("/update/image")
    public ResponseEntity<HttpResponse> updateImage(Authentication authentication,
                                                    @RequestParam("image") MultipartFile image
    ) {
        UserDTO userDTO = UserUtils.getAuthenticatedUser(authentication);
        userService.updateImage(userDTO.getEmail(), image);
        publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), EventType.PROFILE_PICTURE_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Image was updated successfully")
                        .httpStatus(HttpStatus.OK)
                        .statusCode(HttpStatus.resolve(200).value())
                        .data(Map.of(
                                "user", userService.getUserById(userDTO.getId()),
                                "roles", roleService.getRoles(),
                                "events", eventService.getEventsByUserId(userDTO.getId())
                        ))
                        .build()
        );
    }

    @GetMapping(value = "/image/{fileName}", produces = IMAGE_PNG_VALUE)
    public byte[] getProfileImage(@PathVariable("fileName") String fileName) throws Exception {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/Downloads/images/" + fileName));
    }


    // START - - - Set new password without Login
    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> getLinkResetPassword(@PathVariable String email) {
        userService.resetPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Email with verification link was sent. Please check your mail box.")
                        .httpStatus(HttpStatus.OK)
                        .statusCode(HttpStatus.resolve(200).value())
                        .build()
        );
    }

    @PutMapping("/new/password")
    public ResponseEntity<HttpResponse> resetPassword(@RequestBody @Valid ResetPasswordForm form) {
//        TimeUnit.SECONDS.sleep(2);
        userService.updatePasswordBeingLoggedOut(form.getUserId(), form.getNewPassword(), form.getConfirmNewPassword());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Password reset successfully!")
                        .httpStatus(HttpStatus.OK)
                        .statusCode(HttpStatus.resolve(200).value())
                        .build()
        );
    }
    // END - - - Set new password without Login

    // - - - Set a new role
    @PatchMapping("/update/role/{roleName}")
    public ResponseEntity<HttpResponse> updateRole(Authentication authentication, @PathVariable String roleName) {
        UserDTO userDTO = UserUtils.getAuthenticatedUser(authentication);
        roleService.updateRole(userDTO.getId(), roleName);
        publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), EventType.ROLE_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Role updated successfully ")
                        .httpStatus(HttpStatus.OK)
                        .statusCode(HttpStatus.resolve(200).value())
                        .data(Map.of(
                                "user", userService.getUserById(userDTO.getId()),
                                "roles", roleService.getRoles()
                        ))
                        .build()
        );
    }

    @GetMapping("/verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyResetPasswordUrl(@PathVariable String key) {
//        TimeUnit.SECONDS.sleep(2);
        UserDTO user = userService.verifyResetPasswordUrl(key);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user))
                        .message("Please enter the new password")
                        .httpStatus(HttpStatus.OK)
                        .statusCode(HttpStatus.resolve(200).value())
                        .build()
        );
    }

    @GetMapping("/verify/account/{key}")
    public ResponseEntity<HttpResponse> verifyAccount(@PathVariable("key") String key) {
        UserDTO userDTO = userService.verifyAccount(key);
        return ResponseEntity.ok()
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .message(userDTO.isEnabled() ? "Account already verified" : "Account verified")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @GetMapping("/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {
        if (isHeaderAndTokenValid(request)) {
            String token = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
            UserDTO userDTO = userService.getUserById(tokenProvider.getSubject(token, request));
            return ResponseEntity.ok()
                    .body(
                            HttpResponse.builder()
                                    .timeStamp(LocalDateTime.now().toString())
                                    .data(Map.of(
                                            "user", userDTO,
                                            "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                            "refresh_token", token))
                                    .message("Token refreshed")
                                    .httpStatus(HttpStatus.OK)
                                    .statusCode(HttpStatus.OK.value())
                                    .build());
        } else {
            return ResponseEntity.badRequest()
                    .body(
                            HttpResponse.builder()
                                    .timeStamp(LocalDateTime.now().toString())
                                    .message("Refresh token missing or invalid")
                                    .httpStatus(HttpStatus.BAD_REQUEST)
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .build());
        }
    }

    //    @GetMapping("/activities")
//    public ResponseEntity<HttpResponse> getEvents(Authentication authentication) {
//        UserDTO userDTO = UserUtils.getAuthenticatedUser(authentication);
//        List<UserEvent> es = (List<UserEvent>) eventService.getEventsByUserId(userDTO.getId());
//        return ResponseEntity.ok()
//                .body(
//                        HttpResponse.builder()
//                                .timeStamp(LocalDateTime.now().toString())
//                                .message("Activities loaded")
//                                .httpStatus(HttpStatus.OK)
//                                .statusCode(HttpStatus.OK.value())
//                                .data(Map.of(
//                                        "user", userService.getUserById(userDTO.getId()),
//                                        "roles", roleService.getRoles(),
//                                        "events", es
//                                ))
//                                .build());
//    }
//
    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) != null
                && request.getHeader(AUTHORIZATION).startsWith("Bearer ")
                && tokenProvider
                .isTokenValid(
                        tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring("Bearer ".length()), request),
                        request.getHeader(AUTHORIZATION).substring("Bearer ".length()));
    }

}
