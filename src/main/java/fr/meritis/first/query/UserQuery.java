package fr.meritis.first.query;

public class UserQuery {
    public static final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM Users WHERE email = :email";
    public static final String INSERT_USER_QUERRY = "INSERT INTO Users (first_name, last_name, email, password) VALUES (:firstName, :lastName, :email, :password)";
    public static final String INSERT_VERIFICATION_URL_QUERY = "INSERT INTO AccountVerifications (user_id, url) VALUES (:userId, :url)";
    public static final String SELECT_USER_BY_EMAIL_QUERRY = "SELECT * FROM Users WHERE email = :email";
    public static final String DELETE_VERIFICATION_CODE_BY_USER_ID = "DELETE FROM TwoFactorVerifications WHERE user_id = :userId";
    public static final String INSERT_VERIFICATION_CODE_QUERRY = "INSERT INTO twoFactorVerifications (user_id, code, expiration_date) VALUES (:userId, :code, :expirationDate)";

}
