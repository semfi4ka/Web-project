package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.dao.CommentDao;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.Comment;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDaoImpl implements CommentDao {

    private final DataSource dataSource;

    private static final String SQL_INSERT = """
        INSERT INTO cocktail_comments (cocktail_id, user_id, text)
        VALUES (?, ?, ?)
    """;

    private static final String SQL_FIND_BY_COCKTAIL = """
        SELECT cc.id, cc.cocktail_id, cc.text, cc.created_at,
               u.id AS user_id, u.username, u.role,
               cr.rating AS rating
        FROM cocktail_comments cc
        JOIN users u ON u.id = cc.user_id
        LEFT JOIN cocktail_ratings cr
               ON cr.cocktail_id = cc.cocktail_id AND cr.user_id = cc.user_id
        WHERE cc.cocktail_id = ?
        ORDER BY cc.created_at DESC
    """;

    public CommentDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(long cocktailId, long userId, String text) throws DaoException {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT)) {
            ps.setLong(1, cocktailId);
            ps.setLong(2, userId);
            ps.setString(3, text);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<com.filippovich.webtask.model.ProfileComment> findCommentsByUser(long userId) throws DaoException {
        String sql = """
        SELECT c.id AS cocktail_id, c.name AS cocktail_name,
               cc.text, cc.created_at,
               r.rating AS rating
        FROM cocktail_comments cc
        JOIN cocktails c ON c.id = cc.cocktail_id
        LEFT JOIN cocktail_ratings r
               ON r.cocktail_id = cc.cocktail_id AND r.user_id = cc.user_id
        WHERE cc.user_id = ?
        ORDER BY cc.created_at DESC
    """;

        List<com.filippovich.webtask.model.ProfileComment> list = new java.util.ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var pc = new com.filippovich.webtask.model.ProfileComment();
                    pc.setCocktailId(rs.getLong("cocktail_id"));
                    pc.setCocktailName(rs.getString("cocktail_name"));
                    pc.setText(rs.getString("text"));

                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) pc.setCreatedAt(ts.toLocalDateTime());

                    int r = rs.getInt("rating");
                    pc.setRating(rs.wasNull() ? null : r);

                    list.add(pc);
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return list;
    }

    @Override
    public List<Comment> findByCocktailId(long cocktailId) throws DaoException {
        List<Comment> list = new ArrayList<>();

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_COCKTAIL)) {

            ps.setLong(1, cocktailId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment cm = new Comment();
                    cm.setId(rs.getLong("id"));
                    cm.setCocktailId(rs.getLong("cocktail_id"));
                    cm.setText(rs.getString("text"));

                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) cm.setCreatedAt(ts.toLocalDateTime());

                    User u = new User();
                    u.setId(rs.getLong("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setRole(UserRole.valueOf(rs.getString("role")));
                    cm.setAuthor(u);

                    int r = rs.getInt("rating");
                    cm.setRating(rs.wasNull() ? null : r);

                    list.add(cm);
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return list;
    }
}