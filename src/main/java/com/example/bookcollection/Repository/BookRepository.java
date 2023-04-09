package com.example.bookcollection.Repository;

import com.example.bookcollection.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByNameEquals(String name);
    List<Book> findByAuthorEquals(String author);
    List<Book> findByCategoryEquals(String category);
    List<Book> findTop12ByOrderByLikesDesc();
}
