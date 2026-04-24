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
import java.util.ArrayList;
import java.util.Comparator;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final String PAGE = "/WEB-INF/pages/profile.jsp";
    private static final String USER_ID_PARAM = "userId";
    private static final String COCKTAIL_SORT_PARAM = "cocktailSort";
    private static final String COMMENT_SORT_PARAM = "commentSort";

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
            String cocktailSort = normalizeSort(req.getParameter(COCKTAIL_SORT_PARAM));
            String commentSort = normalizeSort(req.getParameter(COMMENT_SORT_PARAM));

            List<Cocktail> profileCocktails = new ArrayList<>(cocktailService.getCocktailsByAuthor(profileUser.getId()));
            List<ProfileComment> profileComments = ownProfile
                    ? new ArrayList<>(feedbackService.getCommentsByUser(profileUser.getId()))
                    : new ArrayList<>();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            Map<Long, String> cocktailDates = new HashMap<>();
            Map<Long, Double> cocktailRatingValues = new HashMap<>();
            Map<Long, String> profileCocktailRatings = new HashMap<>();
            for (Cocktail cocktail : profileCocktails) {
                if (cocktail.getCreatedAt() != null) {
                    cocktailDates.put(cocktail.getId(), cocktail.getCreatedAt().format(dtf));
                }
                OptionalDouble avgRating = feedbackService.getAvgRating(cocktail.getId());
                if (avgRating.isPresent()) {
                    double value = avgRating.getAsDouble();
                    cocktailRatingValues.put(cocktail.getId(), value);
                    profileCocktailRatings.put(cocktail.getId(), String.format("%.1f", value));
                } else {
                    cocktailRatingValues.put(cocktail.getId(), Double.NaN);
                    profileCocktailRatings.put(cocktail.getId(), "-");
                }
            }

            OptionalDouble profileAvgRating = feedbackService.getAvgRatingByAuthor(profileUser.getId());
            sortCocktails(profileCocktails, cocktailRatingValues, cocktailSort);
            if (ownProfile) {
                sortComments(profileComments, commentSort);
            }

            req.setAttribute("currentUser", currentUser);
            req.setAttribute("profileUser", profileUser);
            req.setAttribute("isOwnProfile", ownProfile);
            req.setAttribute("cocktailSort", cocktailSort);
            req.setAttribute("commentSort", commentSort);
            req.setAttribute("cocktailSortLabel", sortLabel(cocktailSort));
            req.setAttribute("commentSortLabel", sortLabel(commentSort));
            req.setAttribute("nextCocktailSort", nextSort(cocktailSort));
            req.setAttribute("nextCommentSort", nextSort(commentSort));
            req.setAttribute("profileAvgRating",
                    profileAvgRating.isPresent() ? String.format("%.1f", profileAvgRating.getAsDouble()) : "-");
            req.setAttribute("profileCocktails", profileCocktails);
            req.setAttribute("profileCocktailRatings", profileCocktailRatings);
            req.setAttribute("cocktailDates", cocktailDates);
            if (ownProfile) {
                req.setAttribute("profileComments", profileComments);

                Map<Integer, String> commentDates = new HashMap<>();
                for (int i = 0; i < profileComments.size(); i++) {
                    if (profileComments.get(i).getCreatedAt() != null) {
                        commentDates.put(i, profileComments.get(i).getCreatedAt().format(dtf));
                    }
                }
                req.setAttribute("profileCommentDates", commentDates);
            }

            if (ownProfile) {
                List<ProfileRating> myRatings = feedbackService.getRatingsByUser(currentUser.getId());

                Map<Long, String> ratingDates = new HashMap<>();
                for (ProfileRating rating : myRatings) {
                    if (rating.getUpdatedAt() != null) {
                        ratingDates.put(rating.getCocktailId(), rating.getUpdatedAt().format(dtf));
                    }
                }

                req.setAttribute("myRatings", myRatings);
                req.setAttribute("ratingDates", ratingDates);
            }

            req.getRequestDispatcher(PAGE).forward(req, resp);
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
    }

    private String normalizeSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return "newest";
        }
        return switch (sort) {
            case "oldest", "highest_rating", "lowest_rating" -> sort;
            default -> "newest";
        };
    }

    private String nextSort(String sort) {
        return switch (sort) {
            case "newest" -> "oldest";
            case "oldest" -> "highest_rating";
            case "highest_rating" -> "lowest_rating";
            default -> "newest";
        };
    }

    private String sortLabel(String sort) {
        return switch (sort) {
            case "oldest" -> "Oldest first";
            case "highest_rating" -> "Highest rating";
            case "lowest_rating" -> "Lowest rating";
            default -> "Newest first";
        };
    }

    private void sortCocktails(List<Cocktail> cocktails, Map<Long, Double> ratingValues, String sort) {
        Comparator<Cocktail> comparator = switch (sort) {
            case "oldest" -> Comparator.comparing(Cocktail::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "highest_rating" -> Comparator
                    .comparing((Cocktail cocktail) -> ratingSortableValue(ratingValues.get(cocktail.getId()), true))
                    .thenComparing(Cocktail::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            case "lowest_rating" -> Comparator
                    .comparing((Cocktail cocktail) -> ratingSortableValue(ratingValues.get(cocktail.getId()), false))
                    .thenComparing(Cocktail::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            default -> Comparator.comparing(Cocktail::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        };
        cocktails.sort(comparator);
    }

    private void sortComments(List<ProfileComment> comments, String sort) {
        Comparator<ProfileComment> comparator = switch (sort) {
            case "oldest" -> Comparator.comparing(ProfileComment::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "highest_rating" -> Comparator
                    .comparing((ProfileComment comment) -> commentRatingSortableValue(comment.getRating(), true))
                    .thenComparing(ProfileComment::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            case "lowest_rating" -> Comparator
                    .comparing((ProfileComment comment) -> commentRatingSortableValue(comment.getRating(), false))
                    .thenComparing(ProfileComment::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            default -> Comparator.comparing(ProfileComment::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        };
        comments.sort(comparator);
    }

    private double ratingSortableValue(Double value, boolean highestFirst) {
        if (value == null || value.isNaN()) {
            return Double.POSITIVE_INFINITY;
        }
        return highestFirst ? -value : value;
    }

    private double commentRatingSortableValue(Integer value, boolean highestFirst) {
        if (value == null) {
            return Double.POSITIVE_INFINITY;
        }
        return highestFirst ? -value : value;
    }
}
