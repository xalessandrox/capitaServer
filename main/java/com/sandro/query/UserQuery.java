package com.sandro.query;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.06.2023
 */


public class UserQuery {

    public static final String INSERT_USER_QUERY =
            "INSERT INTO users (first_name, last_name, email, password) VALUES (:firstName, :lastName, :email, :password)";
    public static final String COUNT_USER_EMAIL_QUERY =
            "SELECT COUNT(*) FROM users WHERE email = :email";
    public static final String INSERT_ACCOUNT_VERIFICATION_URL_QUERY =
            "INSERT INTO account_verifications (user_id, url) VALUES (:userId, :url)";
    public static final String SELECT_USER_BY_EMAIL_QUERY =
            "SELECT * FROM users WHERE email = :email";
    public static final String DELETE_VERIFICATION_CODE_BY_USER_ID_QUERY =
            "DELETE FROM two_factors_verifications WHERE user_id = :id";
    public static final String INSERT_VERIFICATION_CODE_QUERY =
            "INSERT INTO two_factors_verifications (user_id, code, expiration_date) " +
                    "VALUES(:userId, :verificationCode, :expirationDate)";
    public static final String SELECT_CODE_BY_USER_QUERY =
            "SELECT * FROM users WHERE id = (SELECT user_id FROM two_factors_verifications WHERE code = :verificationCode)";
    public static final String VERIFY_CODE_EXPIRATION_QUERY =
            "SELECT expiration_date < NOW() FROM two_factors_verifications WHERE code = :verificationCode";

    public static final String DELETE_PASSWORD_VERIFICATION_URL_BY_USER_ID_QUERY =
            "DELETE FROM reset_password_verifications WHERE user_id = :userId";

    public static final String INSERT_PASSWORD_VERIFICATION_URL_QUERY =
            "INSERT INTO reset_password_verifications (user_id, url, expiration_date) VALUES (:userId, :verificationUrl, :expirationDate);";

    public static final String SELECT_EXPIRATION_TIME_BY_URL_QUERY =
            "SELECT expiration_date < NOW() FROM reset_password_verifications WHERE url = :verificationUrl";

    public static final String SELECT_USER_BY_PASSWORD_URL_QUERY =
            "SELECT * FROM users WHERE id = (SELECT user_id FROM reset_password_verifications WHERE url = :verificationUrl)";

    public static final String UPDATE_USER_PWD_BY_URL_QUERY =
            "UPDATE users SET password = :password WHERE id = (SELECT user_id FROM reset_password_verifications WHERE url = :verificationUrl)";

    public static final String UPDATE_USER_PWD_BY_USER_ID_QUERY = """
            UPDATE users SET password = :password WHERE id = :userId
            """;

    public static final String DELETE_PASSWORD_VERIFICATION_URL_QUERY =
            "DELETE FROM reset_password_verifications WHERE url = :verificationUrl";

    public static final String SELECT_USER_BY_ACCOUNT_VERIFICATION_URL_QUERY =
            "SELECT * FROM users WHERE id = (SELECT user_id FROM account_verifications WHERE url = :verificationUrl)";

    public static final String UPDATE_USER_ENABLED_QUERY =
            "UPDATE users SET enabled = :enabled WHERE id = :id";

    public static final String UPDATE_USER_DETAILS_QUERY =
            "UPDATE users SET first_name = :firstName, last_name = :lastName, email = :email, phone = :phone," +
                    "address = :address, title = :title, bio = :bio WHERE id = :id";

    public static final String SELECT_USER_BY_ID_QUERY =
            "SELECT * from users WHERE id = :id";

    public static final String UPDATE_USER_PASSWORD_BY_ID_QUERY =
            "UPDATE users SET password = :newPassword WHERE id = :userId";

    public static final String UPDATE_USER_SETTINGS_QUERY =
            "UPDATE users SET enabled = :enabled, non_locked = :notLocked WHERE id = :userId";

    public static final String UPDATE_USER_MFA_QUERY =
            "UPDATE users SET using_mfa = :usingMfa WHERE id = :userId";

    public static final String UPDATE_USER_IMAGE_URL_QUERY =
            "UPDATE users SET image_url = :imageUrl WHERE email = :email";

}
