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

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet(CocktailViewServlet.URL_MAPPING)
public class CocktailViewServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(CocktailViewServlet.class);

    public static final String URL_MAPPING = "/view";
    public static final String PAGE_VIEW = "/WEB-INF/pages/cocktail_view.jsp";
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String ATTR_COCKTAIL = "cocktail";
    public static final String ATTR_AUTHOR_NAME = "authorName";
    public static final String ATTR_INGREDIENTS = "ingredients";
    public static final String PAGE_WELCOME = "/welcome";
    public static final String ID_PARAMETER = "id";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);

        String cocktailIdParam = req.getParameter(ID_PARAMETER);
        if (cocktailIdParam == null) {
            resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
            return;
        }

        long cocktailId;
        try {
            cocktailId = Long.parseLong(cocktailIdParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
            return;
        }

        CocktailServiceImpl cocktailService = new CocktailServiceImpl(ConnectionDataSource.getDataSource());

        try {
            var optionalCocktail = cocktailService.getCocktailById(cocktailId);
            if (optionalCocktail.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
                return;
            }

            Cocktail cocktail = optionalCocktail.get();
            String authorName = cocktailService.getAuthorNameById(cocktail.getAuthor().getId());
            List<String> ingredients = cocktailService.getIngredientsByCocktailId(cocktail.getId());

            req.setAttribute(ATTR_COCKTAIL, cocktail);
            req.setAttribute(ATTR_AUTHOR_NAME, authorName);
            req.setAttribute(ATTR_INGREDIENTS, ingredients);

            if (currentUser != null) {
                req.setAttribute(ATTR_CURRENT_USER, currentUser);
                logger.info("User '{}' is viewing cocktail '{}'", currentUser.getEmail(), cocktail.getName());
            } else {
                logger.info("GUEST is viewing cocktail '{}'", cocktail.getName());
            }

            req.getRequestDispatcher(PAGE_VIEW).forward(req, resp);

        } catch (ServiceException e) {
            throw new ServletException("Error retrieving cocktail data", e);
        }
    }
}