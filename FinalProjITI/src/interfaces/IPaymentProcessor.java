package interfaces;

import models.Payment;

public interface IPaymentProcessor {
    boolean processPayment(Payment payment);
    boolean refundPayment(String paymentId);
}