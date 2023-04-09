package com.example.bookcollection.service.impl;

import com.example.bookcollection.Repository.UserRepository;
import com.example.bookcollection.model.BookUser;
import com.example.bookcollection.model.Exceptions.InvalidArgumentsException;
import com.example.bookcollection.model.Exceptions.InvalidUserCredentialsException;
import com.example.bookcollection.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public BookUser login(String email, String password) {
        if(email == null || email.isEmpty() || password == null || password.isEmpty())
            throw new InvalidArgumentsException();

        return userRepository.findByEmailAndPassword(email,password).orElseThrow(InvalidUserCredentialsException::new);
    }
}
