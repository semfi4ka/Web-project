package com.filippovich.webtask.servlet;

import com.filippovich.webtask.dao.impl.UserDaoImpl;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

@WebServlet({"/register", "/login"})
public class UserServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(UserServlet.class);

    public static final String LOGIN_PAGE = "WEB-INF/pages/login.jsp";
    public static final String REGISTER_PAGE = "WEB-INF/pages/register.jsp";
    public static final String LOGIN_SERVLET_ACTION = "/login";
    public static final String REGISTER_SERVLET_ACTION = "/register";
    public static final String USERNAME_PARAMETER = "username";
    public static final String EMAIL_PARAMETER = "email";
    public static final String PASSWORD_PARAMETER = "password";
    public static final String WELCOME_PAGE = "welcome";
    public static final String MESSAGE_ATTRIBUTE = "message";
    public static final String SUCCESS_MESSAGE_ATTRIBUTE = "successMessage";
    public static final String REGISTRATION_ERROR_ATTRIBUTE = "registrationError";
    public static final String USERNAME_ERROR_ATTRIBUTE = "usernameError";
    public static final String EMAIL_ERROR_ATTRIBUTE = "emailError";
    public static final String USERNAME_VALUE_ATTRIBUTE = "usernameValue";
    public static final String EMAIL_VALUE_ATTRIBUTE = "emailValue";
    public static final String CURRENT_USER_ATTRIBUTE = "currentUser";

    private UserServiceImpl userService;

    @Override
    public void init() {
        userService = new UserServiceImpl(new UserDaoImpl());
        logger.info("UserServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if (LOGIN_SERVLET_ACTION.equals(path)) {
            req.getRequestDispatcher(LOGIN_PAGE).forward(req, resp);
        } else {
            req.getRequestDispatcher(REGISTER_PAGE).forward(req, resp);
        }
        logger.info("GET request to {} page", path);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        String path = req.getServletPath();
        try {
            if (REGISTER_SERVLET_ACTION.equals(path)) {
                handleRegister(req, resp);
            } else if (LOGIN_SERVLET_ACTION.equals(path)) {
                handleLogin(req, resp);
            }
        } catch (IOException e) {
            logger.error("IOException processing POST request to {}", path, e);
            throw new ServletException(e);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter(USERNAME_PARAMETER);
        String email = req.getParameter(EMAIL_PARAMETER);
        String password = req.getParameter(PASSWORD_PARAMETER);

        try {
            req.setAttribute(USERNAME_VALUE_ATTRIBUTE, username);
            req.setAttribute(EMAIL_VALUE_ATTRIBUTE, email);

            boolean hasError = false;
            if (userService.isUsernameTaken(username)) {
                req.setAttribute(USERNAME_ERROR_ATTRIBUTE, "Username is already taken.");
                hasError = true;
            }
            if (userService.isEmailTaken(email)) {
                req.setAttribute(EMAIL_ERROR_ATTRIBUTE, "Email is already taken.");
                hasError = true;
            }

            if (hasError) {
                req.getRequestDispatcher(REGISTER_PAGE).forward(req, resp);
                logger.warn("Failed registration attempt: username or email already exists");
                return;
            }

            Optional<User> registeredUser = userService.registerUser(username, email, password);
            if (registeredUser.isPresent()) {
                req.setAttribute(SUCCESS_MESSAGE_ATTRIBUTE, "Registration successful!");
                logger.info("New user registered: {}", email);
            } else {
                req.setAttribute(REGISTRATION_ERROR_ATTRIBUTE, "Username or email is already taken.");
                logger.warn("Failed registration attempt for email: {}", email);
            }
            req.getRequestDispatcher(REGISTER_PAGE).forward(req, resp);
        } catch (ServiceException e) {
            logger.error("ServiceException during registration for email: {}", email, e);
            throw new ServletException(e);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter(EMAIL_PARAMETER);
        String password = req.getParameter(PASSWORD_PARAMETER);

        try {
            Optional<User> userOpt = userService.loginUser(email, password);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                req.getSession().setAttribute(CURRENT_USER_ATTRIBUTE, user);
                resp.sendRedirect(WELCOME_PAGE);
                logger.info("User '{}' logged in successfully", email);
            } else {
                req.setAttribute(MESSAGE_ATTRIBUTE, "Invalid email or password");
                req.getRequestDispatcher(LOGIN_PAGE).forward(req, resp);
                logger.warn("Failed login attempt for email: {}", email);
            }
        } catch (ServiceException e) {
            logger.error("ServiceException during login for email: {}", email, e);
            throw new ServletException(e);
        }
    }
}
