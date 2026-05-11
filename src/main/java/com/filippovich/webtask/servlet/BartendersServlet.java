package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.dao.impl.UserDaoImpl;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;
import com.filippovich.webtask.service.impl.FeedbackServiceImpl;
import com.filippovich.webtask.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

@WebServlet("/bartenders")
public class BartendersServlet extends HttpServlet {

    private static final String PAGE = "/WEB-INF/pages/bartenders.jsp";

    private UserServiceImpl userService;
    private FeedbackServiceImpl feedbackService;

    @Override
    public void init() {
        userService = new UserServiceImpl(new UserDaoImpl());
        feedbackService = new FeedbackServiceImpl(ConnectionDataSource.getDataSource());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");

        try {
            List<User> bartenders = userService.getAllUsers().stream()
                    .filter(user -> user.getRole() == UserRole.BARTENDER || user.getRole() == UserRole.ADMIN)
                    .sorted(Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER))
                    .toList();

            Map<Long, String> averageRatings = new HashMap<>();
            Map<Long, String> createdDates = new HashMap<>();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            for (User bartender : bartenders) {
                bartender.setCocktailCount(userService.getCocktailCountByUser(bartender));

                OptionalDouble avgRating = feedbackService.getAvgRatingByAuthor(bartender.getId());
                averageRatings.put(
                        bartender.getId(),
                        avgRating.isPresent() ? String.format("%.1f", avgRating.getAsDouble()) : "-"
                );

                if (bartender.getCreatedAt() != null) {
                    createdDates.put(bartender.getId(), bartender.getCreatedAt().format(dateFormatter));
                }
            }

            req.setAttribute("currentUser", currentUser);
            req.setAttribute("bartenders", bartenders);
            req.setAttribute("averageRatings", averageRatings);
            req.setAttribute("createdDates", createdDates);
            req.getRequestDispatcher(PAGE).forward(req, resp);
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
    }
}
