package com.filippovich.webtask.dao;

import com.filippovich.webtask.model.User;
import java.util.List;
import java.util.Optional;
import com.filippovich.webtask.exception.DaoException;

public interface UserDao {
    Optional<User> findById(long id)  throws DaoException;
    Optional<User> findByUsername(String username) throws DaoException;
    Optional<User> findByEmail(String email)  throws DaoException;
    List<User> findAll() throws DaoException;
    boolean save(User user)  throws DaoException;
    boolean update(User user)   throws DaoException;
    boolean delete(long id)   throws DaoException;
    Optional<User> authentication(String email, String passwordHash)  throws DaoException;
    int countCocktailsByUserId(long userId) throws DaoException;
}
