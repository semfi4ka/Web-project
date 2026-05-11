package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.dao.UserDao;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private final DataSource dataSource;
    private final String SQL_FIND_BY_ID = """
        SELECT id, username, email, password, role, created_at
        FROM users
        WHERE id = ?
""";

    private final String SQL_FIND_BY_EMAIL = """
        SELECT id, username, email, password, role, created_at
        FROM users
        WHERE email = ?
""";

    private final String SQL_FIND_BY_USERNAME = """
        SELECT id, username, email, password, role, created_at
        FROM users
        WHERE username = ?
""";

    private final String SQL_FIND_ALL = """
        SELECT id, username, email, password, role, created_at
        FROM users
""";

    private final String SQL_AUTHENTICATION = """
        SELECT id, username, email, password, role, created_at
        FROM users
        WHERE email = ? AND password = ?
""";

    public static final String SQL_COUNT_COCKTAILS = "SELECT COUNT(*) FROM cocktails WHERE author_id = ?";
    private final String SQL_SAVE = "INSERT INTO users (username, email, password, role, created_at) VALUES (?, ?, ?, ?, ?)";
    private final String SQL_UPDATE = "UPDATE users SET username=?, email=?, password=?, role=? WHERE id=?";
    private final String SQL_DELETE = "DELETE FROM users WHERE id = ?";
    public UserDaoImpl() {
        this.dataSource = ConnectionDataSource.getDataSource();
    }

    @Override
    public Optional<User> findById(long id) throws DaoException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_USERNAME)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_EMAIL)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() throws DaoException {
        List<User> list = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                list.add(mapUser(resultSet));
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return list;
    }

    @Override
    public boolean save(User user) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SAVE, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPasswordHash());
            preparedStatement.setString(4, user.getRole().name());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                }
            }

            return true;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(User user) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPasswordHash());
            preparedStatement.setString(4, user.getRole().name());
            preparedStatement.setLong(5, user.getId());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(long id) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE)) {

            preparedStatement.setLong(1, id);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<User> authentication(String email, String passwordHash) throws DaoException {
        try (Connection connection = ConnectionDataSource.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_AUTHENTICATION)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, passwordHash);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }

        return Optional.empty();
    }

    @Override
    public int countCocktailsByUserId(long userId) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_COUNT_COCKTAILS)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return 0;
    }


    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}
