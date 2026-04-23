package com.filippovich.webtask.service.impl;

import com.filippovich.webtask.dao.impl.CommentDaoImpl;
import com.filippovich.webtask.dao.impl.RatingDaoImpl;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.Comment;
import com.filippovich.webtask.service.FeedbackService;

import javax.sql.DataSource;
import java.util.List;
import java.util.OptionalDouble;

public class FeedbackServiceImpl implements FeedbackService {

    private final RatingDaoImpl ratingDao;
    private final CommentDaoImpl commentDao;

    public FeedbackServiceImpl(DataSource ds) {
        this.ratingDao = new RatingDaoImpl(ds);
        this.commentDao = new CommentDaoImpl(ds);
    }

    @Override
    public void rate(long cocktailId, long userId, int rating) throws ServiceException {
        if (rating < 1 || rating > 5) throw new ServiceException("Rating must be 1..5");
        try {
            ratingDao.upsertRating(cocktailId, userId, rating);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void comment(long cocktailId, long userId, String text) throws ServiceException {
        String t = text == null ? "" : text.trim();
        if (t.isEmpty()) return;
        if (t.length() > 1000) t = t.substring(0, 1000);
        try {
            commentDao.save(cocktailId, userId, t);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public java.util.List<com.filippovich.webtask.model.ProfileRating> getRatingsByUser(long userId) throws ServiceException {
        try {
            return ratingDao.findRatingsByUser(userId);
        } catch (com.filippovich.webtask.exception.DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public java.util.List<com.filippovich.webtask.model.ProfileComment> getCommentsByUser(long userId) throws ServiceException {
        try {
            return commentDao.findCommentsByUser(userId);
        } catch (com.filippovich.webtask.exception.DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Comment> getComments(long cocktailId) throws ServiceException {
        try {
            return commentDao.findByCocktailId(cocktailId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public OptionalDouble getAvgRating(long cocktailId) throws ServiceException {
        try {
            return ratingDao.findAvgRating(cocktailId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public OptionalDouble getAvgRatingByAuthor(long authorId) throws ServiceException {
        try {
            return ratingDao.findAvgRatingByAuthor(authorId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Integer getUserRating(long cocktailId, long userId) throws ServiceException {
        try {
            return ratingDao.findUserRating(cocktailId, userId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
