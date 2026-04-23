package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.dao.CocktailDao;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.CocktailIngredient;
import com.filippovich.webtask.model.CocktailStatus;
import com.filippovich.webtask.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CocktailDaoImpl implements CocktailDao {

    private static final String SQL_FIND_BY_ID = """
        SELECT id, name, description, status, author_id, created_at, image_path
        FROM cocktails
        WHERE id = ?
    """;

    private static final String SQL_FIND_ALL = """
        SELECT id, name, description, status, author_id, created_at, image_path
        FROM cocktails
    """;

    private static final String SQL_FIND_BY_STATUS = """
        SELECT id, name, description, status, author_id, created_at, image_path
        FROM cocktails
        WHERE status = ?
    """;

    private static final String SQL_FIND_AUTHOR_NAME_BY_ID = """
        SELECT username
        FROM users
        WHERE id = ?
    """;

    public static final String SQL_DELETE_INGREDIENTS = "DELETE FROM cocktail_ingredients WHERE cocktail_id = ?";
    private final DataSource dataSource;

    private static final String SQL_SAVE = """
        INSERT INTO cocktails (name, description, status, author_id, created_at, image_path)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

    private static final String SQL_UPDATE = """
        UPDATE cocktails
        SET name=?, description=?, status=?, author_id=?, image_path=?
        WHERE id=?
    """;

    private static final String SQL_DELETE = "DELETE FROM cocktails WHERE id=?";

    private static final String SQL_FIND_INGREDIENTS_BY_COCKTAIL_ID = """
        SELECT i.name, ci.amount, i.unit
        FROM cocktail_ingredients ci
        JOIN ingredients i ON ci.ingredient_id = i.id
        WHERE ci.cocktail_id = ?
    """;

    private static final String SQL_INSERT_INGREDIENT = """
        INSERT INTO ingredients (name, unit)
        VALUES (?, ?)
        ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id)
    """;

    private static final String SQL_INSERT_COCKTAIL_INGREDIENT = """
        INSERT INTO cocktail_ingredients (cocktail_id, ingredient_id, amount)
        VALUES (?, ?, ?)
    """;

    public CocktailDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Cocktail> findById(long id) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapCocktail(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Cocktail> findAll() throws DaoException {
        List<Cocktail> cocktailList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                cocktailList.add(mapCocktail(resultSet));
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return cocktailList;
    }

    @Override
    public boolean save(Cocktail cocktail) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SAVE, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, cocktail.getName());
            preparedStatement.setString(2, cocktail.getDescription());
            preparedStatement.setString(3, cocktail.getStatus().name());
            preparedStatement.setLong(4, cocktail.getAuthor().getId());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(cocktail.getCreatedAt()));
            preparedStatement.setString(6, cocktail.getImagePath()); // may be null

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) return false;

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) cocktail.setId(generatedKeys.getLong(1));
            }
            return true;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Cocktail cocktail) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE)) {

            preparedStatement.setString(1, cocktail.getName());
            preparedStatement.setString(2, cocktail.getDescription());
            preparedStatement.setString(3, cocktail.getStatus().name());
            preparedStatement.setLong(4, cocktail.getAuthor().getId());
            preparedStatement.setString(5, cocktail.getImagePath());
            preparedStatement.setLong(6, cocktail.getId());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(long id) throws DaoException {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement stmtIngredients = connection.prepareStatement(SQL_DELETE_INGREDIENTS)) {
                stmtIngredients.setLong(1, id);
                stmtIngredients.executeUpdate();
            }

            try (PreparedStatement stmtCocktail = connection.prepareStatement(SQL_DELETE)) {
                stmtCocktail.setLong(1, id);
                return stmtCocktail.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Cocktail> findByStatus(String status) throws DaoException {
        List<Cocktail> cocktailList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_STATUS)) {

            preparedStatement.setString(1, status);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) cocktailList.add(mapCocktail(resultSet));
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return cocktailList;
    }

    @Override
    public String findAuthorNameById(long authorId) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_AUTHOR_NAME_BY_ID)) {

            preparedStatement.setLong(1, authorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return resultSet.getString("username");
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return "Unknown";
    }

    @Override
    public List<String> findIngredientsByCocktailId(long cocktailId) throws DaoException {
        List<String> ingredients = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_INGREDIENTS_BY_COCKTAIL_ID)) {

            preparedStatement.setLong(1, cocktailId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String ingredient = resultSet.getString("name") + " - " +
                            resultSet.getDouble("amount") + " " +
                            resultSet.getString("unit");
                    ingredients.add(ingredient);
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return ingredients;
    }

    public List<Cocktail> findByAuthorId(long authorId) throws DaoException {
        String sql = """
        SELECT id, name, description, status, author_id, created_at, image_path
        FROM cocktails
        WHERE author_id = ?
        ORDER BY created_at DESC
    """;

        List<Cocktail> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCocktail(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return list;
    }

    @Override
    public boolean saveCocktailWithIngredients(Cocktail cocktail, List<CocktailIngredient> ingredients) throws DaoException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SAVE, Statement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, cocktail.getName());
                    preparedStatement.setString(2, cocktail.getDescription());
                    preparedStatement.setString(3, cocktail.getStatus().name());
                    preparedStatement.setLong(4, cocktail.getAuthor().getId());
                    preparedStatement.setTimestamp(5, Timestamp.valueOf(cocktail.getCreatedAt()));
                    preparedStatement.setString(6, cocktail.getImagePath());
                    preparedStatement.executeUpdate();

                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) cocktail.setId(generatedKeys.getLong(1));
                    }
                }

                for (CocktailIngredient cocktailIngredient : ingredients) {
                    long ingredientId;

                    try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_INGREDIENT, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        preparedStatement.setString(1, cocktailIngredient.getIngredient().getName());
                        preparedStatement.setString(2, cocktailIngredient.getIngredient().getUnit());
                        preparedStatement.executeUpdate();

                        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                            generatedKeys.next();
                            ingredientId = generatedKeys.getLong(1);
                        }
                    }

                    try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_COCKTAIL_INGREDIENT)) {
                        preparedStatement.setLong(1, cocktail.getId());
                        preparedStatement.setLong(2, ingredientId);
                        preparedStatement.setDouble(3, cocktailIngredient.getAmount());
                        preparedStatement.executeUpdate();
                    }
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw new DaoException(e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Cocktail mapCocktail(ResultSet resultSet) throws SQLException {
        Cocktail cocktail = new Cocktail();
        cocktail.setId(resultSet.getLong("id"));
        cocktail.setName(resultSet.getString("name"));
        cocktail.setDescription(resultSet.getString("description"));
        cocktail.setStatus(CocktailStatus.valueOf(resultSet.getString("status")));

        User author = new User();
        author.setId(resultSet.getLong("author_id"));
        cocktail.setAuthor(author);

        Timestamp ts = resultSet.getTimestamp("created_at");
        if (ts != null) cocktail.setCreatedAt(ts.toLocalDateTime());

        cocktail.setImagePath(resultSet.getString("image_path")); // can be null
        return cocktail;
    }
}