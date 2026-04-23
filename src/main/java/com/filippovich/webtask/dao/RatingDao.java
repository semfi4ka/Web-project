package com.filippovich.webtask.dao;

import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.ProfileRating;

import java.util.List;
import java.util.OptionalDouble;

public interface RatingDao {
    void upsertRating(long cocktailId, long userId, int rating) throws DaoException;

    List<ProfileRating> findRatingsByUser(long userId) throws DaoException;

    OptionalDouble findAvgRating(long cocktailId) throws DaoException;

    OptionalDouble findAvgRatingByAuthor(long authorId) throws DaoException;

    Integer findUserRating(long cocktailId, long userId) throws DaoException;
}
