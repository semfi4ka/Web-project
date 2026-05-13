package com.filippovich.webtask.service;

import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.BlogComment;
import com.filippovich.webtask.model.BlogPost;
import com.filippovich.webtask.model.User;

import java.util.List;

public interface BlogService {
    List<BlogPost> getPosts() throws ServiceException;

    void createPost(String title, String content, User author) throws ServiceException;

    List<BlogComment> getComments(long postId) throws ServiceException;

    void addComment(long postId, long userId, String text) throws ServiceException;
}
