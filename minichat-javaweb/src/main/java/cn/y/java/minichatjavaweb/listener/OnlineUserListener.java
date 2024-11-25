package cn.y.java.minichatjavaweb.listener;

import cn.y.java.minichatjavaweb.entities.User;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.*;

import static cn.y.java.minichatjavaweb.constants.UserConstant.ONLINE_USER_KEY;
import static cn.y.java.minichatjavaweb.constants.UserConstant.SESSION_KEY;

@WebListener
public class OnlineUserListener implements HttpSessionListener, HttpSessionAttributeListener {


    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (Objects.equals(SESSION_KEY, event.getName())) {
            ServletContext context = event.getSession().getServletContext();
            synchronized (this) {
                Set<User> users = (Set<User>) context.getAttribute(ONLINE_USER_KEY);
                if (Objects.isNull(users)) {
                    users = new HashSet<>();
                    context.setAttribute(ONLINE_USER_KEY, users);
                }
                users.add((User) event.getValue());
            }
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ServletContext context = se.getSession().getServletContext();
        Set<User> users = (Set<User>) context.getAttribute(ONLINE_USER_KEY);
        if (!Objects.isNull(users)) {
            users.remove(se.getSession().getAttribute(SESSION_KEY));
        }
    }
}
