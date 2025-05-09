package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.dto.request.RegisterRequest;
import com.hoangtrang.taskoserver.dto.response.RegisterResponse;
import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.exception.ErrorStatus;
import com.hoangtrang.taskoserver.mapper.UserMapper;
import com.hoangtrang.taskoserver.model.User;
import com.hoangtrang.taskoserver.repository.UserRepository;
import com.hoangtrang.taskoserver.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    UserMapper userMapper;


    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorStatus.EMAIL_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(OffsetDateTime.now());

        userRepository.save(user);
        return userMapper.toRegisterResponse(user);
    }
}
