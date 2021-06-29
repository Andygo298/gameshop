package com.github.andygo298.gameshop.web.config;

import com.github.andygo298.gameshop.dao.config.DaoConfig;
import com.github.andygo298.gameshop.service.config.ServiceConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.Filter;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
                WebSecurityConfig.class,
                RootConfig.class,
                ServiceConfig.class,
                DaoConfig.class,

        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{
                WebConfig.class,
                SwaggerConfig.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        DelegatingFilterProxy delegateFilterProxy = new DelegatingFilterProxy();
        delegateFilterProxy.setTargetBeanName("springSecurityFilterChain");
        return new Filter[]{delegateFilterProxy};
    }
}
