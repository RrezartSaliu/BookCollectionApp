package com.example.bookcollection.model;

import javax.persistence.*;

@Entity
public class Book {
    @Id
    @Column(name = "isbn")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long isbn;

    @Column(name = "name")
    private String name;
    @Column(name = "author")
    private String author;
    @Column(name = "price")
    private double price;
    @Column(name = "currency")
    private char currency;
    @Column(name = "category")
    private String category;
    @Column(name = "image")
    private String image;
    @Column(name = "no_of_likes")
    private Integer likes;

    public Book(String name, String author, double price, char currency, String category, String image) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.currency = currency;
        this.category = category;
        this.image = image;
        likes = 0;
    }

    public Book() {

    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getIsbn() {
        return isbn;
    }

    public void setIsbn(Long isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public char getCurrency() {
        return currency;
    }

    public void setCurrency(char currency) {
        this.currency = currency;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
