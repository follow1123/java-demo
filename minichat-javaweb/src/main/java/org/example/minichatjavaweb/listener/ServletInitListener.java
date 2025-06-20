package org.example.minichatjavaweb.listener;

import org.example.minichatjavaweb.repository.UserRepository;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServletInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("---------- servlet context init ----------");
        sce.getServletContext().setAttribute(UserRepository.KEY, new UserRepository());
    }
}
