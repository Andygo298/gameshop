package com.github.andygo298.gameshop.dao.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class DataSourceSettings {

    @Value("${url}")
    private String url;

    @Value("${nameAdmin}")
    private String nameAdmin;

    @Value("${password}")
    private String password;

    @Value("${driver}")
    private String driver;
}
