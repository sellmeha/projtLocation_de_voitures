package iscae.mr.jwt_spring_boot.user.controllers;


import iscae.mr.jwt_spring_boot.JwtUtil;
import iscae.mr.jwt_spring_boot.dao.entities.Users;
import iscae.mr.jwt_spring_boot.user.services.UserService;
import iscae.mr.jwt_spring_boot.user.dtos.AuthRequestDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;



    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader("Authorization") String refreshToken, HttpServletResponse response) {

        if (!jwtUtil.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(403).body(Map.of("error", "Invalid or expired refresh token"));
        }
        String username = jwtUtil.extractUsername(refreshToken);


        Users user = userService.getUserByUsername(username);

        String newAccessToken = jwtUtil.generateAccessToken(user);


        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .sameSite("Strict")
                .build();


        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());


        return ResponseEntity.ok(Map.of("access_token", newAccessToken));
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody AuthRequestDTO request, HttpServletResponse response) {
        try {



            Map<String, String> tokens = userService.authenticateUser(request.getUsername(), request.getPassword());


            String accessToken = tokens.get("access_token");
            String refreshToken = tokens.get("refresh_token");


            userService.addCookie(response, "access_token", accessToken);
            userService.addCookie(response, "refresh_token", refreshToken);

            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));   }
    }





    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody AuthRequestDTO request) {
        try {

            userService.registerUser(request.getUsername(), request.getPassword(), "USER");
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        try {

            userService.logout(response);
            return ResponseEntity.ok("User logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }

    }



}
