package com.filippovich.webtask.service;

import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.Comment;

import java.util.List;
import java.util.OptionalDouble;

public interface FeedbackService {
    void rate(long cocktailId, long userId, int rating) throws ServiceException;

    void comment(long cocktailId, long userId, String text) throws ServiceException;

    java.util.List<com.filippovich.webtask.model.ProfileRating> getRatingsByUser(long userId) throws ServiceException;

    java.util.List<com.filippovich.webtask.model.ProfileComment> getCommentsByUser(long userId) throws ServiceException;

    List<Comment> getComments(long cocktailId) throws ServiceException;

    OptionalDouble getAvgRating(long cocktailId) throws ServiceException;

    OptionalDouble getAvgRatingByAuthor(long authorId) throws ServiceException;

    Integer getUserRating(long cocktailId, long userId) throws ServiceException;
}
