package fr.meritis.first.repository;

import fr.meritis.first.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("integrationtest")

public class UserRepositoryTest {

    public static final String EMAIL = "domain@fake.test";
    public static final String PASSWORD = "123456";
    public static final String FIRSTNAME = "toto";
    public static final String LASTNAME = "titi";
    public static final String PHONE = "12132154679";

    public UserRepositoryTest() {
    }

    @Autowired
    public UserRepository<User> userRepository;

    @Test
    public void createUserTest(){
        //Given
        User user = User.builder().email(EMAIL).password(PASSWORD).firstname(FIRSTNAME).lastname(LASTNAME)
                .phone(PHONE).isUsingMfa(false).enabled(true).isNotLocked(true).build();
        //When
        userRepository.create(user);
        //Then
        Assertions.assertThat(user.getId()).isGreaterThan(0);
    }

}
