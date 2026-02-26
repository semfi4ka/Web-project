package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.service.impl.CocktailServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

@WebServlet(WelcomeServlet.URL_MAPPING)
public class WelcomeServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(WelcomeServlet.class);

    public static final String URL_MAPPING = "/welcome";
    public static final String PAGE_WELCOME = "WEB-INF/pages/welcome.jsp";
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String ATTR_COCKTAIL_LIST = "cocktailList";
    public static final String APPROVED_STATUS = "APPROVED";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);

        CocktailServiceImpl cocktailService = new CocktailServiceImpl(ConnectionDataSource.getDataSource());

        List<Cocktail> cocktails;
        try {
            cocktails = cocktailService.getCocktailsByStatus(APPROVED_STATUS);
            logger.info("Loaded {} approved cocktails", cocktails.size());
        } catch (ServiceException e) {
            logger.error("Error retrieving cocktails", e);
            throw new ServletException("Failed to load cocktails", e);
        }

        if (currentUser != null) {
            req.setAttribute(ATTR_CURRENT_USER, currentUser);
            logger.info("Welcome page accessed by user: {}", currentUser.getEmail());
        } else {
            logger.info("Welcome page accessed by GUEST");
        }

        req.setAttribute(ATTR_COCKTAIL_LIST, cocktails);
        req.getRequestDispatcher(PAGE_WELCOME).forward(req, resp);
    }
}