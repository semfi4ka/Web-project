package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.service.impl.CocktailServiceImpl;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;

@WebServlet("/welcome")
public class WelcomeServlet extends HttpServlet {

    private static final String SEARCH_PARAM = "q";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CocktailServiceImpl cocktailService = new CocktailServiceImpl(ConnectionDataSource.getDataSource());
        Map<Long, String> ratings = new HashMap<>();

        try {
            List<Cocktail> cocktailList = new ArrayList<>(cocktailService.findAllApproved());
            String searchQuery = normalizeSearchValue(req.getParameter(SEARCH_PARAM));

            if (!searchQuery.isBlank()) {
                cocktailList = cocktailList.stream()
                        .map(cocktail -> new SearchHit(cocktail, calculateSearchScore(cocktail.getName(), searchQuery)))
                        .filter(hit -> hit.score() > 0)
                        .sorted(Comparator.comparingInt(SearchHit::score).reversed())
                        .map(SearchHit::cocktail)
                        .toList();
            }

            for (Cocktail cocktail : cocktailList) {
                try {
                    OptionalDouble avg = cocktailService.getAvgRating(cocktail.getId());
                    ratings.put(
                            cocktail.getId(),
                            avg.isPresent() ? String.format("%.1f", avg.getAsDouble()) : "-"
                    );
                } catch (ServiceException e) {
                    ratings.put(cocktail.getId(), "-");
                }
            }

            req.setAttribute("cocktailList", cocktailList);
            req.setAttribute("ratings", ratings);
            req.setAttribute("searchQuery", req.getParameter(SEARCH_PARAM) == null ? "" : req.getParameter(SEARCH_PARAM));
            req.setAttribute("searchPerformed", !searchQuery.isBlank());

            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/pages/welcome.jsp");
            dispatcher.forward(req, resp);
        } catch (ServiceException | ServletException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching approved cocktails.");
        }
    }

    private int calculateSearchScore(String cocktailName, String query) {
        String normalizedName = normalizeSearchValue(cocktailName);
        if (normalizedName.isBlank() || query.isBlank()) {
            return 0;
        }

        if (normalizedName.equals(query)) {
            return 1200;
        }
        if (normalizedName.contains(query)) {
            return 1000 - Math.max(0, normalizedName.length() - query.length());
        }

        int bestScore = 0;
        for (String token : normalizedName.split("\\s+")) {
            if (token.isBlank()) {
                continue;
            }
            if (token.equals(query)) {
                bestScore = Math.max(bestScore, 1100);
            } else if (token.contains(query) || query.contains(token)) {
                bestScore = Math.max(bestScore, 900 - Math.abs(token.length() - query.length()) * 10);
            } else {
                double tokenSimilarity = similarity(token, query);
                if (tokenSimilarity >= 0.64d) {
                    bestScore = Math.max(bestScore, (int) (tokenSimilarity * 850));
                }
            }
        }

        double fullSimilarity = similarity(normalizedName, query);
        if (fullSimilarity >= 0.5d) {
            bestScore = Math.max(bestScore, (int) (fullSimilarity * 700));
        }

        return bestScore;
    }

    private double similarity(String left, String right) {
        int maxLength = Math.max(left.length(), right.length());
        if (maxLength == 0) {
            return 1.0d;
        }
        int distance = levenshteinDistance(left, right);
        return 1.0d - ((double) distance / maxLength);
    }

    private int levenshteinDistance(String left, String right) {
        int[] previous = new int[right.length() + 1];
        int[] current = new int[right.length() + 1];

        for (int j = 0; j <= right.length(); j++) {
            previous[j] = j;
        }

        for (int i = 1; i <= left.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= right.length(); j++) {
                int cost = left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(
                        Math.min(current[j - 1] + 1, previous[j] + 1),
                        previous[j - 1] + cost
                );
            }

            int[] swap = previous;
            previous = current;
            current = swap;
        }

        return previous[right.length()];
    }

    private String normalizeSearchValue(String value) {
        if (value == null) {
            return "";
        }
        return value
                .toLowerCase(Locale.ROOT)
                .replace('ё', 'е')
                .replaceAll("[^\\p{L}\\p{Nd}]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }

    private record SearchHit(Cocktail cocktail, int score) {
    }
}
