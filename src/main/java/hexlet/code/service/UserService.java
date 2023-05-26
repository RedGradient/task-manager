package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
    User createNewUser(UserDto userDto);

    User updateUser(Long id, UserDto userDto);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    Iterable<User> getUsers();

    User getUserById(Long id);

    void deleteUser(Long id);

    User getUserByEmail(String email);

    String getCurrentUsername();
    User getCurrentUser();
}
