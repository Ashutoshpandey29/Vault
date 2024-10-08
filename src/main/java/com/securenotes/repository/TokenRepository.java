package com.securenotes.repository;

import com.securenotes.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer>  {

    @Query("select t from Token t inner join user u on t.user.id = u.id where t.user.id = :userId and t.isLoggedOut = false")
    List<Token> findAllTokenByUser(int userId);

    Optional<Token> findByToken(String token);
}
