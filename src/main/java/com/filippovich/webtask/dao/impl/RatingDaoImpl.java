package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.dao.RatingDao;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.ProfileRating;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.OptionalDouble;

public class RatingDaoImpl implements RatingDao {

    private final DataSource dataSource;

    private static final String SQL_UPSERT = """
        INSERT INTO cocktail_ratings (cocktail_id, user_id, rating)
        VALUES (?, ?, ?)
        ON DUPLICATE KEY UPDATE rating = VALUES(rating)
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

    public RatingDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
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
}
