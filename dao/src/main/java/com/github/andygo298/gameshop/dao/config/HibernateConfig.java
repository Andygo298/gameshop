package com.github.andygo298.gameshop.dao.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;


import javax.sql.DataSource;

@Configuration
@Import(SettingsConfig.class)
public class HibernateConfig {

    private final SettingsConfig settingsConfig;

    public HibernateConfig(SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
    }

    @Bean
    public DataSource dataSource() {
        final DataSourceSettings dataSourceSettings = settingsConfig.dataSourceSettings();
//hikari:
        final HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(dataSourceSettings.getUrl());
        hikariDataSource.setUsername(dataSourceSettings.getNameAdmin());
        hikariDataSource.setPassword(dataSourceSettings.getPassword());
        hikariDataSource.setDriverClassName(dataSourceSettings.getDriver());
        hikariDataSource.setMaximumPoolSize(20);
        return hikariDataSource;

    }

    @Bean(destroyMethod = "destroy")
    public LocalSessionFactoryBean entityManagerFactory() {
        final LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
        sfb.setDataSource(dataSource());
        sfb.setPackagesToScan("com.github.andygo298.gameshop.model.entity");
        sfb.setHibernateProperties(settingsConfig.hibernateProperties());
        return sfb;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}
