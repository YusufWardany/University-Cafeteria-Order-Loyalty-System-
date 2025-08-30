package models;

import enums.PaymentMethod;
import enums.PaymentStatus;
import java.util.Date;

public class Payment {
    private String paymentId;
    private Order order;
    private double amount;
    private Date paymentDate;
    private PaymentStatus status;
    private PaymentMethod method;

    public Payment(String paymentId, Order order, PaymentMethod method) {
        this.paymentId = paymentId;
        this.order = order;
        this.amount = order.getOrderCalculator().calculateTotal();
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }

    public boolean processPayment() {
        this.status = PaymentStatus.PROCESSING;
        System.out.println("Processing payment " + paymentId + " for order " + order.getOrderId());

        // Simulate payment processing
        try {
            Thread.sleep(1000); // Simulate processing time
            this.status = PaymentStatus.COMPLETED;
            this.paymentDate = new Date();
            System.out.println("Payment " + paymentId + " completed successfully");
            return true;
        } catch (InterruptedException e) {
            this.status = PaymentStatus.FAILED;
            System.out.println("Payment " + paymentId + " failed");
            return false;
        }
    }

    public boolean refundPayment() {
        if (this.status != PaymentStatus.COMPLETED) {
            System.out.println("Cannot refund payment that hasn't been completed");
            return false;
        }

        this.status = PaymentStatus.REFUNDED;
        System.out.println("Payment " + paymentId + " refunded");
        return true;
    }

    public String getPaymentDetails() {
        return "Payment ID: " + paymentId +
                "\nOrder ID: " + order.getOrderId() +
                "\nAmount: $" + amount +
                "\nMethod: " + method +
                "\nStatus: " + status +
                "\nDate: " + paymentDate;
    }

    public boolean isPaymentSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public PaymentMethod getMethod() { return method; }
    public Date getPaymentDate() { return paymentDate; }
}