package fr.meritis.first.query;

public class UserQuery {
    public static final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM Users WHERE email = :email";
    public static final String INSERT_USER_QUERRY = "INSERT INTO Users (first_name, last_name, email, password) VALUES (:firstName, :lastName, :email, :password)";
    public static final String INSERT_VERIFICATION_URL_QUERY = "INSERT INTO AccountVerifications (user_id, url) VALUES (:userId, :url)";
    public static final String SELECT_USER_BY_EMAIL_QUERRY = "SELECT * FROM Users WHERE email = :email";
    public static final String DELETE_VERIFICATION_CODE_BY_USER_ID = "DELETE FROM TwoFactorVerifications WHERE user_id = :userId";
    public static final String INSERT_VERIFICATION_CODE_QUERRY = "INSERT INTO twoFactorVerifications (user_id, code, expiration_date) VALUES (:userId, :code, :expirationDate)";
    public static final String SELECT_USER_BY_USER_CODE_QUERRY_FROM_TWO_FACTOR_VERFICATION = "SELECT * FROM Users WHERE id = (SELECT user_id FROM twoFactorVerifications WHERE code = :code)";
    public static final String DELETE_CODE_FROM_TWO_FACTOR_AUTHENTIFICATION = "DELETE FROM TwoFactorVerifications WHERE code = :code";

    public static final String SELECT_CODE_EXPERATION_QUERY = "SELECT expiration_date < NOW() AS is_expired FROM TwoFactorVerifications WHERE code = :code";
}
