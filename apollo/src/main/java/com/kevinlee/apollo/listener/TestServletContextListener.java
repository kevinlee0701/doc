package com.kevinlee.apollo.listener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @ClassName TestServletContextListener
 * @Author kevinlee
 * @Date 2022/4/1 11:00
 * @Version 1.0
 **/
@WebListener
public class TestServletContextListener implements ServletContextListener {

    private ApplicationContext applicationContext;

    private ServletContext servletContext;
    @Override
    public void contextInitialized(ServletContextEvent event) {
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        servletContext =event.getServletContext();
        servletContext.setAttribute("version", "version");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
