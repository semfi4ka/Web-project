package com.filippovich.webtask.servlet;

import com.filippovich.webtask.connection.ConnectionDataSource;
import com.filippovich.webtask.exception.ServiceException;
import com.filippovich.webtask.model.User;
import com.filippovich.webtask.service.impl.FeedbackServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        long cocktailId = Long.parseLong(req.getParameter("cocktailId"));
        String ratingStr = req.getParameter("rating");
        String text = req.getParameter("text");

        FeedbackServiceImpl service = new FeedbackServiceImpl(ConnectionDataSource.getDataSource());

        try {
            // рейтинг обязателен (в форме required), но на всякий случай проверим
            if (ratingStr != null && !ratingStr.isBlank()) {
                int rating = Integer.parseInt(ratingStr);
                service.rate(cocktailId, currentUser.getId(), rating);
            }

            // комментарий необязательный
            if (text != null && !text.trim().isEmpty()) {
                service.comment(cocktailId, currentUser.getId(), text);
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/view?id=" + cocktailId);
    }
}