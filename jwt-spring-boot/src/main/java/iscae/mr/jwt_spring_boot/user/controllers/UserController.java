
package iscae. mr. jwt_spring_boot. user. controllers;
import iscae.mr.jwt_spring_boot.dao.entities.Users;
import iscae.mr.jwt_spring_boot.dao.repositories.UserRepository;
import iscae.mr.jwt_spring_boot.JwtUtil;
import iscae.mr.jwt_spring_boot.user.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;


        @GetMapping("/list")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<Users>> getAllUsers(HttpServletRequest request) {
            try {
                String token = userService.extractTokenFromCookies(request);
                List<Users> users = userService.getAllUsers(token);
                return ResponseEntity.ok(users);
            } catch (SecurityException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }

        @GetMapping("/details")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<Users> getUserDetails(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
            try {
                String token = userService.extractTokenFromCookies(request);
                Users user = userService.getUserDetails(token, userDetails.getUsername());
                return ResponseEntity.ok(user);
            } catch (SecurityException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }

        @GetMapping("/role")
        public ResponseEntity<Map<String, String>> getUserRole(HttpServletRequest request) {
            try {
                String token = userService.extractTokenFromCookies(request);
                Map<String, String> role = userService.getUserRole(token);
                return ResponseEntity.ok(role);
            } catch (SecurityException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", e.getMessage()));
            }
        }
    }


