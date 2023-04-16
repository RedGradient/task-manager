package hexlet.code.service;

import hexlet.code.component.JwtHelper;
import hexlet.code.dto.AuthenticationRequest;
import hexlet.code.dto.AuthenticationResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper jwtHelper;

    public ResponseEntity<?> authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userService.loadUserByUsername(request.getEmail());
        var jwtToken = jwtHelper.generateToken(user);
        return ResponseEntity.ok(jwtToken);
    }
}
