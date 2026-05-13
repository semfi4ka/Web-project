package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.dao.BlogDao;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.BlogComment;
import com.filippovich.webtask.model.BlogPost;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BlogDaoImpl implements BlogDao {

    private final DataSource dataSource;

    private static final String SQL_CREATE_POSTS = """
        CREATE TABLE IF NOT EXISTS blog_posts (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            title VARCHAR(255) NOT NULL,
            content TEXT NOT NULL,
            author_id BIGINT NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (author_id) REFERENCES users(id)
        )
    """;

    private static final String SQL_CREATE_COMMENTS = """
        CREATE TABLE IF NOT EXISTS blog_comments (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            post_id BIGINT NOT NULL,
            user_id BIGINT NOT NULL,
            text TEXT NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (post_id) REFERENCES blog_posts(id) ON DELETE CASCADE,
            FOREIGN KEY (user_id) REFERENCES users(id)
        )
    """;

    private static final String SQL_FIND_ALL_POSTS = """
        SELECT bp.id, bp.title, bp.content, bp.created_at,
               u.id AS user_id, u.username, u.role
        FROM blog_posts bp
        JOIN users u ON u.id = bp.author_id
        ORDER BY bp.created_at DESC
    """;

    private static final String SQL_INSERT_POST = """
        INSERT INTO blog_posts (title, content, author_id)
        VALUES (?, ?, ?)
    """;

    private static final String SQL_FIND_COMMENTS_BY_POST = """
        SELECT bc.id, bc.post_id, bc.text, bc.created_at,
               u.id AS user_id, u.username, u.role
        FROM blog_comments bc
        JOIN users u ON u.id = bc.user_id
        WHERE bc.post_id = ?
        ORDER BY bc.created_at ASC
    """;

    private static final String SQL_INSERT_COMMENT = """
        INSERT INTO blog_comments (post_id, user_id, text)
        VALUES (?, ?, ?)
    """;

    public BlogDaoImpl(DataSource dataSource) throws DaoException {
        this.dataSource = dataSource;
        ensureTables();
    }

    @Override
    public List<BlogPost> findAllPosts() throws DaoException {
        List<BlogPost> posts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL_POSTS);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                posts.add(mapPost(rs));
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return posts;
    }

    @Override
    public void savePost(BlogPost post) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT_POST)) {

            statement.setString(1, post.getTitle());
            statement.setString(2, post.getContent());
            statement.setLong(3, post.getAuthor().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<BlogComment> findCommentsByPostId(long postId) throws DaoException {
        List<BlogComment> comments = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_COMMENTS_BY_POST)) {

            statement.setLong(1, postId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapComment(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return comments;
    }

    @Override
    public void saveComment(long postId, long userId, String text) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT_COMMENT)) {

            statement.setLong(1, postId);
            statement.setLong(2, userId);
            statement.setString(3, text);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private void ensureTables() throws DaoException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(SQL_CREATE_POSTS);
            statement.execute(SQL_CREATE_COMMENTS);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private BlogPost mapPost(ResultSet rs) throws SQLException {
        BlogPost post = new BlogPost();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            post.setCreatedAt(createdAt.toLocalDateTime());
        }

        post.setAuthor(mapUser(rs));
        return post;
    }

    private BlogComment mapComment(ResultSet rs) throws SQLException {
        BlogComment comment = new BlogComment();
        comment.setId(rs.getLong("id"));
        comment.setPostId(rs.getLong("post_id"));
        comment.setText(rs.getString("text"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            comment.setCreatedAt(createdAt.toLocalDateTime());
        }

        comment.setAuthor(mapUser(rs));
        return comment;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        return user;
    }
}
