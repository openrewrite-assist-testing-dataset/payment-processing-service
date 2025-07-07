package com.example.payment;

import com.example.payment.api.PaymentResource;
import com.example.payment.common.Payment;
import com.example.payment.common.PaymentStatus;
import com.example.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentResourceTest {
    
    @Mock
    private PaymentService paymentService;
    
    @Mock
    private Principal principal;
    
    private PaymentResource paymentResource;
    
    @BeforeEach
    void setUp() {
        paymentResource = new PaymentResource(paymentService);
    }
    
    @Test
    void testCreatePayment() {
        // Given
        PaymentResource.CreatePaymentRequest request = new PaymentResource.CreatePaymentRequest();
        request.setMerchantId("merchant-123");
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("USD");
        request.setDescription("Test payment");
        
        Payment createdPayment = new Payment("merchant-123", new BigDecimal("100.00"), "USD", "Test payment");
        when(paymentService.createPayment(anyString(), any(BigDecimal.class), anyString(), anyString()))
                .thenReturn(createdPayment);
        
        // When
        Response response = paymentResource.createPayment(principal, request);
        
        // Then
        assertEquals(201, response.getStatus());
        Payment returnedPayment = (Payment) response.getEntity();
        assertEquals("merchant-123", returnedPayment.getMerchantId());
        assertEquals(new BigDecimal("100.00"), returnedPayment.getAmount());
        verify(paymentService, times(1)).createPayment("merchant-123", new BigDecimal("100.00"), "USD", "Test payment");
    }
    
    @Test
    void testCreatePaymentWithInvalidData() {
        // Given
        PaymentResource.CreatePaymentRequest request = new PaymentResource.CreatePaymentRequest();
        request.setMerchantId("");
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("USD");
        request.setDescription("Test payment");
        
        when(paymentService.createPayment(anyString(), any(BigDecimal.class), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Merchant ID is required"));
        
        // When
        Response response = paymentResource.createPayment(principal, request);
        
        // Then
        assertEquals(400, response.getStatus());
        PaymentResource.ErrorResponse error = (PaymentResource.ErrorResponse) response.getEntity();
        assertEquals("Merchant ID is required", error.getError());
    }
    
    @Test
    void testGetPayment() {
        // Given
        String paymentId = "payment-123";
        Payment payment = new Payment("merchant-123", new BigDecimal("100.00"), "USD", "Test payment");
        when(paymentService.getPayment(paymentId)).thenReturn(Optional.of(payment));
        
        // When
        Response response = paymentResource.getPayment(principal, paymentId);
        
        // Then
        assertEquals(200, response.getStatus());
        Payment returnedPayment = (Payment) response.getEntity();
        assertEquals("merchant-123", returnedPayment.getMerchantId());
        verify(paymentService, times(1)).getPayment(paymentId);
    }
    
    @Test
    void testGetPaymentNotFound() {
        // Given
        String paymentId = "nonexistent-payment";
        when(paymentService.getPayment(paymentId)).thenReturn(Optional.empty());
        
        // When
        Response response = paymentResource.getPayment(principal, paymentId);
        
        // Then
        assertEquals(404, response.getStatus());
        verify(paymentService, times(1)).getPayment(paymentId);
    }
    
    @Test
    void testGetPaymentsByMerchant() {
        // Given
        String merchantId = "merchant-123";
        Payment payment1 = new Payment(merchantId, new BigDecimal("100.00"), "USD", "Payment 1");
        Payment payment2 = new Payment(merchantId, new BigDecimal("200.00"), "USD", "Payment 2");
        List<Payment> payments = Arrays.asList(payment1, payment2);
        when(paymentService.getPaymentsByMerchant(merchantId)).thenReturn(payments);
        
        // When
        List<Payment> result = paymentResource.getPaymentsByMerchant(principal, merchantId);
        
        // Then
        assertEquals(2, result.size());
        assertEquals(merchantId, result.get(0).getMerchantId());
        assertEquals(merchantId, result.get(1).getMerchantId());
        verify(paymentService, times(1)).getPaymentsByMerchant(merchantId);
    }
    
    @Test
    void testProcessPayment() {
        // Given
        String paymentId = "payment-123";
        PaymentResource.ProcessPaymentRequest request = new PaymentResource.ProcessPaymentRequest();
        request.setPaymentMethodId("pm-123");
        
        Payment processedPayment = new Payment("merchant-123", new BigDecimal("100.00"), "USD", "Test payment");
        processedPayment.setStatus(PaymentStatus.COMPLETED);
        when(paymentService.processPayment(paymentId, "pm-123")).thenReturn(processedPayment);
        
        // When
        Response response = paymentResource.processPayment(principal, paymentId, request);
        
        // Then
        assertEquals(200, response.getStatus());
        Payment returnedPayment = (Payment) response.getEntity();
        assertEquals(PaymentStatus.COMPLETED, returnedPayment.getStatus());
        verify(paymentService, times(1)).processPayment(paymentId, "pm-123");
    }
    
    @Test
    void testProcessPaymentNotFound() {
        // Given
        String paymentId = "nonexistent-payment";
        PaymentResource.ProcessPaymentRequest request = new PaymentResource.ProcessPaymentRequest();
        request.setPaymentMethodId("pm-123");
        
        when(paymentService.processPayment(paymentId, "pm-123"))
                .thenThrow(new PaymentService.PaymentNotFoundException("Payment not found"));
        
        // When
        Response response = paymentResource.processPayment(principal, paymentId, request);
        
        // Then
        assertEquals(404, response.getStatus());
        PaymentResource.ErrorResponse error = (PaymentResource.ErrorResponse) response.getEntity();
        assertEquals("Payment not found", error.getError());
    }
    
    @Test
    void testCancelPayment() {
        // Given
        String paymentId = "payment-123";
        Payment cancelledPayment = new Payment("merchant-123", new BigDecimal("100.00"), "USD", "Test payment");
        cancelledPayment.setStatus(PaymentStatus.CANCELLED);
        when(paymentService.cancelPayment(paymentId)).thenReturn(cancelledPayment);
        
        // When
        Response response = paymentResource.cancelPayment(principal, paymentId);
        
        // Then
        assertEquals(200, response.getStatus());
        Payment returnedPayment = (Payment) response.getEntity();
        assertEquals(PaymentStatus.CANCELLED, returnedPayment.getStatus());
        verify(paymentService, times(1)).cancelPayment(paymentId);
    }
    
    @Test
    void testRefundPayment() {
        // Given
        String paymentId = "payment-123";
        PaymentResource.RefundRequest request = new PaymentResource.RefundRequest();
        request.setAmount(new BigDecimal("50.00"));
        
        Payment refundedPayment = new Payment("merchant-123", new BigDecimal("100.00"), "USD", "Test payment");
        refundedPayment.setStatus(PaymentStatus.REFUNDED);
        when(paymentService.refundPayment(paymentId, new BigDecimal("50.00"))).thenReturn(refundedPayment);
        
        // When
        Response response = paymentResource.refundPayment(principal, paymentId, request);
        
        // Then
        assertEquals(200, response.getStatus());
        Payment returnedPayment = (Payment) response.getEntity();
        assertEquals(PaymentStatus.REFUNDED, returnedPayment.getStatus());
        verify(paymentService, times(1)).refundPayment(paymentId, new BigDecimal("50.00"));
    }
}