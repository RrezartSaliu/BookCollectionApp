package com.example.bookcollection.config;

import com.example.bookcollection.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomEmailPasswordAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public CustomEmailPasswordAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        if("".equals(email) || "".equals(password)){
            throw new BadCredentialsException("Invalid Credentials");
        }
        UserDetails userDetails = this.userService.loadUserByUsername(email);

        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new BadCredentialsException("Pasword is incorrect!");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
