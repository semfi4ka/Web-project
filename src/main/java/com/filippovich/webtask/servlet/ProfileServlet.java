package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.dao.impl.UserDaoImpl;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.ProfileComment;
import com.filippovich.webtask.model.ProfileRating;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.service.impl.CocktailServiceImpl;
import com.filippovich.webtask.service.impl.FeedbackServiceImpl;
import com.filippovich.webtask.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final String PAGE = "/WEB-INF/pages/profile.jsp";
    private static final String USER_ID_PARAM = "userId";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        long profileUserId = currentUser.getId();
        String userIdParam = req.getParameter(USER_ID_PARAM);
        if (userIdParam != null && !userIdParam.isBlank()) {
            try {
                profileUserId = Long.parseLong(userIdParam);
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/profile");
                return;
            }
        }

        CocktailServiceImpl cocktailService = new CocktailServiceImpl(ConnectionDataSource.getDataSource());
        FeedbackServiceImpl feedbackService = new FeedbackServiceImpl(ConnectionDataSource.getDataSource());
        UserServiceImpl userService = new UserServiceImpl(new UserDaoImpl());

        try {
            User profileUser = profileUserId == currentUser.getId()
                    ? currentUser
                    : userService.getUserById(profileUserId);

            if (profileUser == null) {
                resp.sendRedirect(req.getContextPath() + "/welcome");
                return;
            }

            boolean ownProfile = profileUser.getId() == currentUser.getId();
            List<Cocktail> profileCocktails = cocktailService.getCocktailsByAuthor(profileUser.getId());

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            Map<Long, String> cocktailDates = new HashMap<>();
            for (Cocktail cocktail : profileCocktails) {
                if (cocktail.getCreatedAt() != null) {
                    cocktailDates.put(cocktail.getId(), cocktail.getCreatedAt().format(dtf));
                }
            }

            OptionalDouble profileAvgRating = feedbackService.getAvgRatingByAuthor(profileUser.getId());

            req.setAttribute("currentUser", currentUser);
            req.setAttribute("profileUser", profileUser);
            req.setAttribute("isOwnProfile", ownProfile);
            req.setAttribute("profileAvgRating",
                    profileAvgRating.isPresent() ? String.format("%.1f", profileAvgRating.getAsDouble()) : "-");
            req.setAttribute("profileCocktails", profileCocktails);
            req.setAttribute("cocktailDates", cocktailDates);

            if (ownProfile) {
                List<ProfileRating> myRatings = feedbackService.getRatingsByUser(currentUser.getId());
                List<ProfileComment> myComments = feedbackService.getCommentsByUser(currentUser.getId());

                Map<Long, String> ratingDates = new HashMap<>();
                for (ProfileRating rating : myRatings) {
                    if (rating.getUpdatedAt() != null) {
                        ratingDates.put(rating.getCocktailId(), rating.getUpdatedAt().format(dtf));
                    }
                }

                Map<Integer, String> commentDates = new HashMap<>();
                for (int i = 0; i < myComments.size(); i++) {
                    if (myComments.get(i).getCreatedAt() != null) {
                        commentDates.put(i, myComments.get(i).getCreatedAt().format(dtf));
                    }
                }

                req.setAttribute("myRatings", myRatings);
                req.setAttribute("ratingDates", ratingDates);
                req.setAttribute("myComments", myComments);
                req.setAttribute("profileCommentDates", commentDates);
            }

            req.getRequestDispatcher(PAGE).forward(req, resp);
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
    }
}
