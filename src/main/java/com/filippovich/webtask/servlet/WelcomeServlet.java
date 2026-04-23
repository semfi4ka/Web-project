package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.service.impl.CocktailServiceImpl;
import com.filippovich.webtask.service.impl.FeedbackServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.RequestDispatcher;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.OptionalDouble;

@WebServlet("/welcome")
public class WelcomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CocktailServiceImpl cocktailService = new CocktailServiceImpl(ConnectionDataSource.getDataSource());

        // Создаем карту для хранения рейтингов
        Map<Long, String> ratings = new HashMap<>();
        List<Cocktail> cocktailList = null;

        try {
            // Получаем все одобренные коктейли
            cocktailList = cocktailService.findAllApproved();



            // Для каждого коктейля получаем средний рейтинг
            for (Cocktail cocktail : cocktailList) {
                try {
                    OptionalDouble avg = cocktailService.getAvgRating(cocktail.getId());
                    ratings.put(
                            cocktail.getId(),
                            avg.isPresent() ? String.format("%.1f", avg.getAsDouble()) : "—"
                    );
                } catch (ServiceException e) {
                    // Логируем ошибку при получении рейтинга
                    System.err.println("Error getting rating for cocktail ID: " + cocktail.getId());
                    e.printStackTrace();
                    ratings.put(cocktail.getId(), "Ошибка");
                }
            }

            // Передаем список коктейлей и рейтингов в запрос
            req.setAttribute("cocktailList", cocktailList);
            req.setAttribute("ratings", ratings);

            // Перенаправляем на welcome.jsp
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/pages/welcome.jsp");
            dispatcher.forward(req, resp);

        } catch (ServiceException | ServletException e) {
            // Логируем ошибку, если не удалось получить коктейли
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching approved cocktails.");
        }
    }
}