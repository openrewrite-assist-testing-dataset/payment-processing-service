package com.example.payment;

import com.example.payment.api.PaymentResource;
import com.example.payment.auth.OAuth2Authenticator;
import com.example.payment.service.PaymentDAO;
import com.example.payment.service.PaymentService;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jdbi.v3.core.Jdbi;

import java.security.Principal;

public class PaymentApplication extends Application<PaymentConfiguration> {
    
    public static void main(String[] args) throws Exception {
        new PaymentApplication().run(args);
    }
    
    @Override
    public String getName() {
        return "payment-processing-service";
    }
    
    @Override
    public void initialize(Bootstrap<PaymentConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<PaymentConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(PaymentConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }
    
    @Override
    public void run(PaymentConfiguration configuration, Environment environment) {
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");
        final PaymentDAO paymentDAO = jdbi.onDemand(PaymentDAO.class);
        final PaymentService paymentService = new PaymentService(paymentDAO);
        final PaymentResource paymentResource = new PaymentResource(paymentService);
        
        // Register resources
        environment.jersey().register(paymentResource);
        
        // Register OAuth2 authentication
        environment.jersey().register(new AuthDynamicFeature(
            new OAuthCredentialAuthFilter.Builder<Principal>()
                .setAuthenticator(new OAuth2Authenticator())
                .setPrefix("Bearer")
                .buildAuthFilter()));
        
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Principal.class));
        
        // Health checks
        environment.healthChecks().register("database", 
            new DatabaseHealthCheck(configuration.getDataSourceFactory()));
    }
}