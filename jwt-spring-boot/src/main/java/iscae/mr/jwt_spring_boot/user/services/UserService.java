package iscae.mr.jwt_spring_boot.user.services;

import iscae.mr.jwt_spring_boot.JwtUtil;
import iscae.mr.jwt_spring_boot.dao.entities.Users;
import iscae.mr.jwt_spring_boot.dao.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, String> authenticateUser(String username, String password) {
        Users user = userRepository.findByUsername(username);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            String accessToken = jwtUtil.generateAccessToken(user);

            String refreshToken = jwtUtil.generateRefreshToken(user);

            return Map.of(
                    "access_token", accessToken,
                    "refresh_token", refreshToken
            );
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }


    public Users registerUser(String username, String password, String role) {
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("User already exists");
        }

        Users user = new Users();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(new HashSet<>(Set.of(role)));

        return userRepository.save(user);
    }
    public Users getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void addCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);
    }

    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {

        deleteCookie(response, "access_token");
        deleteCookie(response, "refresh_token");
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Logout successful");
        return ResponseEntity.ok(responseBody);
    }

    public void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
    public String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


        public List<Users> getAllUsers(String token) {
            if (token != null && jwtUtil.isTokenValid(token)) {
                return userRepository.findAll();
            }
            throw new SecurityException("Unauthorized");
        }

        public Users getUserDetails(String token, String username) {
            if (token != null && jwtUtil.isTokenValid(token)) {
                return Optional.ofNullable(userRepository.findByUsername(username))
                        .orElseThrow(() -> new RuntimeException("User not found"));
            }
            throw new SecurityException("Unauthorized");
        }

        public Map<String, String> getUserRole(String token) {
            if (token != null && jwtUtil.isTokenValid(token)) {
                List<String> roles = jwtUtil.getRoleFromToken(token);
                if (!roles.isEmpty()) {
                    String role = roles.get(0);
                    Map<String, String> response = new HashMap<>();
                    response.put("role", role);
                    return response;
                } else {
                    throw new SecurityException("No role found");
                }
            }
            throw new SecurityException("Unauthorized");
        }



}

