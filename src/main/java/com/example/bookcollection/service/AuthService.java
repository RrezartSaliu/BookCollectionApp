package com.example.bookcollection.service;

import com.example.bookcollection.model.BookUser;


public interface AuthService {
    BookUser login(String email, String password);
}
