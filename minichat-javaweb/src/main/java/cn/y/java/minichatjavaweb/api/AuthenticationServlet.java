package cn.y.java.minichatjavaweb.api;

import cn.y.java.minichatjavaweb.dto.response.StatusResult;
import cn.y.java.minichatjavaweb.entities.User;
import cn.y.java.minichatjavaweb.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Objects;

import static cn.y.java.minichatjavaweb.constants.UserConstant.SESSION_KEY;

@Slf4j
@WebServlet("/auth/*")
public class AuthenticationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 基础配置
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        switch (req.getPathInfo()) {
            case "/login" -> handleLogin(req, resp);
            case "/logout" -> handleLogout(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        ObjectMapper om = new ObjectMapper();
        if (session.isNew()) {
            session.invalidate();
            om.writeValue(resp.getOutputStream(), new StatusResult("用户未登录"));
            return;
        }
        session.invalidate();
        om.writeValue(resp.getOutputStream(), new StatusResult(true));
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper om = new ObjectMapper();
        String errorMessage = "用户名或密码错误";

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 用户名或密码为空
        if (Objects.isNull(username) || username.isBlank() || Objects.isNull(password) || password.isBlank()){
            log.info("user {} login failed", username);
            resp.setStatus(401);
            om.writeValue(resp.getOutputStream(), new StatusResult(errorMessage));
            return;
        }

        UserRepository userRepository = (UserRepository) getServletContext().getAttribute(UserRepository.KEY);
        User user = userRepository.findByUsername(username);

        if (Objects.isNull(user) || !password.equals(user.getPassword())){
            log.info("user {} login failed", username);
            resp.setStatus(401);
            om.writeValue(resp.getOutputStream(), new StatusResult(errorMessage));
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute(SESSION_KEY, user);
        Cookie nameCookie = new Cookie("uname", user.getUsername());
        Cookie idCookie = new Cookie("uid", user.getId().toString());
        nameCookie.setPath("/");
        idCookie.setPath("/");
        resp.addCookie(nameCookie);
        resp.addCookie(idCookie);

        om.writeValue(resp.getOutputStream(), new StatusResult(true));
    }
}
