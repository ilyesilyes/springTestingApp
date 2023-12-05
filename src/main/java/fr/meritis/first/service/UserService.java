package fr.meritis.first.service;

import fr.meritis.first.domain.User;
import fr.meritis.first.dto.UserDTO;

public interface UserService {
    UserDTO createUser(User user);
    UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO user);

    UserDTO verifyCode(String email, String code);
}
