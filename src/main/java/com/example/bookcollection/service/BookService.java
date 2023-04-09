package com.example.bookcollection.service;

import com.example.bookcollection.model.Book;
import java.util.*;

public interface BookService {
    List<Book> listAll();
    Book findById(Long id);
    Book create(String name, String author, double price, char currency, String category, String image);
    Book update(Long id, String name, String author, double price, char currency, String category, String image);
    Book delete(Long id);
    List<Book> filter (String author, String category, String name);
    List<Book> listTop12();
}
