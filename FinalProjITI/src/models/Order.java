package models;

import enums.OrderStatus;
import java.util.Date;

public class Order {
    private String orderId;
    private Student student;
    private OrderStatus status;
    private Date orderDate;
    private OrderItems orderItems;
    private OrderCalculator orderCalculator;
    private OrderConfirmation orderConfirmation;

    public Order(String orderId, Student student) {
        this.orderId = orderId;
        this.student = student;
        this.status = OrderStatus.PENDING;
        this.orderDate = new Date();
        this.orderItems = new OrderItems(this);
        this.orderCalculator = new OrderCalculator(this, orderItems);
        this.orderConfirmation = new OrderConfirmation(this);
    }

    public void changeStatus(OrderStatus newStatus) {
        this.status = newStatus;
        System.out.println("Order " + orderId + " status changed to: " + newStatus);
    }

    public OrderStatus getStatus() {
        return status;
    }

    // Delegation methods
    public void addItem(MenuItem item) {
        orderItems.addItem(item);
    }

    public boolean removeItem(String itemId) {
        return orderItems.removeItem(itemId);
    }

    public double calculateTotal() {
        return orderCalculator.calculateTotal();
    }

    public boolean confirmOrder() {
        return orderConfirmation.confirmOrder();
    }

    // Getters
    public String getOrderId() { return orderId; }
    public Student getStudent() { return student; }
    public Date getOrderDate() { return orderDate; }
    public OrderItems getOrderItems() { return orderItems; }
    public OrderCalculator getOrderCalculator() { return orderCalculator; }

    @Override
    public String toString() {
        return "Order #" + orderId + " - " + student.getName() + " - " + status + " - Total: $" + calculateTotal();
    }
}