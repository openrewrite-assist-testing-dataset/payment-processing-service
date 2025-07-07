package com.example.payment.client;

import com.example.payment.common.Payment;
import com.example.payment.common.PaymentStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class PaymentClient {
    
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String authToken;
    
    public PaymentClient(String baseUrl, String authToken) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.authToken = authToken;
        this.objectMapper = new ObjectMapper();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    
    public Payment createPayment(String merchantId, BigDecimal amount, String currency, String description) throws PaymentClientException {
        Payment payment = new Payment(merchantId, amount, currency, description);
        
        try {
            String json = objectMapper.writeValueAsString(payment);
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
            
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v1/payments")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + authToken)
                    .addHeader("Content-Type", "application/json")
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new PaymentClientException("Failed to create payment: " + response.code() + " " + response.message());
                }
                
                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, Payment.class);
            }
        } catch (IOException e) {
            throw new PaymentClientException("Error creating payment", e);
        }
    }
    
    public Optional<Payment> getPayment(String paymentId) throws PaymentClientException {
        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v1/payments/" + paymentId)
                    .get()
                    .addHeader("Authorization", "Bearer " + authToken)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() == 404) {
                    return Optional.empty();
                }
                
                if (!response.isSuccessful()) {
                    throw new PaymentClientException("Failed to get payment: " + response.code() + " " + response.message());
                }
                
                String responseBody = response.body().string();
                Payment payment = objectMapper.readValue(responseBody, Payment.class);
                return Optional.of(payment);
            }
        } catch (IOException e) {
            throw new PaymentClientException("Error getting payment", e);
        }
    }
    
    public List<Payment> getPaymentsByMerchant(String merchantId) throws PaymentClientException {
        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v1/payments/merchant/" + merchantId)
                    .get()
                    .addHeader("Authorization", "Bearer " + authToken)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new PaymentClientException("Failed to get payments by merchant: " + response.code() + " " + response.message());
                }
                
                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, new TypeReference<List<Payment>>() {});
            }
        } catch (IOException e) {
            throw new PaymentClientException("Error getting payments by merchant", e);
        }
    }
    
    public Payment processPayment(String paymentId, String paymentMethodId) throws PaymentClientException {
        try {
            String json = objectMapper.writeValueAsString(new ProcessPaymentRequest(paymentMethodId));
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
            
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v1/payments/" + paymentId + "/process")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + authToken)
                    .addHeader("Content-Type", "application/json")
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new PaymentClientException("Failed to process payment: " + response.code() + " " + response.message());
                }
                
                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, Payment.class);
            }
        } catch (IOException e) {
            throw new PaymentClientException("Error processing payment", e);
        }
    }
    
    public Payment cancelPayment(String paymentId) throws PaymentClientException {
        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v1/payments/" + paymentId + "/cancel")
                    .post(RequestBody.create("", MediaType.get("application/json")))
                    .addHeader("Authorization", "Bearer " + authToken)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new PaymentClientException("Failed to cancel payment: " + response.code() + " " + response.message());
                }
                
                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, Payment.class);
            }
        } catch (IOException e) {
            throw new PaymentClientException("Error cancelling payment", e);
        }
    }
    
    public Payment refundPayment(String paymentId, BigDecimal refundAmount) throws PaymentClientException {
        try {
            String json = objectMapper.writeValueAsString(new RefundRequest(refundAmount));
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
            
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v1/payments/" + paymentId + "/refund")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + authToken)
                    .addHeader("Content-Type", "application/json")
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new PaymentClientException("Failed to refund payment: " + response.code() + " " + response.message());
                }
                
                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, Payment.class);
            }
        } catch (IOException e) {
            throw new PaymentClientException("Error refunding payment", e);
        }
    }
    
    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
    
    private static class ProcessPaymentRequest {
        public final String paymentMethodId;
        
        public ProcessPaymentRequest(String paymentMethodId) {
            this.paymentMethodId = paymentMethodId;
        }
    }
    
    private static class RefundRequest {
        public final BigDecimal amount;
        
        public RefundRequest(BigDecimal amount) {
            this.amount = amount;
        }
    }
    
    public static class PaymentClientException extends Exception {
        public PaymentClientException(String message) {
            super(message);
        }
        
        public PaymentClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}