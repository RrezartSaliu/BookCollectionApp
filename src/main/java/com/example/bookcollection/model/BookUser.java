package com.example.bookcollection.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class BookUser {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Book> myCollection;
    @ManyToMany
    private List<Book> myWishList;
    @ManyToMany
    private Set<Book> likedBooks;

    public BookUser(String firstName, String lastName, String email, String password, UserType userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.userType = userType;
        myCollection = new ArrayList<>();
        myWishList = new ArrayList<>();
        likedBooks = new HashSet<>();
    }
    public BookUser(){

    }

    public Set<Book> getLikedBooks() {
        return likedBooks;
    }

    public void setLikedBooks(Set<Book> likedBooks) {
        this.likedBooks = likedBooks;
    }

    public List<Book> getMyWishList() {
        return myWishList;
    }

    public void setMyWishList(List<Book> myWishList) {
        this.myWishList = myWishList;
    }

    public List<Book> getMyCollection() {
        return myCollection;
    }

    public void setMyCollection(List<Book> myCollection) {
        this.myCollection = myCollection;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addBookToCollection(Book book){
        myCollection.add(book);
    }
    public void addBookToWishList(Book book){
        myWishList.add(book);
    }
    public void removeLike(Book book) { likedBooks.remove(book); }
    public void doLike(Book book) { likedBooks.add(book); }
}
