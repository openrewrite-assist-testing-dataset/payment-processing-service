package com.example.payment;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.db.DataSourceFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseHealthCheck extends HealthCheck {
    
    private final DataSourceFactory dataSourceFactory;
    
    public DatabaseHealthCheck(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }
    
    @Override
    protected Result check() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                dataSourceFactory.getUrl(),
                dataSourceFactory.getUser(),
                dataSourceFactory.getPassword())) {
            
            try (PreparedStatement statement = connection.prepareStatement("SELECT 1");
                 ResultSet resultSet = statement.executeQuery()) {
                
                if (resultSet.next() && resultSet.getInt(1) == 1) {
                    return Result.healthy("Database connection is working");
                } else {
                    return Result.unhealthy("Database query returned unexpected result");
                }
            }
        } catch (Exception e) {
            return Result.unhealthy("Database connection failed: " + e.getMessage());
        }
    }
}