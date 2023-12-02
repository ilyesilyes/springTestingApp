package fr.meritis.first.repository;

import fr.meritis.first.domain.User;
import fr.meritis.first.dto.UserDTO;

import java.util.Collection;

public interface UserRepository<T extends User> {
    /* Basic CRUD Operation */
    T create(T data);
    Collection<T> list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);

    T getUserByEmail(String email);

    void sendVerificationCode(UserDTO user);

    /* more complexe operation */

}
