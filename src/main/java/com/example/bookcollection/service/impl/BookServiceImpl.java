package com.example.bookcollection.service.impl;

import com.example.bookcollection.Repository.BookRepository;
import com.example.bookcollection.model.Book;
import com.example.bookcollection.model.Exceptions.InvalidBookIdException;
import com.example.bookcollection.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> listAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(InvalidBookIdException::new);
    }

    @Override
    public Book create(String name, String author, double price, char currency, String category, String image) {
        return this.bookRepository.save(new Book(name, author, price, currency, category, image));
    }

    @Override
    public Book update(Long id, String name, String author, double price, char currency, String category, String image) {
        Book book = this.findById(id);
        book.setName(name);
        book.setAuthor(author);
        book.setPrice(price);
        book.setCurrency(currency);
        book.setCategory(category);
        book.setImage(image);
        return this.bookRepository.save(book);
    }

    @Override
    public Book delete(Long id) {
        Book book = this.findById(id);
        this.bookRepository.delete(book);
        return book;
    }

    @Override
    public List<Book> filter(String author, String category, String name) {
        if (author != null && category == null && name == null)
            return bookRepository.findByAuthorEquals(author);
        if (author == null && category != null && name == null)
            return bookRepository.findByCategoryEquals(category);
        if (author == null && category == null && name != null)
            return bookRepository.findByNameEquals(name);
        return bookRepository.findAll();
    }

    @Override
    public List<Book> listTop12() {
        return bookRepository.findTop12ByOrderByLikesDesc();
    }


}
