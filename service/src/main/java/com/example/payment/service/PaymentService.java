package com.example.payment.service;

import com.example.payment.common.Payment;
import com.example.payment.common.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PaymentService {
    
    private final PaymentDAO paymentDAO;
    
    public PaymentService(PaymentDAO paymentDAO) {
        this.paymentDAO = paymentDAO;
    }
    
    public Payment createPayment(String merchantId, BigDecimal amount, String currency, String description) {
        validatePaymentRequest(merchantId, amount, currency);
        
        Payment payment = new Payment(merchantId, amount, currency, description);
        return paymentDAO.create(payment);
    }
    
    public Optional<Payment> getPayment(String paymentId) {
        return paymentDAO.findById(paymentId);
    }
    
    public List<Payment> getPaymentsByMerchant(String merchantId) {
        return paymentDAO.findByMerchantId(merchantId);
    }
    
    public List<Payment> getPaymentsByCustomer(String customerId) {
        return paymentDAO.findByCustomerId(customerId);
    }
    
    public Payment processPayment(String paymentId, String paymentMethodId) {
        Optional<Payment> paymentOpt = paymentDAO.findById(paymentId);
        if (!paymentOpt.isPresent()) {
            throw new PaymentNotFoundException("Payment not found: " + paymentId);
        }
        
        Payment payment = paymentOpt.get();
        
        if (!payment.getStatus().canTransitionTo(PaymentStatus.PROCESSING)) {
            throw new InvalidPaymentStatusException("Cannot process payment in status: " + payment.getStatus());
        }
        
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setPaymentMethodId(paymentMethodId);
        payment.setTransactionId(UUID.randomUUID().toString());
        
        paymentDAO.update(payment);
        
        // Simulate payment processing
        boolean processingSuccess = simulatePaymentProcessing(payment);
        
        if (processingSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
        
        return paymentDAO.update(payment);
    }
    
    public Payment cancelPayment(String paymentId) {
        Optional<Payment> paymentOpt = paymentDAO.findById(paymentId);
        if (!paymentOpt.isPresent()) {
            throw new PaymentNotFoundException("Payment not found: " + paymentId);
        }
        
        Payment payment = paymentOpt.get();
        
        if (!payment.getStatus().canTransitionTo(PaymentStatus.CANCELLED)) {
            throw new InvalidPaymentStatusException("Cannot cancel payment in status: " + payment.getStatus());
        }
        
        payment.setStatus(PaymentStatus.CANCELLED);
        return paymentDAO.update(payment);
    }
    
    public Payment refundPayment(String paymentId, BigDecimal refundAmount) {
        Optional<Payment> paymentOpt = paymentDAO.findById(paymentId);
        if (!paymentOpt.isPresent()) {
            throw new PaymentNotFoundException("Payment not found: " + paymentId);
        }
        
        Payment payment = paymentOpt.get();
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new InvalidPaymentStatusException("Can only refund completed payments");
        }
        
        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException("Refund amount cannot exceed original payment amount");
        }
        
        // For simplicity, we'll mark the payment as refunded
        // In a real system, you'd create a separate refund record
        payment.setStatus(PaymentStatus.REFUNDED);
        return paymentDAO.update(payment);
    }
    
    private void validatePaymentRequest(String merchantId, BigDecimal amount, String currency) {
        if (merchantId == null || merchantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Merchant ID is required");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency is required");
        }
        
        if (!isValidCurrency(currency)) {
            throw new IllegalArgumentException("Invalid currency: " + currency);
        }
    }
    
    private boolean isValidCurrency(String currency) {
        // Simple validation for common currencies
        return currency.matches("^(USD|EUR|GBP|JPY|CAD|AUD|CHF|CNY)$");
    }
    
    private boolean simulatePaymentProcessing(Payment payment) {
        // Simulate payment processing with 90% success rate
        // In a real system, this would integrate with payment processors
        return Math.random() > 0.1;
    }
    
    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class InvalidPaymentStatusException extends RuntimeException {
        public InvalidPaymentStatusException(String message) {
            super(message);
        }
    }
}