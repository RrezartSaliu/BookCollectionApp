package com.example.bookcollection.service.impl;

import com.example.bookcollection.Repository.BookRepository;
import com.example.bookcollection.Repository.UserRepository;
import com.example.bookcollection.model.Book;
import com.example.bookcollection.model.BookUser;
import com.example.bookcollection.model.Exceptions.InvalidUserIdException;
import com.example.bookcollection.model.Exceptions.InvalidUsernameOrPasswordException;
import com.example.bookcollection.model.Exceptions.UsernameAlreadyExistsException;

import com.example.bookcollection.model.UserType;
import com.example.bookcollection.service.BookService;
import com.example.bookcollection.service.EmailService;
import com.example.bookcollection.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final BookRepository bookRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, BookService bookService, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<BookUser> listAll() {
        return this.userRepository.findAll();
    }

    @Override
    public BookUser findById(Long id) {
        return this.userRepository.findById(id).orElseThrow(InvalidUserIdException::new);
    }


    @Override
    public BookUser update(Long id, String firstName, String lastName, String email, String password, UserType type) {
        BookUser user = this.findById(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(this.passwordEncoder.encode(password));
        user.setUserType(type);
        return this.userRepository.save(user);
    }

    @Override
    public BookUser delete(Long id) {
        BookUser user = findById(id);
        userRepository.delete(user);
        return user;
    }

    @Override
    public BookUser register(String firstName, String lastName, String email, String password, UserType type) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty())
            throw new InvalidUsernameOrPasswordException();
        if(this.userRepository.findByEmail(email) != null)
            throw new UsernameAlreadyExistsException(email);
        BookUser user = new BookUser(firstName, lastName, email, passwordEncoder.encode(password), type);
        emailService.sendEmail(email, "Successful registration on BookCollection App", "Hello " + firstName + " " +lastName + "you have been successfully registered on our page");
        return userRepository.save(user);
    }

    @Override
    public void addBookToCollection(Book book, Long id) {
        BookUser user = this.findById(id);
        if(!user.getMyCollection().contains(book))
            user.addBookToCollection(book);
        userRepository.save(user);
    }

    @Override
    public BookUser findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void addBookToWishList(Book book, Long id) {
        BookUser user = this.findById(id);
        if(!user.getMyWishList().contains(book))
            user.addBookToWishList(book);
        userRepository.save(user);
    }

    @Override
    public void removeFromWishList(Book book, Long id){
        BookUser user = this.findById(id);
        List<Book> newList = user.getMyWishList();
        newList.remove(book);
        user.setMyWishList(newList);
        userRepository.save(user);
    }

    @Override
    public void removeBookFromMyCollection(Book book, Long id) {
        BookUser user = this.findById(id);
        List<Book> newList = user.getMyCollection();
        newList.remove(book);
        user.setMyCollection(newList);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        BookUser user = userRepository.findByEmail(email);
        return new User(
            user.getEmail(),
            user.getPassword(),
            Stream.of(new SimpleGrantedAuthority(user.getUserType().toString())).collect(Collectors.toList())
        );
    }
    @Override
    public void likeBook(Book book, Long id) {
        BookUser user = this.findById(id);
        if(user.getLikedBooks().contains(book)) {
            user.removeLike(book);
            book.setLikes(book.getLikes()-1);
        }
        else {
            user.doLike(book);
            book.setLikes(book.getLikes()+1);
        }
        bookRepository.save(book);
        userRepository.save(user);
    }
}
