package fdms.service;

import fdms.data.Database;
import fdms.dto.Dto.LoginRequest;
import fdms.dto.Dto.LoginResponse;
import fdms.exception.ApiException;
import fdms.model.Role;
import fdms.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    // token -> user (sesi sederhana di memori)
    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    public LoginResponse login(LoginRequest req) {
        if (req == null || req.username() == null || req.password() == null) {
            throw ApiException.badRequest("username dan password wajib diisi");
        }
        User user = Database.users.get(req.username());
        if (user == null || !user.password.equals(req.password())) {
            throw ApiException.unauthorized("Username atau password salah");
        }
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return new LoginResponse(token, user.username, user.name, user.role);
    }

    public void logout(String authorization) {
        sessions.remove(extractToken(authorization));
    }

    public User currentUser(String authorization) {
        User user = sessions.get(extractToken(authorization));
        if (user == null) {
            throw ApiException.unauthorized("Token tidak valid atau sudah kedaluwarsa");
        }
        return user;
    }

    public User requireRole(String authorization, Role role) {
        User user = currentUser(authorization);
        if (user.role != role) {
            throw ApiException.forbidden("Akses ditolak untuk role " + user.role);
        }
        return user;
    }

    private String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw ApiException.unauthorized("Header Authorization: Bearer <token> diperlukan");
        }
        return authorization.substring(7).trim();
    }
}
