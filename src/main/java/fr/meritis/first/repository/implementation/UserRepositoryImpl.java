package fr.meritis.first.repository.implementation;

import fr.meritis.first.domain.Role;
import fr.meritis.first.domain.User;
import fr.meritis.first.exception.ApiException;
import fr.meritis.first.repository.RoleRepository;
import fr.meritis.first.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static fr.meritis.first.enumeration.RoleType.ROLE_USER;
import static fr.meritis.first.enumeration.VerificationType.ACCOUNT;
import static fr.meritis.first.query.UserQuery.*;
import static java.util.Objects.requireNonNull;


@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User> {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public User create(User user) {
        //check the email is unique
        if(getEmailCount(user.getEmail().trim().toLowerCase()) > 0) throw new ApiException("Email already in use. please use a different email and try again.");
        //same new user
        try {
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource parameter = getSqlParameterSource(user);
            namedParameterJdbcTemplate.update(INSERT_USER_QUERRY, parameter, holder);

            user.setId(requireNonNull(holder.getKey()).longValue());
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());

            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
            namedParameterJdbcTemplate.update(INSERT_VERIFICATION_URL_QUERY, Map.of("userId", user.getId(), "url", verificationUrl));

            //emailService.sendVerificationUrl(user.getFirstname(), user.getEmail(), verificationUrl, ACCOUNT);

            user.setEnabled(false);
            user.setNotLocked(true);

            return user;
        } catch (Exception exception) {
            throw new ApiException("An error occured please try again.");
        }
    }

    @Override
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }


    private String getVerificationUrl(String key, String type){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify" + type + "/" + key).toUriString();
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstname())
                .addValue("lastName", user.getLastname())
                .addValue("email", user.getEmail())
                .addValue("password", bCryptPasswordEncoder.encode(user.getPassword()));
    }

    private Integer getEmailCount(String email) {
        return namedParameterJdbcTemplate.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
    }
}
