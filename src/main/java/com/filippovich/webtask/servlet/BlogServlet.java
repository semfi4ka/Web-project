package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.dao.impl.BlogDaoImpl;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.BlogComment;
import com.filippovich.webtask.model.BlogPost;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.model.UserRole;
import com.filippovich.webtask.service.impl.BlogServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/blog")
public class BlogServlet extends HttpServlet {

    private static final String PAGE = "/WEB-INF/pages/blog.jsp";
    private static final String ACTION_PARAM = "action";
    private static final String CREATE_ACTION = "create";
    private static final String COMMENT_ACTION = "comment";

    private BlogServiceImpl blogService;

    @Override
    public void init() throws ServletException {
        try {
            blogService = new BlogServiceImpl(new BlogDaoImpl(ConnectionDataSource.getDataSource()));
        } catch (DaoException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");

        try {
            List<BlogPost> posts = blogService.getPosts();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            Map<Long, String> postDates = new HashMap<>();
            Map<Long, List<BlogComment>> commentsByPost = new HashMap<>();
            Map<Long, String> commentDates = new HashMap<>();

            for (BlogPost post : posts) {
                if (post.getCreatedAt() != null) {
                    postDates.put(post.getId(), post.getCreatedAt().format(formatter));
                }

                List<BlogComment> comments = blogService.getComments(post.getId());
                commentsByPost.put(post.getId(), comments);

                for (BlogComment comment : comments) {
                    if (comment.getCreatedAt() != null) {
                        commentDates.put(comment.getId(), comment.getCreatedAt().format(formatter));
                    }
                }
            }

            req.setAttribute("currentUser", currentUser);
            req.setAttribute("posts", posts);
            req.setAttribute("postDates", postDates);
            req.setAttribute("commentsByPost", commentsByPost);
            req.setAttribute("commentDates", commentDates);
            req.getRequestDispatcher(PAGE).forward(req, resp);
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter(ACTION_PARAM);

        try {
            if (CREATE_ACTION.equals(action) && currentUser.getRole() == UserRole.ADMIN) {
                blogService.createPost(req.getParameter("title"), req.getParameter("content"), currentUser);
            } else if (COMMENT_ACTION.equals(action)) {
                long postId = Long.parseLong(req.getParameter("postId"));
                blogService.addComment(postId, currentUser.getId(), req.getParameter("text"));
            }

            resp.sendRedirect(req.getContextPath() + "/blog");
        } catch (NumberFormatException | ServiceException e) {
            throw new ServletException(e);
        }
    }
}
