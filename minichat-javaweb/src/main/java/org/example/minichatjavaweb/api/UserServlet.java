package org.example.minichatjavaweb.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.minichatjavaweb.constants.UserConstant;
import org.example.minichatjavaweb.entities.User;
import org.example.minichatjavaweb.response.UserResult;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@WebServlet("/user/*")
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if (Objects.equals(req.getPathInfo(), "/listOnlineUsers")) {
            handleListOnlineUsers(req, resp);
        }
    }

    private void handleListOnlineUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = getServletContext();
        Set<User> users = (Set<User>) context.getAttribute(UserConstant.ONLINE_USER_KEY);
        if (users == null) {
            resp.getWriter().write("[]");
            return;
        }
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(UserConstant.SESSION_KEY);
        List<UserResult> userResults = users.stream()
                .filter(u -> !Objects.equals(u.getId(), user.getId()))
                .map(u -> UserResult.builder().id(u.getId()).username(u.getUsername()).build())
                .toList();
        ObjectMapper om = new ObjectMapper();
        om.writeValue(resp.getOutputStream(), userResults);
    }
}
