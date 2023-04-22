package hexlet.code.controller;

import hexlet.code.exceptions.BadCredentialsException;
import hexlet.code.dto.AuthenticationRequest;
import hexlet.code.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class AuthenticationController {

    @Autowired
    private AuthenticationService service;

    @Operation(summary = "Get authentication token")
    @ApiResponse(responseCode = "200", description = "Token created",
        content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    @PostMapping
    public ResponseEntity<?> createAuthToken(@RequestBody AuthenticationRequest request) {
        try {
            return service.authenticate(request);
        } catch (Exception e) {
            throw new BadCredentialsException();
        }
    }
}
