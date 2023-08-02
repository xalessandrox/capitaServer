package com.sandro.resource;

import com.sandro.domain.HttpResponse;
import com.sandro.domain.Role;
import com.sandro.domain.User;
import com.sandro.domain.UserPrincipal;
import com.sandro.dto.UserDTO;
import com.sandro.dtomapper.UserDTOMapper;
import com.sandro.exception.ApiException;
import com.sandro.form.LoginForm;
import com.sandro.form.UpdateForm;
import com.sandro.form.UpdatePasswordForm;
import com.sandro.form.UpdateSettingsForm;
import com.sandro.provider.TokenProvider;
import com.sandro.repository.RoleRepository;
import com.sandro.service.RoleService;
import com.sandro.service.UserService;
import com.sandro.utils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) {
        UserDTO userDTO = userService.createUser(user);
        return ResponseEntity
                .created(getUri())
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of("user", userDTO))
                                .message("User created")
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
                                .data(Map.of("user", userDTO, "roles", roleService.getRoles()))
                                .message("Profile retrieved")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @PatchMapping("/update")
    public ResponseEntity<HttpResponse> update(@RequestBody @Valid UpdateForm user) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1500);
        UserDTO updatedUser = userService.updateUserDetails(user);
        return ResponseEntity
                .created(getUri())
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of("user", updatedUser))
                                .message("User updated")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
//        authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginForm.getEmail(), loginForm.getPassword())); // this line is moved on authenticate()
//        UserDTO userDTO = userService.getUserByEmail(loginForm.getEmail());  // this line is not needed anymore since a user is given and not the email anymore
        Authentication authentication = authenticate(loginForm.getEmail(), loginForm.getPassword());
        UserDTO userDTO = UserUtils.getLoggedInUser(authentication);

        log.info("{}", authentication);
        log.info("{}", ((UserPrincipal) authentication.getPrincipal()).getUser());

        return userDTO.isUsingMfa() ? sendVerificationCode(userDTO) : sendResponse(userDTO);
    }

    private Authentication authenticate(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(email, password));
            return authentication;
        } catch (BadCredentialsException exception) {
            throw new ApiException("Bad credentials. Access denied.");
        } catch (Exception exception) {
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
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Password updated successfully")
                        .httpStatus(HttpStatus.OK)
                        .statusCode(HttpStatus.resolve(200).value())
                        .build()
        );
    }

    @PatchMapping("/update/settings")
    public ResponseEntity<HttpResponse> updateSettings(Authentication authentication, @RequestBody @Valid UpdateSettingsForm form) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1500);
        UserDTO userDTO = UserUtils.getAuthenticatedUser(authentication);
        userService.updateSettings(userDTO.getId(), form.isEnabled(), form.isNotLocked());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Account settings updated successfully")
                        .httpStatus(HttpStatus.OK)
                        .statusCode(HttpStatus.resolve(200).value())
                        .data(Map.of(
                                "user", userService.getUserById(userDTO.getId()),
                                "roles", roleService.getRoles()
                        ))
                        .build()
        );
    }

    // START - - - Set new password without Login
    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable String email) {
        userService.resetPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Email sent. Check your mail box to reset the password")
                        .httpStatus(HttpStatus.OK)
                        .statusCode(HttpStatus.resolve(200).value())
                        .build()
        );
    }

    @PostMapping("/resetpassword/{key}/{password}/{confirmPassword}")
    public ResponseEntity<HttpResponse> setNewPassword(
            @PathVariable("key") String key,
            @PathVariable("password") String password,
            @PathVariable("confirmPassword") String confirmPassword) {

        userService.setNewPassword(key, password, confirmPassword);
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

    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) != null
                && request.getHeader(AUTHORIZATION).startsWith("Bearer ")
                && tokenProvider
                .isTokenValid(
                        tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring("Bearer ".length()), request),
                        request.getHeader(AUTHORIZATION).substring("Bearer ".length()));
    }

}
