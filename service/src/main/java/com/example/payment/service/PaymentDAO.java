package com.example.payment.service;

import com.example.payment.common.Payment;
import com.example.payment.common.PaymentStatus;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(Payment.class)
public interface PaymentDAO {
    
    @SqlUpdate("INSERT INTO payments (id, merchant_id, amount, currency, status, description, customer_id, payment_method_id, created_at, updated_at, transaction_id) " +
               "VALUES (:id, :merchantId, :amount, :currency, :status, :description, :customerId, :paymentMethodId, :createdAt, :updatedAt, :transactionId)")
    @GetGeneratedKeys
    Payment create(@BindBean Payment payment);
    
    @SqlQuery("SELECT * FROM payments WHERE id = :id")
    Optional<Payment> findById(@Bind("id") String id);
    
    @SqlQuery("SELECT * FROM payments WHERE merchant_id = :merchantId ORDER BY created_at DESC")
    List<Payment> findByMerchantId(@Bind("merchantId") String merchantId);
    
    @SqlQuery("SELECT * FROM payments WHERE customer_id = :customerId ORDER BY created_at DESC")
    List<Payment> findByCustomerId(@Bind("customerId") String customerId);
    
    @SqlQuery("SELECT * FROM payments WHERE status = :status ORDER BY created_at DESC")
    List<Payment> findByStatus(@Bind("status") PaymentStatus status);
    
    @SqlQuery("SELECT * FROM payments ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    List<Payment> findAll(@Bind("limit") int limit, @Bind("offset") int offset);
    
    @SqlUpdate("UPDATE payments SET merchant_id = :merchantId, amount = :amount, currency = :currency, " +
               "status = :status, description = :description, customer_id = :customerId, " +
               "payment_method_id = :paymentMethodId, updated_at = :updatedAt, transaction_id = :transactionId " +
               "WHERE id = :id")
    Payment update(@BindBean Payment payment);
    
    @SqlUpdate("DELETE FROM payments WHERE id = :id")
    void delete(@Bind("id") String id);
    
    @SqlQuery("SELECT COUNT(*) FROM payments WHERE merchant_id = :merchantId")
    int countByMerchantId(@Bind("merchantId") String merchantId);
    
    @SqlQuery("SELECT COUNT(*) FROM payments WHERE status = :status")
    int countByStatus(@Bind("status") PaymentStatus status);
}