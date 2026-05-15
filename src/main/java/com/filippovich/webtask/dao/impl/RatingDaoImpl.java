package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.dao.RatingDao;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.ProfileRating;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class RatingDaoImpl implements RatingDao {

    private final DataSource dataSource;

    private static final String SQL_UPSERT = """
        INSERT INTO cocktail_ratings (cocktail_id, user_id, rating)
        VALUES (?, ?, ?)
        ON DUPLICATE KEY UPDATE rating = VALUES(rating), updated_at = CURRENT_TIMESTAMP
    """;

    private static final String SQL_CREATE_RATINGS = """
        CREATE TABLE IF NOT EXISTS cocktail_ratings (
            cocktail_id BIGINT NOT NULL,
            user_id BIGINT NOT NULL,
            rating INT NOT NULL,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            PRIMARY KEY (cocktail_id, user_id),
            FOREIGN KEY (cocktail_id) REFERENCES cocktails(id) ON DELETE CASCADE,
            FOREIGN KEY (user_id) REFERENCES users(id)
        )
    """;

    private static final String SQL_ADD_UPDATED_AT = """
        ALTER TABLE cocktail_ratings
        ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    """;

    private static final String SQL_AVG = """
        SELECT AVG(rating) AS avg_rating
        FROM cocktail_ratings
        WHERE cocktail_id = ?
    """;

    private static final String SQL_AVG_BY_AUTHOR = """
        SELECT AVG(r.rating) AS avg_rating
        FROM cocktail_ratings r
        JOIN cocktails c ON c.id = r.cocktail_id
        WHERE c.author_id = ?
    """;

    private static final String SQL_FIND_USER_RATING = """
        SELECT rating
        FROM cocktail_ratings
        WHERE cocktail_id = ? AND user_id = ?
    """;

    private static final String SQL_TRENDING_IDS = """
        SELECT r.cocktail_id
        FROM cocktail_ratings r
        JOIN cocktails c ON c.id = r.cocktail_id
        WHERE c.status = 'APPROVED'
          AND r.rating >= 4
          AND r.updated_at >= ?
        GROUP BY r.cocktail_id
        ORDER BY COUNT(*) DESC, AVG(r.rating) DESC, MAX(r.updated_at) DESC
        LIMIT ?
    """;

    private static final String SQL_BEST_CREATED_AFTER_IDS = """
        SELECT c.id
        FROM cocktails c
        JOIN cocktail_ratings r ON r.cocktail_id = c.id
        WHERE c.status = 'APPROVED'
          AND c.created_at >= ?
        GROUP BY c.id
        ORDER BY AVG(r.rating) DESC, COUNT(r.rating) DESC, c.created_at DESC
        LIMIT ?
    """;

    public RatingDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        ensureRatingTable();
    }

    @Override
    public void upsertRating(long cocktailId, long userId, int rating) throws DaoException {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_UPSERT)) {
            ps.setLong(1, cocktailId);
            ps.setLong(2, userId);
            ps.setInt(3, rating);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<ProfileRating> findRatingsByUser(long userId) throws DaoException {
        String sql = """
        SELECT c.id AS cocktail_id, c.name AS cocktail_name, r.rating, r.updated_at
        FROM cocktail_ratings r
        JOIN cocktails c ON c.id = r.cocktail_id
        WHERE r.user_id = ?
        ORDER BY r.updated_at DESC
    """;

        List<com.filippovich.webtask.model.ProfileRating> list = new java.util.ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var pr = new com.filippovich.webtask.model.ProfileRating();
                    pr.setCocktailId(rs.getLong("cocktail_id"));
                    pr.setCocktailName(rs.getString("cocktail_name"));
                    pr.setRating(rs.getInt("rating"));
                    Timestamp ts = rs.getTimestamp("updated_at");
                    if (ts != null) pr.setUpdatedAt(ts.toLocalDateTime());
                    list.add(pr);
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return list;
    }

    @Override
    public OptionalDouble findAvgRating(long cocktailId) throws DaoException {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_AVG)) {
            ps.setLong(1, cocktailId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double v = rs.getDouble("avg_rating");
                    if (rs.wasNull()) return OptionalDouble.empty();
                    return OptionalDouble.of(v);
                }
            }
            return OptionalDouble.empty();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public OptionalDouble findAvgRatingByAuthor(long authorId) throws DaoException {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_AVG_BY_AUTHOR)) {
            ps.setLong(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double value = rs.getDouble("avg_rating");
                    if (rs.wasNull()) {
                        return OptionalDouble.empty();
                    }
                    return OptionalDouble.of(value);
                }
            }
            return OptionalDouble.empty();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Long> findTrendingCocktailIds(LocalDateTime since, int limit) throws DaoException {
        return findCocktailIds(SQL_TRENDING_IDS, since, limit);
    }

    @Override
    public List<Long> findBestCocktailIdsCreatedAfter(LocalDateTime since, int limit) throws DaoException {
        return findCocktailIds(SQL_BEST_CREATED_AFTER_IDS, since, limit);
    }

    @Override
    public Integer findUserRating(long cocktailId, long userId) throws DaoException {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_USER_RATING)) {
            ps.setLong(1, cocktailId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("rating");
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private List<Long> findCocktailIds(String sql, LocalDateTime since, int limit) throws DaoException {
        List<Long> ids = new ArrayList<>();

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(since));
            ps.setInt(2, Math.max(1, limit));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return ids;
    }

    private void ensureRatingTable() {
        try (Connection c = dataSource.getConnection();
             Statement statement = c.createStatement()) {
            statement.execute(SQL_CREATE_RATINGS);

            DatabaseMetaData metaData = c.getMetaData();
            try (ResultSet columns = metaData.getColumns(null, null, "cocktail_ratings", "updated_at")) {
                if (!columns.next()) {
                    statement.execute(SQL_ADD_UPDATED_AT);
                }
            }
        } catch (SQLException ignored) {
            // Existing callers already surface SQL failures through DAO methods.
        }
    }
}
