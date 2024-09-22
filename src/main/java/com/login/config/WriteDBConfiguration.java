package com.login.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class WriteDBConfiguration {
    
    @Autowired
    private Environment env;
    
    @Bean
    @Primary
    public DataSource dataSource1()
    {
        DriverManagerDataSource datasource =new DriverManagerDataSource();
        datasource.setDriverClassName(env.getProperty("spring.writeDatasource.driverClassName"));
        datasource.setUrl(env.getProperty("spring.writeDatasource.url"));
        datasource.setUsername(env.getProperty("spring.writeDatasource.username"));
        datasource.setPassword(env.getProperty("spring.writeDatasource.password"));
                      
        return datasource;        
    }
    
    @Bean(name = "writeJdbc")
    @Primary
    public JdbcTemplate jdbcTemplate1(@Qualifier("dataSource1") DataSource ds)
    {
        return new JdbcTemplate(ds);
        
    }
    
  

}
