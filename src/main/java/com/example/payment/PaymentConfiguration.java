package com.example.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class PaymentConfiguration extends Configuration {
    
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();
    
    @Valid
    @NotNull
    private OAuth2Configuration oauth2 = new OAuth2Configuration();
    
    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
    
    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory database) {
        this.database = database;
    }
    
    @JsonProperty("oauth2")
    public OAuth2Configuration getOauth2() {
        return oauth2;
    }
    
    @JsonProperty("oauth2")
    public void setOauth2(OAuth2Configuration oauth2) {
        this.oauth2 = oauth2;
    }
    
    public static class OAuth2Configuration {
        @NotNull
        private String jwtSecret = "payment-service-secret-key-change-in-production";
        
        @NotNull 
        private String issuer = "payment-service";
        
        private long expirationTimeInMinutes = 60;
        
        @JsonProperty
        public String getJwtSecret() {
            return jwtSecret;
        }
        
        @JsonProperty
        public void setJwtSecret(String jwtSecret) {
            this.jwtSecret = jwtSecret;
        }
        
        @JsonProperty
        public String getIssuer() {
            return issuer;
        }
        
        @JsonProperty
        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }
        
        @JsonProperty
        public long getExpirationTimeInMinutes() {
            return expirationTimeInMinutes;
        }
        
        @JsonProperty
        public void setExpirationTimeInMinutes(long expirationTimeInMinutes) {
            this.expirationTimeInMinutes = expirationTimeInMinutes;
        }
    }
}