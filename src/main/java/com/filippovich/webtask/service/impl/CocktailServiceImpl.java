package com.filippovich.webtask.service.impl;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.dao.impl.CocktailDaoImpl;
import com.filippovich.webtask.dao.impl.RatingDaoImpl;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.Cocktail;
import com.filippovich.webtask.model.CocktailIngredient;
import com.filippovich.webtask.model.CocktailStatus;
import com.filippovich.webtask.service.CocktailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class CocktailServiceImpl implements CocktailService {

    private static final Logger logger = LogManager.getLogger(CocktailServiceImpl.class);

    private final CocktailDaoImpl cocktailDao;

    public CocktailServiceImpl(DataSource dataSource) {
        this.cocktailDao = new CocktailDaoImpl(dataSource);
        logger.info("CocktailServiceImpl initialized with dataSource");
    }

    public OptionalDouble getAvgRating(long cocktailId) throws ServiceException {
        try {
            RatingDaoImpl ratingDao =
                    new RatingDaoImpl(ConnectionDataSource.getDataSource());
            return ratingDao.findAvgRating(cocktailId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public List<Cocktail> findAllApproved() throws ServiceException {
        try {
            CocktailDaoImpl cocktailDao = new CocktailDaoImpl(ConnectionDataSource.getDataSource());
            return cocktailDao.findByStatus("APPROVED");
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Cocktail> getAllCocktails() throws ServiceException {
        try {
            logger.debug("Fetching all cocktails");
            return cocktailDao.findAll();
        } catch (DaoException e) {
            logger.error("Error fetching all cocktails", e);
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Cocktail> getCocktailsByStatus(String status) throws ServiceException {
        try {
            logger.debug("Fetching cocktails with status: {}", status);
            return cocktailDao.findByStatus(status);
        } catch (DaoException e) {
            logger.error("Error fetching cocktails by status: {}", status, e);
            throw new ServiceException(e);
        }
    }

    public List<Cocktail> getCocktailsByAuthor(long authorId) throws ServiceException {
        try {
            return cocktailDao.findByAuthorId(authorId);
        } catch (com.filippovich.webtask.exception.DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<Cocktail> getCocktailById(long id) throws ServiceException {
        try {
            logger.debug("Fetching cocktail with id: {}", id);
            return cocktailDao.findById(id);
        } catch (DaoException e) {
            logger.error("Error fetching cocktail by id: {}", id, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean updateCocktail(Cocktail cocktail) throws ServiceException {
        try {
            logger.info("Updating cocktail: {} (id={})", cocktail.getName(), cocktail.getId());
            return cocktailDao.update(cocktail);
        } catch (DaoException e) {
            logger.error("Error updating cocktail: {} (id={})", cocktail.getName(), cocktail.getId(), e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean deleteCocktail(long id) throws ServiceException {
        try {
            logger.info("Deleting cocktail with id: {}", id);
            return cocktailDao.delete(id);
        } catch (DaoException e) {
            logger.error("Error deleting cocktail with id: {}", id, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public String getAuthorNameById(long authorId) throws ServiceException {
        try {
            logger.debug("Fetching author name for id: {}", authorId);
            return cocktailDao.findAuthorNameById(authorId);
        } catch (DaoException e) {
            logger.error("Error fetching author name for id: {}", authorId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public List<String> getIngredientsByCocktailId(long cocktailId) throws ServiceException {
        try {
            logger.debug("Fetching ingredients for cocktail id: {}", cocktailId);
            return cocktailDao.findIngredientsByCocktailId(cocktailId);
        } catch (DaoException e) {
            logger.error("Error fetching ingredients for cocktail id: {}", cocktailId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean addCocktailWithIngredients(Cocktail cocktail, List<CocktailIngredient> ingredients, String role) throws ServiceException {
        switch (role) {
            case "CLIENT":
                cocktail.setStatus(CocktailStatus.MODERATION);
                break;
            case "BARTENDER":
            case "ADMIN":
                cocktail.setStatus(CocktailStatus.APPROVED);
                break;
            default:
                cocktail.setStatus(CocktailStatus.DRAFT);
        }
        try {
            logger.info("Adding cocktail '{}' with status '{}' by role '{}'", cocktail.getName(), cocktail.getStatus(), role);
            return cocktailDao.saveCocktailWithIngredients(cocktail, ingredients);
        } catch (DaoException e) {
            logger.error("Error adding cocktail '{}' with role '{}'", cocktail.getName(), role, e);
            throw new ServiceException(e);
        }
    }
}
