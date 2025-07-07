package com.example.payment.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Payment {
    
    @JsonProperty
    private String id;
    
    @JsonProperty
    private String merchantId;
    
    @JsonProperty
    private BigDecimal amount;
    
    @JsonProperty
    private String currency;
    
    @JsonProperty
    private PaymentStatus status;
    
    @JsonProperty
    private String description;
    
    @JsonProperty
    private String customerId;
    
    @JsonProperty
    private String paymentMethodId;
    
    @JsonProperty
    private LocalDateTime createdAt;
    
    @JsonProperty
    private LocalDateTime updatedAt;
    
    @JsonProperty
    private String transactionId;
    
    public Payment() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }
    
    public Payment(String merchantId, BigDecimal amount, String currency, String description) {
        this();
        this.merchantId = merchantId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
        this.updatedAt = LocalDateTime.now();
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPaymentMethodId() {
        return paymentMethodId;
    }
    
    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id) &&
               Objects.equals(merchantId, payment.merchantId) &&
               Objects.equals(amount, payment.amount) &&
               Objects.equals(currency, payment.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, merchantId, amount, currency);
    }
    
    @Override
    public String toString() {
        return "Payment{" +
               "id='" + id + '\'' +
               ", merchantId='" + merchantId + '\'' +
               ", amount=" + amount +
               ", currency='" + currency + '\'' +
               ", status=" + status +
               ", customerId='" + customerId + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}