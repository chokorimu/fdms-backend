package fdms.controller;

import fdms.dto.Dto.LoginRequest;
import fdms.dto.Dto.LoginResponse;
import fdms.dto.Dto.UserDto;
import fdms.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return auth.login(req);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        auth.logout(authorization);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UserDto me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return UserDto.from(auth.currentUser(authorization));
    }
}
