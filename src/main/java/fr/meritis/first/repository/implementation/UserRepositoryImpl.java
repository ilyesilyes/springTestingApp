package fr.meritis.first.repository.implementation;

import fr.meritis.first.domain.Role;
import fr.meritis.first.domain.User;
import fr.meritis.first.domain.UserPrincipal;
import fr.meritis.first.dto.UserDTO;
import fr.meritis.first.exception.ApiException;
import fr.meritis.first.repository.RoleRepository;
import fr.meritis.first.repository.UserRepository;
import fr.meritis.first.repository.implementation.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import static fr.meritis.first.enumeration.RoleType.ROLE_USER;
import static fr.meritis.first.enumeration.VerificationType.ACCOUNT;
import static fr.meritis.first.query.UserQuery.*;
import static fr.meritis.first.utils.SmsUtils.sendSMS;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateFormatUtils.format;
import static org.apache.commons.lang3.time.DateUtils.addDays;


@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {
    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
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
            namedParameterJdbcTemplate.update(INSERT_VERIFICATION_URL_QUERY, of("userId", user.getId(), "url", verificationUrl));

            //emailService.sendVerificationUrl(user.getFirstname(), user.getEmail(), verificationUrl, ACCOUNT);

            user.setEnabled(false);
            user.setNotLocked(true);

            return user;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occured please try again." );
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
        return namedParameterJdbcTemplate.queryForObject(COUNT_USER_EMAIL_QUERY, of("email", email), Integer.class);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if (user == null) {
            log.info("User not found in the database: {}", email);
            throw new UsernameNotFoundException(String.format("User not found in the database: %s", email));
        } else {
            log.info("User found in the database: {}", email);
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()).getPermission());
        }
    }

    public User getUserByEmail(String email) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_USER_BY_EMAIL_QUERRY, of("email", email), new UserRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            throw new ApiException("No user found by email:" + email);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ApiException("An error eccurred. Please try again.");
        }

    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        String experationDate = format(addDays(new Date(),1), DATE_FORMAT);
        String verificaionCode = randomAlphabetic(8).toUpperCase();
        try {
            namedParameterJdbcTemplate.update(DELETE_VERIFICATION_CODE_BY_USER_ID, of("userId", user.getId()));
            namedParameterJdbcTemplate.update(INSERT_VERIFICATION_CODE_QUERRY, of("userId", user.getId(), "code",verificaionCode, "expirationDate", experationDate));
            sendSMS(user.getPhone(),"From: first\nVerification code \n" + verificaionCode);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ApiException("An error eccurred. Please try again.");
        }

    }
}
