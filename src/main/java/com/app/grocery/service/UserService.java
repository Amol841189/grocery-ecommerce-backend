package com.app.grocery.service;

import com.app.grocery.dto.UserCreateRequest;
import com.app.grocery.dto.UserResponse;
import com.app.grocery.entity.Role;
import com.app.grocery.entity.User;
import com.app.grocery.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreateRequest request){

        // CHeck Email
        if(userRepository.existsByEmail(request.email())){
            throw new RuntimeException("Email already registered");
        }

        // Check phone
        if(userRepository.existsByMobileNumber(request.mobileNumber())){
            throw new RuntimeException("Mobile Number already registered");
        }

        User user = User.builder()
                    .name(request.name())
                    .email(request.email())
                    .mobileNumber(request.mobileNumber())
                    .password(passwordEncoder.encode(request.password()))
                    .role(Role.CUSTOMER)
                    .build();
        
        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getUserId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getMobileNumber(),
                savedUser.getRole()
        );
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                            .stream()
                            .map(user -> new UserResponse(
                                user.getUserId(),
                                user.getName(),
                                user.getEmail(),
                                user.getMobileNumber(),
                                user.getRole()
                            ))
                            .toList();
    }
}
