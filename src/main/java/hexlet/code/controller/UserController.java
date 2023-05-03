package hexlet.code.controller;

import hexlet.code.models.User;
import hexlet.code.dto.UserDto;
import hexlet.code.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;

@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {

    public static final String ID = "/{id}";
    public static final String USER_CONTROLLER_PATH = "/users";
    private static final String ONLY_OWNER_BY_ID = """
        @userRepository.findById(#id).get().getEmail() == authentication.name
        """;

    @Autowired
    private UserService userService;

    @Operation(summary = "Get list of all users")
    @ApiResponse(responseCode = "200", description = "List of all users",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @GetMapping
    public Iterable<User> getAllUsers() {
        return userService.getUsers();
    }

    @Operation(summary = "Get specific user by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User with that id not found", content = @Content)
    })
    @GetMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "User created",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserDto newUser) {
        return userService.createNewUser(newUser);
    }

    @Operation(description = "Delete user by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted"),
        @ApiResponse(responseCode = "404", description = "User with that id not found", content = @Content)
    })
    @PreAuthorize(ONLY_OWNER_BY_ID)
    @DeleteMapping(ID)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Update user by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated"),
        @ApiResponse(responseCode = "404", description = "User with that id not found")
    })
    @PreAuthorize(ONLY_OWNER_BY_ID)
    @PutMapping(ID)
    public User updateUser(@PathVariable Long id, @RequestBody UserDto updatedUser) {
        return userService.updateUser(id, updatedUser);
    }

}
