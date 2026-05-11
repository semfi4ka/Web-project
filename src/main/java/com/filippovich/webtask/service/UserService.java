package com.filippovich.webtask.service;

import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.util.PasswordEncoder;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> registerUser(String username, String email, String password) throws ServiceException;

    Optional<User> loginUser(String email, String password) throws ServiceException;

    boolean isUsernameTaken(String username) throws ServiceException;

    boolean isEmailTaken(String email) throws ServiceException;

    boolean updateUser(User user) throws ServiceException;

    List<User> getAllUsers() throws ServiceException;

    User getUserById(long id) throws ServiceException;

    int getCocktailCountByUser(User user) throws ServiceException;
}
