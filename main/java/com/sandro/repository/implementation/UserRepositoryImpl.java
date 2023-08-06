package com.sandro.repository.implementation;

import com.sandro.domain.Role;
import com.sandro.domain.User;
import com.sandro.domain.UserPrincipal;
import com.sandro.dto.UserDTO;
import com.sandro.enumeration.VerificationType;
import com.sandro.exception.ApiException;
import com.sandro.form.UpdateForm;
import com.sandro.repository.RoleRepository;
import com.sandro.repository.UserRepository;
import com.sandro.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.sandro.enumeration.RoleType.ROLE_USER;
import static com.sandro.enumeration.VerificationType.ACCOUNT;
import static com.sandro.enumeration.VerificationType.PASSWORD;
import static com.sandro.query.UserQuery.*;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.06.2023
 */

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    @Override
    public User create(User user) {
        // check if email is unique
        if (getEmailCount(user.getEmail().trim().toLowerCase()) > 0) {
            throw new ApiException("Email already in use. Please use a different email and try again");
        }
        // save new user if email is unique
        try {
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = getSqlParameterSource(user);
            jdbc.update(INSERT_USER_QUERY, parameterSource, holder);
            user.setId(Objects.requireNonNull(holder.getKey()).longValue());
            // add role to the user
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
            // send verification url
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
            // save url in verification table
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, Map.of("userId", user.getId(), "url", verificationUrl));
            // send email to user with verification url
//            emailService.sendVerificationUrl(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);
            user.setEnabled(false);
            user.setNotLocked(true);
            // return the newly created user
            return user;
            // if any errors, throw an exception with proper message
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
//        return null;
    }


    @Override
    public Collection list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(Long id) {
        try {
            return jdbc.queryForObject(SELECT_USER_BY_ID_QUERY, Map.of("id", id), new UserRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No user found by id: " + id);
        } catch (Exception ex) {
            throw new RuntimeException("Something wrong occurred");
        }
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    private Integer getEmailCount(String email) {
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
    }

    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify/" + type + "/" + key).toUriString();
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", passwordEncoder.encode(user.getPassword()));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if (user == null) {
            log.error("User not found in the database: {}", email);
            throw new UsernameNotFoundException("User not found");
        } else {
            log.info("User found in the database: {}", email);
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()));
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            User user = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
            return user;
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("No user found by this email: " + email);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }

    @Override
    public void sendVerificationCode(UserDTO user) {

        try {

            final String expirationDate = DateFormatUtils.format(DateUtils.addDays(new Date(), 1), DATE_FORMAT);
            final String verificationCode = RandomStringUtils.randomAlphabetic(8).toUpperCase();
            jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID_QUERY, Map.of("id", user.getId()));
            jdbc.update(INSERT_VERIFICATION_CODE_QUERY, Map.of("userId", user.getId(), "verificationCode", verificationCode, "expirationDate", expirationDate));

//            final String mailSubject = "SecureCapita - Verification Code";
//            final String mailContent = "Enter Your Verification code:  \n<h2>" + verificationCode + "</h2>";
//            new EmailUtils().sendEmail(user.getEmail(), mailSubject, mailContent);
            log.info("Verification Code: {}", verificationCode);

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }

    }

    private Boolean isVerificationCodeExpired(String verificationCode) {
        try {
            return jdbc.queryForObject(VERIFY_CODE_EXPIRATION_QUERY, Map.of("verificationCode", verificationCode), Boolean.class);
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("This verification code is not valid. Please try again.");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again");
        }
    }

    @Override
    public User verifyCode(String email, String verificationCode) {
        if (isVerificationCodeExpired(verificationCode)) {
            throw new ApiException("Verification code has expired. Please log in again.");
        }
        try {
            User userByCode = jdbc.queryForObject(SELECT_CODE_BY_USER_QUERY, Map.of("verificationCode", verificationCode), new UserRowMapper());
            User userByMail = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
            if (userByCode.getEmail().equals(userByMail.getEmail())) {
                jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID_QUERY, Map.of("id", userByCode.getId()));
                return userByCode;
            } else {
                throw new ApiException("Code is invalid. Please try again");
            }
        } catch (EmptyResultDataAccessException exception) {
            log.error("Empty Result. Unable to find record.");
            throw exception;
        } catch (Exception exception) {
            log.error("An error occurred. Please try again.");
            throw exception;
        }
    }

    @Override
    public void resetPassword(String email) {

        if (getEmailCount(email.trim().toUpperCase()) == 0) {
            throw new ApiException("There is no account associated with this email");
        }

        try {
            final String expirationDate = DateFormatUtils.format(DateUtils.addDays(new Date(), 1), DATE_FORMAT);
            User user = getUserByEmail(email);
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
            jdbc.update(DELETE_PASSWORD_VERIFICATION_URL_BY_USER_ID_QUERY, Map.of("userId", user.getId()));
            jdbc.update(INSERT_PASSWORD_VERIFICATION_URL_QUERY, Map.of(
                            "userId", user.getId(),
                            "verificationUrl", verificationUrl,
                            "expirationDate", expirationDate
                    )
            );
            //TODO: SEND AN EMAIL
            log.info("VERIFICATION URL: --> {}", verificationUrl);
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("No verification code found");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }

    @Override
    public User verifyResetPasswordUrl(String uuid) {
        if (isVerificationPwdLinkExpired(uuid, PASSWORD)) {
            throw new ApiException("Link has expired. Please try again");
        }
        try {
            User user = jdbc.queryForObject(SELECT_USER_BY_PASSWORD_URL_QUERY, Map.of("verificationUrl", getVerificationUrl(uuid, PASSWORD.getType())), new UserRowMapper());
//            jdbc.update(DELETE_RECORD_FROM_RESET_PASSWORD_VERIFICATIONS_TABLE_QUERY, Map.of("verificationUrl", getVerificationUrl(uuid, PASSWORD.getType())));
            return user;
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("This link is not valid. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }

    }


    private Boolean isVerificationPwdLinkExpired(String uuid, VerificationType password) {
        try {
            return jdbc.queryForObject(SELECT_EXPIRATION_TIME_BY_URL_QUERY, Map.of("verificationUrl", getVerificationUrl(uuid, password.getType())), Boolean.class);
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("This link is not valid. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }


    @Override
    public void setNewPassword(String key, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new ApiException("Passwords don't match. Please try again.");
        }
        try {
            jdbc.update(UPDATE_USER_PWD_BY_URL_QUERY, Map.of(
                    "password", passwordEncoder.encode(password),
                    "verificationUrl", getVerificationUrl(key, PASSWORD.getType()))
            );
            jdbc.update(DELETE_PASSWORD_VERIFICATION_URL_QUERY, Map.of("verificationUrl", getVerificationUrl(key, PASSWORD.getType())));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }

    }

    @Override
    public User verifyAccount(String key) {
        try {
            User user = jdbc.queryForObject(
                    SELECT_USER_BY_ACCOUNT_VERIFICATION_URL_QUERY,
                    Map.of(
                            "verificationUrl", getVerificationUrl(key, ACCOUNT.getType())
                    ),
                    new UserRowMapper()
            );
            jdbc.update(UPDATE_USER_ENABLED_QUERY, Map.of("enabled", true, "id", user.getId()));
            // url can be deleted depending on the use case
            return user;
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("This link is not valid. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }

    @Override
    public User updateUserDetails(UpdateForm user) {
        log.info("Updating user details");
        try {
            jdbc.update(UPDATE_USER_DETAILS_QUERY, getUserDetailsSqlParameterSource(user));
            return get(user.getId());
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No user found by this id: " + user.getId());
        }
    }

    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword) {
        if (!newPassword.trim().equals(confirmNewPassword)) {
            throw new ApiException("Passwords don't match. Please try again.");
        }
        User user = get(id);
        if (!passwordEncoder.matches(currentPassword.trim(), user.getPassword())) {
            throw new ApiException("Password is incorrect. Please try again.");
        }

        try {
            jdbc.update(UPDATE_USER_PASSWORD_BY_ID_QUERY,
                    Map.of(
                            "userId", user.getId(),
                            "newPassword", passwordEncoder.encode(newPassword)
                    ));
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }

    }

    @Override
    public void updateUserSettings(Long userId, boolean enabled, boolean notLocked) {
        log.info("Updating account settings");
        try {
            jdbc.update(UPDATE_USER_SETTINGS_QUERY, Map.of("userId", userId, "enabled", enabled, "notLocked", notLocked));
        } catch (Exception exception) {
            throw new ApiException("An error occurred trying to update user accounts");
        }
    }

    @Override
    public void updateUsingMfa(Long id) {
        log.info("Updating user's Multifactor Authentication");
        User user = get(id);
        boolean usingMfa = user.isUsingMfa();
        try {
            jdbc.update(UPDATE_USER_MFA_QUERY, Map.of("userId", id, "usingMfa", !usingMfa));
        } catch (Exception exception) {
            throw new ApiException("An error occurred trying to update multi-factor authentication");
        }
    }

    private String saveProfileImage(String email, MultipartFile image) {
        Path fileStorageLocation = Paths.get(System.getProperty("user.home") + "/Downloads/images/").toAbsolutePath().normalize();

        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(fileStorageLocation);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new ApiException("Unable to create folder to save image");
            }
            log.info("Directory successfully created");
        }

        String randomKey = null;
        try {
            randomKey = UUID.randomUUID().toString();
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(email + '-' + randomKey + ".png"), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiException("Unable to  save image");
        }
        log.info("The following file was stored: {}", fileStorageLocation.resolve(email + '-' + randomKey + ".png"));
        return randomKey;
    }

    @Override
    public void updateImage(String email, MultipartFile image) {
        log.info("Updating user's image");
        String randomKey = saveProfileImage(email, image);
        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/" + email + '-' + randomKey + ".png").toUriString();
        try {
            jdbc.update(UPDATE_USER_IMAGE_URL_QUERY, Map.of("imageUrl", imageUrl, "email", email));
        } catch (Exception exception) {
            throw new ApiException("An error occurred trying to update multi-factor authentication");
        }
    }

    private SqlParameterSource getUserDetailsSqlParameterSource(UpdateForm user) {
        return new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("address", user.getAddress())
                .addValue("title", user.getTitle())
                .addValue("bio", user.getBio())
                .addValue("phone", user.getPhone());
    }

}
