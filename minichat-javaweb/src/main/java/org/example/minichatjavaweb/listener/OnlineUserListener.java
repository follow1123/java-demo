package org.example.minichatjavaweb.listener;

import org.example.minichatjavaweb.constants.UserConstant;
import org.example.minichatjavaweb.entities.User;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@WebListener
public class OnlineUserListener implements HttpSessionListener, HttpSessionAttributeListener {


    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (Objects.equals(UserConstant.SESSION_KEY, event.getName())) {
            ServletContext context = event.getSession().getServletContext();
            synchronized (this) {
                Set<User> users = (Set<User>) context.getAttribute(UserConstant.ONLINE_USER_KEY);
                if (Objects.isNull(users)) {
                    users = new HashSet<>();
                    context.setAttribute(UserConstant.ONLINE_USER_KEY, users);
                }
                users.add((User) event.getValue());
            }
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ServletContext context = se.getSession().getServletContext();
        Set<User> users = (Set<User>) context.getAttribute(UserConstant.ONLINE_USER_KEY);
        if (!Objects.isNull(users)) {
            users.remove(se.getSession().getAttribute(UserConstant.SESSION_KEY));
        }
    }
}
