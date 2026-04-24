package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.Comment;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.service.impl.CocktailServiceImpl;
import com.filippovich.webtask.service.impl.FeedbackServiceImpl;
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
    public static final String COMMENT_SORT_PARAMETER = "commentSort";

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

            // форматируем даты в строку (JSP не умеет LocalDateTime в fmt:formatDate)
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            req.setAttribute("cocktailCreatedAt", cocktail.getCreatedAt().format(dtf));

            FeedbackServiceImpl feedbackService = new FeedbackServiceImpl(ConnectionDataSource.getDataSource());

            OptionalDouble avgRating = feedbackService.getAvgRating(cocktail.getId());
            String commentSort = normalizeSort(req.getParameter(COMMENT_SORT_PARAMETER));
            List<Comment> comments = new ArrayList<>(feedbackService.getComments(cocktail.getId()));
            sortComments(comments, commentSort);

            // карта дат комментариев (id -> "dd.MM.yyyy HH:mm")
            Map<Long, String> commentDates = new HashMap<>();
            for (Comment c : comments) {
                if (c.getCreatedAt() != null) {
                    commentDates.put(c.getId(), c.getCreatedAt().format(dtf));
                }
            }
            req.setAttribute("commentDates", commentDates);
            req.setAttribute("commentSort", commentSort);
            req.setAttribute("commentSortLabel", sortLabel(commentSort));
            req.setAttribute("nextCommentSort", nextSort(commentSort));

            req.setAttribute("avgRating",
                    avgRating.isPresent() ? String.format("%.1f", avgRating.getAsDouble()) : "—");
            req.setAttribute("comments", comments);

            // Моя текущая оценка (чтобы показать "Твоя оценка" и кнопку "Изменить")
            Integer myRating = null;
            if (currentUser != null) {
                myRating = feedbackService.getUserRating(cocktail.getId(), currentUser.getId());
            }
            req.setAttribute("myRating", myRating);

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

    private void sortComments(List<Comment> comments, String sort) {
        Comparator<Comment> comparator = switch (sort) {
            case "oldest" -> Comparator.comparing(Comment::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "highest_rating" -> Comparator
                    .comparing((Comment comment) -> commentRatingSortableValue(comment.getRating(), true))
                    .thenComparing(Comment::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            case "lowest_rating" -> Comparator
                    .comparing((Comment comment) -> commentRatingSortableValue(comment.getRating(), false))
                    .thenComparing(Comment::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            default -> Comparator.comparing(Comment::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        };
        comments.sort(comparator);
    }

    private double commentRatingSortableValue(Integer value, boolean highestFirst) {
        if (value == null) {
            return Double.POSITIVE_INFINITY;
        }
        return highestFirst ? -value : value;
    }
}
