-- Create payments table
CREATE TABLE payments (
    id VARCHAR(255) PRIMARY KEY,
    merchant_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(50) NOT NULL,
    description TEXT,
    customer_id VARCHAR(255),
    payment_method_id VARCHAR(255),
    transaction_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create indexes for better performance
CREATE INDEX idx_payments_merchant_id ON payments(merchant_id);
CREATE INDEX idx_payments_customer_id ON payments(customer_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at);
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);

-- Insert some sample data
INSERT INTO payments (id, merchant_id, amount, currency, status, description, created_at, updated_at) VALUES
('payment-1', 'merchant-123', 100.00, 'USD', 'completed', 'Test payment 1', NOW(), NOW()),
('payment-2', 'merchant-123', 250.50, 'USD', 'pending', 'Test payment 2', NOW(), NOW()),
('payment-3', 'merchant-456', 75.25, 'EUR', 'failed', 'Test payment 3', NOW(), NOW());