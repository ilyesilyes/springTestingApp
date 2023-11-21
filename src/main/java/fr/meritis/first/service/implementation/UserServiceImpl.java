package fr.meritis.first.service.implementation;

import fr.meritis.first.domain.User;
import fr.meritis.first.dto.UserDTO;
import fr.meritis.first.dto.dtomapper.UserDTOMapper;
import fr.meritis.first.repository.UserRepository;
import fr.meritis.first.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }

}
