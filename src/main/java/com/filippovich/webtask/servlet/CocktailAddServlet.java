package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.CocktailIngredient;
import com.filippovich.webtask.model.Ingredient;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.service.impl.CocktailServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet(CocktailAddServlet.URL_MAPPING)
@MultipartConfig(
        maxFileSize = 5_242_880,       // 5MB
        maxRequestSize = 6_291_456     // ~6MB
)
public class CocktailAddServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(CocktailAddServlet.class);

    public static final String URL_MAPPING = "/add";
    public static final String PAGE_ADD = "/WEB-INF/pages/add.jsp";
    public static final String PAGE_WELCOME = "/welcome";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_DESCRIPTION = "description";
    public static final String PARAM_INGREDIENT_NAME = "ingredientName";
    public static final String PARAM_INGREDIENT_AMOUNT = "ingredientAmount";
    public static final String PARAM_INGREDIENT_UNIT = "ingredientUnit";
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String LOGIN_PATH = "/login";

    public static final String PARAM_PHOTO = "photo";

    private CocktailServiceImpl cocktailService;
    private DataSource dataSource;

    @Override
    public void init() {
        dataSource = ConnectionDataSource.getDataSource();
        cocktailService = new CocktailServiceImpl(dataSource);
        logger.info("CocktailAddServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + LOGIN_PATH);
            return;
        }
        req.setAttribute(ATTR_CURRENT_USER, currentUser);
        req.getRequestDispatcher(PAGE_ADD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute(ATTR_CURRENT_USER);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + LOGIN_PATH);
            return;
        }

        String name = req.getParameter(PARAM_NAME);
        String description = req.getParameter(PARAM_DESCRIPTION);

        // ===== save photo (optional) =====
        String imagePath = null;
        Part photoPart = req.getPart(PARAM_PHOTO);

        if (photoPart != null && photoPart.getSize() > 0) {
            String ct = photoPart.getContentType();
            if (ct != null && ct.startsWith("image/")) {
                String uploadsRealPath = getServletContext().getRealPath("/uploads");
                if (uploadsRealPath == null) uploadsRealPath = System.getProperty("java.io.tmpdir");

                Path uploadDir = Paths.get(uploadsRealPath);
                Files.createDirectories(uploadDir);

                String original = photoPart.getSubmittedFileName();
                String ext = "";

                if (original != null) {
                    int dot = original.lastIndexOf('.');
                    if (dot >= 0) ext = original.substring(dot).toLowerCase();
                }

                Set<String> allowed = Set.of(".jpg", ".jpeg", ".png", ".webp");
                if (!allowed.contains(ext)) ext = ".jpg";

                String fileName = UUID.randomUUID() + ext;
                Path target = uploadDir.resolve(fileName);

                try (var in = photoPart.getInputStream()) {
                    Files.copy(in, target);
                }

                imagePath = "/uploads/" + fileName;
            }
        }
        // ================================

        String[] ingredientNames = req.getParameterValues(PARAM_INGREDIENT_NAME);
        String[] ingredientAmounts = req.getParameterValues(PARAM_INGREDIENT_AMOUNT);
        String[] ingredientUnits = req.getParameterValues(PARAM_INGREDIENT_UNIT);

        Cocktail cocktail = new Cocktail();
        cocktail.setName(name);
        cocktail.setDescription(description);
        cocktail.setAuthor(currentUser);
        cocktail.setCreatedAt(java.time.LocalDateTime.now());
        cocktail.setImagePath(imagePath);

        List<CocktailIngredient> ingredientList = new ArrayList<>();
        if (ingredientNames != null) {
            for (int i = 0; i < ingredientNames.length; i++) {
                String ingredient_name = ingredientNames[i] == null ? "" : ingredientNames[i].trim();
                String ingredient_amount = ingredientAmounts[i] == null ? "" : ingredientAmounts[i].trim();
                String ingredient_unit = ingredientUnits[i] == null ? "" : ingredientUnits[i].trim();

                if (ingredient_name.isEmpty() && ingredient_amount.isEmpty() && ingredient_unit.isEmpty()) continue;

                Ingredient ingredient = new Ingredient();
                ingredient.setName(ingredient_name);
                ingredient.setUnit(ingredient_unit);

                CocktailIngredient cocktailIngredient = new CocktailIngredient();
                cocktailIngredient.setIngredient(ingredient);

                double amount = 0;
                if (!ingredient_amount.isEmpty()) {
                    try { amount = Double.parseDouble(ingredient_amount); } catch (Exception ignored) {}
                }
                cocktailIngredient.setAmount(amount);

                ingredientList.add(cocktailIngredient);
            }
        }

        try {
            cocktailService.addCocktailWithIngredients(cocktail, ingredientList, currentUser.getRole().name());
        } catch (ServiceException e) {
            throw new ServletException(e);
        }

        resp.sendRedirect(req.getContextPath() + PAGE_WELCOME);
    }
}