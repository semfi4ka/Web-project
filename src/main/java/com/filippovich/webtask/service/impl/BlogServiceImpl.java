package com.filippovich.webtask.service.impl;

import com.filippovich.webtask.dao.BlogDao;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.BlogComment;
import com.filippovich.webtask.model.BlogPost;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.service.BlogService;

import java.util.List;

public class BlogServiceImpl implements BlogService {

    private final BlogDao blogDao;

    public BlogServiceImpl(BlogDao blogDao) {
        this.blogDao = blogDao;
    }

    @Override
    public List<BlogPost> getPosts() throws ServiceException {
        try {
            return blogDao.findAllPosts();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void createPost(String title, String content, User author) throws ServiceException {
        String safeTitle = title == null ? "" : title.trim();
        String safeContent = content == null ? "" : content.trim();

        if (safeTitle.isEmpty() || safeContent.isEmpty()) {
            return;
        }

        BlogPost post = new BlogPost();
        post.setTitle(safeTitle.length() > 255 ? safeTitle.substring(0, 255) : safeTitle);
        post.setContent(safeContent);
        post.setAuthor(author);

        try {
            blogDao.savePost(post);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<BlogComment> getComments(long postId) throws ServiceException {
        try {
            return blogDao.findCommentsByPostId(postId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addComment(long postId, long userId, String text) throws ServiceException {
        String safeText = text == null ? "" : text.trim();
        if (safeText.isEmpty()) {
            return;
        }
        if (safeText.length() > 1000) {
            safeText = safeText.substring(0, 1000);
        }

        try {
            blogDao.saveComment(postId, userId, safeText);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
