package hexlet.code.controller;

import hexlet.code.UserNotFoundException;
import hexlet.code.enums.Role;
import hexlet.code.models.User;
import hexlet.code.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    public static final String ID = "/{id}";
    public static final String USER_CONTROLLER_PATH = "/users";
    private static final String ONLY_OWNER_BY_ID = """
        @userRepository.findById(#id).get().getEmail() == authentication.getName()
    """;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Get list of all users")
    @ApiResponse(responseCode = "200", description = "List of all users")
    @GetMapping(path = "")
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    @Operation(summary = "Get specific user by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "404", description = "User with that id not found")
            }
    )
    @GetMapping(ID)
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "User created")
    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User newUser) {
        newUser.setRole(Role.USER);
        return userRepository.save(newUser);
    }

    @Operation(summary = "Update user by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User updated"),
                    @ApiResponse(responseCode = "404", description = "User with that id not found")
            }
    )
    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        updatedUser.setId(id);
        return userRepository.save(updatedUser);
    }

    @Operation(description = "Delete user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User with that id not found")
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

}
