package com.example.bookcollection.Repository;

import com.example.bookcollection.model.BookUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<BookUser, Long> {
    BookUser findByEmail(String email);
    Optional<BookUser> findByEmailAndPassword(String email, String password);
}
