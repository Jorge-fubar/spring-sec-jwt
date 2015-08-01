package org.jed.playground.auth.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@PropertySource("classpath:/authentication-datasource.properties")
@EnableJpaRepositories(entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef = "authTransactionManager")
public class AuthDbConfiguration {

    @Autowired
    private Environment env;

    @Bean
    PlatformTransactionManager authTransactionManager() {
        return new JpaTransactionManager(authEntityManagerFactory().getObject());
    }


    @Bean
    LocalContainerEntityManagerFactoryBean authEntityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        factoryBean.setDataSource(authDataSource());
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        // Funny thing to investigate: if this class is not in the same package as the repository classes
        // they will not be scanned, doesn't matter if you specify the package as here, or as done in DataConfiguration class
        factoryBean.setPackagesToScan("org.jed.playground.auth.model");
        factoryBean.setJpaProperties(additionalProperties());
        return factoryBean;
    }

    @Bean
    public DataSource authDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("authentication.datasource.driver"));
        dataSource.setUrl(env.getProperty("authentication.datasource.url"));
        dataSource.setUsername(env.getProperty("authentication.datasource.username"));
        dataSource.setPassword(env.getProperty("authentication.datasource.password"));
        return dataSource;
    }

    Properties additionalProperties() {
        return new Properties() {
            {
                setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
                setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
                setProperty("hibernate.globally_quoted_identifiers", "true");
            }
        };
    }

}
