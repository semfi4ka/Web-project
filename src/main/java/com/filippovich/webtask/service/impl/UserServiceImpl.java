package com.filippovich.webtask.service.impl;

import com.filippovich.webtask.dao.UserDao;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;
import com.filippovich.webtask.service.UserService;
import com.filippovich.webtask.util.PasswordEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
        logger.info("UserServiceImpl initialized with UserDao");
    }

    @Override
    public Optional<User> registerUser(String username, String email, String password) throws ServiceException {
        logger.info("Attempting to register user with email: {}", email);
        if (isUsernameTaken(username)) {
            logger.warn("Registration failed: username {} already exists", username);
            return Optional.empty();
        }
        if (isEmailTaken(email)) {
            logger.warn("Registration failed: user with email {} already exists", email);
            return Optional.empty();
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashPassword(password));
        newUser.setRole(UserRole.CLIENT);
        newUser.setCreatedAt(LocalDateTime.now());

        try {
            boolean saved = userDao.save(newUser);
            if (!saved) {
                logger.warn("Failed to save new user with email: {}", email);
                return Optional.empty();
            }
            logger.info("User registered successfully with email: {}", email);
        } catch (DaoException e) {
            logger.error("Error saving new user with email: {}", email, e);
            throw new ServiceException(e);
        }

        return Optional.of(newUser);
    }

    @Override
    public boolean isUsernameTaken(String username) throws ServiceException {
        try {
            return userDao.findByUsername(username).isPresent();
        } catch (DaoException e) {
            logger.error("Error checking existing username: {}", username, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean isEmailTaken(String email) throws ServiceException {
        try {
            return userDao.findByEmail(email).isPresent();
        } catch (DaoException e) {
            logger.error("Error checking existing user with email: {}", email, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<User> loginUser(String email, String password) throws ServiceException {
        logger.info("Attempting login for email: {}", email);
        try {
            String hashedPassword = hashPassword(password);
            Optional<User> user = userDao.authentication(email, hashedPassword);
            if (user.isPresent()) {
                logger.info("User login successful for email: {}", email);
            } else {
                logger.warn("User login failed for email: {}", email);
            }
            return user;
        } catch (DaoException e) {
            logger.error("Error during login for email: {}", email, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean updateUser(User user) throws ServiceException {
        logger.info("Updating user: {} (id={})", user.getUsername(), user.getId());
        try {
            boolean updated = userDao.update(user);
            if (updated) {
                logger.info("User updated successfully: {} (id={})", user.getUsername(), user.getId());
            } else {
                logger.warn("User update failed: {} (id={})", user.getUsername(), user.getId());
            }
            return updated;
        } catch (DaoException e) {
            logger.error("Error updating user: {} (id={})", user.getUsername(), user.getId(), e);
            throw new ServiceException("Error updating user", e);
        }
    }

    @Override
    public List<User> getAllUsers() throws ServiceException {
        logger.info("Fetching all users");
        try {
            return userDao.findAll();
        } catch (DaoException e) {
            logger.error("Error fetching all users", e);
            throw new ServiceException(e);
        }
    }

    @Override
    public User getUserById(long id) throws ServiceException {
        logger.info("Fetching user by id: {}", id);
        try {
            User user = userDao.findById(id).orElse(null);
            if (user != null) {
                logger.info("User found: {} (id={})", user.getUsername(), id);
            } else {
                logger.warn("User not found with id: {}", id);
            }
            return user;
        } catch (DaoException e) {
            logger.error("Error fetching user by id: {}", id, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public int getCocktailCountByUser(User user) throws ServiceException {
        logger.info("Counting cocktails for user: {} (id={})", user.getUsername(), user.getId());
        try {
            int count = userDao.countCocktailsByUserId(user.getId());
            logger.info("User {} (id={}) has {} cocktails", user.getUsername(), user.getId(), count);
            return count;
        } catch (DaoException e) {
            logger.error("Error counting cocktails for user: {} (id={})", user.getUsername(), user.getId(), e);
            throw new ServiceException("Error counting cocktails for user", e);
        }
    }

    private String hashPassword(String password) {
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        return passwordEncoder.hash(password);
    }
}
