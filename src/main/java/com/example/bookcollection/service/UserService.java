package com.example.bookcollection.service;

import com.example.bookcollection.model.Book;
import com.example.bookcollection.model.BookUser;

import com.example.bookcollection.model.UserType;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<BookUser> listAll();
    BookUser findById(Long id);
    BookUser update(Long id, String firstName, String lastName, String email, String password, UserType type);
    BookUser delete(Long id);
    BookUser register(String firstName, String lastName, String email, String password, UserType type);
    void addBookToCollection(Book book, Long id);
    BookUser findByEmail(String email);
    void addBookToWishList(Book book, Long id);
    void removeBookFromMyCollection(Book book, Long id);
    void removeFromWishList(Book book, Long id);
    void likeBook(Book book, Long id);
}
