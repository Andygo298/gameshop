package com.github.andygo298.gameshop.dao.config;

import org.springframework.beans.factory.annotation.Value;

public class DataSourceSettings {

    @Value("${url}")
    private String url;

    @Value("${nameAdmin}")
    private String nameAdmin;

    @Value("${password}")
    private String password;

    @Value("${driver}")
    private String driver;

    public String getUrl() {
        return url;
    }

    public String getNameAdmin() {
        return nameAdmin;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }
}
