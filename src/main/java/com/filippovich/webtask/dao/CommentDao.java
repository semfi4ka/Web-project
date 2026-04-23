package com.filippovich.webtask.dao;

import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.Comment;
import com.filippovich.webtask.model.ProfileComment;

import java.util.List;

public interface CommentDao {
    void save(long cocktailId, long userId, String text) throws DaoException;

    List<ProfileComment> findCommentsByUser(long userId) throws DaoException;

    List<Comment> findByCocktailId(long cocktailId) throws DaoException;
}
