package com.example.payment.api;

import com.example.payment.common.Payment;
import com.example.payment.service.PaymentService;
import io.dropwizard.auth.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Path("/api/v1/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {
    
    private final PaymentService paymentService;
    
    public PaymentResource(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @POST
    @Operation(summary = "Create a new payment")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Payment created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment data"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public Response createPayment(@Auth Principal principal, CreatePaymentRequest request) {
        try {
            Payment payment = paymentService.createPayment(
                request.getMerchantId(),
                request.getAmount(),
                request.getCurrency(),
                request.getDescription()
            );
            return Response.status(Response.Status.CREATED).entity(payment).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/{paymentId}")
    @Operation(summary = "Get payment by ID")
    public Response getPayment(@Auth Principal principal, @PathParam("paymentId") String paymentId) {
        Optional<Payment> payment = paymentService.getPayment(paymentId);
        if (payment.isPresent()) {
            return Response.ok(payment.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    @GET
    @Path("/merchant/{merchantId}")
    @Operation(summary = "Get payments by merchant ID")
    public List<Payment> getPaymentsByMerchant(@Auth Principal principal, @PathParam("merchantId") String merchantId) {
        return paymentService.getPaymentsByMerchant(merchantId);
    }
    
    @POST
    @Path("/{paymentId}/process")
    @Operation(summary = "Process a payment")
    public Response processPayment(@Auth Principal principal, 
                                 @PathParam("paymentId") String paymentId,
                                 ProcessPaymentRequest request) {
        try {
            Payment payment = paymentService.processPayment(paymentId, request.getPaymentMethodId());
            return Response.ok(payment).build();
        } catch (PaymentService.PaymentNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (PaymentService.InvalidPaymentStatusException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @POST
    @Path("/{paymentId}/cancel")
    @Operation(summary = "Cancel a payment")
    public Response cancelPayment(@Auth Principal principal, @PathParam("paymentId") String paymentId) {
        try {
            Payment payment = paymentService.cancelPayment(paymentId);
            return Response.ok(payment).build();
        } catch (PaymentService.PaymentNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (PaymentService.InvalidPaymentStatusException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @POST
    @Path("/{paymentId}/refund")
    @Operation(summary = "Refund a payment")
    public Response refundPayment(@Auth Principal principal,
                                @PathParam("paymentId") String paymentId,
                                RefundRequest request) {
        try {
            Payment payment = paymentService.refundPayment(paymentId, request.getAmount());
            return Response.ok(payment).build();
        } catch (PaymentService.PaymentNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (PaymentService.InvalidPaymentStatusException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    public static class CreatePaymentRequest {
        private String merchantId;
        private BigDecimal amount;
        private String currency;
        private String description;
        
        public String getMerchantId() { return merchantId; }
        public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class ProcessPaymentRequest {
        private String paymentMethodId;
        
        public String getPaymentMethodId() { return paymentMethodId; }
        public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    }
    
    public static class RefundRequest {
        private BigDecimal amount;
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
    
    public static class ErrorResponse {
        private final String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() { return error; }
    }
}