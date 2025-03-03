package org.example.securetracks.service;


import org.example.securetracks.model.User;
import org.example.securetracks.model.enums.Role;
import org.example.securetracks.model.enums.Status;
import org.example.securetracks.repository.UserRepository;
import org.example.securetracks.request.AuthRequest;
import org.example.securetracks.response.AuthResponse;
import org.example.securetracks.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService,
                       UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();



            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new AuthResponse(

                    user.getEmail(),
                    user.getPhone(),
                    user.getUsername(),
                    user.getRole(),
                    user.getFullName(),
                    jwt



            );
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = User.builder()
                .username(request.getUsername())
                .phone(request.getPhoneNumber())
                .email(request.getEmail())
                .password(encodedPassword)
                .fullName(request.getFullName())
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .createAt(LocalDateTime.now())
                .build();

        userRepository.save(newUser);

        return new AuthResponse(
                newUser.getEmail(),
                newUser.getPhone(),
                newUser.getUsername(),
                newUser.getRole(),
                newUser.getFullName(),
                null
        );
    }

}
