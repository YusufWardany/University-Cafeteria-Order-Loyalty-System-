package services;

import interfaces.IPaymentProcessor;
import models.Payment;

public class PaymentProcessor implements IPaymentProcessor {
    @Override
    public boolean processPayment(Payment payment) {
        return payment.processPayment();
    }

    @Override
    public boolean refundPayment(String paymentId) {
        // This would look up the payment by ID in a real application
        System.out.println("Processing refund for payment ID: " + paymentId);
        return true;
    }
}