package com.filippovich.webtask.dao;

import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.BlogComment;
import com.filippovich.webtask.model.BlogPost;

import java.util.List;

public interface BlogDao {
    List<BlogPost> findAllPosts() throws DaoException;

    void savePost(BlogPost post) throws DaoException;

    List<BlogComment> findCommentsByPostId(long postId) throws DaoException;

    void saveComment(long postId, long userId, String text) throws DaoException;
}
